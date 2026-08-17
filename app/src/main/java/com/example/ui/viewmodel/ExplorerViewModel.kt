package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MaintenanceRepository
import com.example.data.SportTracData
import com.example.data.local.AppDatabase
import com.example.data.local.MaintenanceEntity
import com.example.data.local.VehicleProfileEntity
import com.example.model.*
import com.example.ui.components.SnackbarPayload
import com.example.ui.components.SnackbarType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import com.example.data.SportTracPartsCatalog

enum class MainTab {
    VIEW_3D,
    REPAIR_MANUAL,
    DIAGNOSTICS,
    MAINTENANCE,
    PARTS_CART
}

class ExplorerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = MaintenanceRepository(db.maintenanceDao())
    val acousticRepository = com.example.data.AcousticDiagnosticRepository(db.acousticReferenceDao())
    val repairChecklistRepo = com.example.data.RepairChecklistRepository(db.repairChecklistDao())
    val offlineCacheRepo = com.example.data.OfflineCacheRepository(db.offlineCacheDao())

    val cached3DAssetsCount: StateFlow<Int> = offlineCacheRepo.assets3DCountFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val cachedManualsCount: StateFlow<Int> = offlineCacheRepo.manualsCountFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val cachedSymptomsCount: StateFlow<Int> = offlineCacheRepo.symptomsCountFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val cacheManifest: StateFlow<com.example.data.local.CacheManifestEntity?> = offlineCacheRepo.manifestFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = com.example.data.local.CacheManifestEntity()
        )

    fun resyncOfflineCache() {
        viewModelScope.launch {
            offlineCacheRepo.seedAndSyncOfflineCache()
            _voiceNotice.value = "Room DB Offline Cache synced successfully!"
        }
    }

    fun upgradeOfflineCache() {
        viewModelScope.launch {
            val updatedVer = offlineCacheRepo.performUpgradeToLatestVersion()
            _voiceNotice.value = "Room Cache Upgraded to v$updatedVer!"
        }
    }

    fun checkForCacheUpgrades() {
        viewModelScope.launch {
            val isAvail = offlineCacheRepo.checkForUpgrades()
            if (isAvail) {
                _voiceNotice.value = "New CAD & Manual Content v2.5.0 Available for Upgrade!"
            } else {
                _voiceNotice.value = "Room DB content is already at the latest version."
            }
        }
    }

    fun clearOfflineCache() {
        viewModelScope.launch {
            offlineCacheRepo.clearAllOfflineCache()
            _voiceNotice.value = "Room DB Offline Cache cleared."
        }
    }

    val savedChecklists: StateFlow<List<com.example.data.local.RepairChecklistEntity>> = repairChecklistRepo.allSavedChecklists
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveRepairChecklistProgress(
        componentId: String,
        componentName: String,
        currentStepIndex: Int,
        completedIndices: Set<Int>,
        totalSteps: Int
    ) {
        viewModelScope.launch {
            repairChecklistRepo.saveProgress(
                componentId = componentId,
                componentName = componentName,
                currentStepIndex = currentStepIndex,
                completedIndices = completedIndices,
                totalSteps = totalSteps
            )
        }
    }

    fun resetRepairChecklistProgress(componentId: String) {
        viewModelScope.launch {
            repairChecklistRepo.resetProgress(componentId)
        }
    }

    // Skill Level Experience Setting
    private val _skillLevel = MutableStateFlow(com.example.ui.components.SkillLevel.ROOKIE)
    val skillLevel: StateFlow<com.example.ui.components.SkillLevel> = _skillLevel.asStateFlow()

    // Team & Virtual Mentor Panel State
    private val _isTeamPanelOpen = MutableStateFlow(false)
    val isTeamPanelOpen: StateFlow<Boolean> = _isTeamPanelOpen.asStateFlow()

    // Voice Synthesis Settings Dialog State
    private val _isVoiceSettingsOpen = MutableStateFlow(false)
    val isVoiceSettingsOpen: StateFlow<Boolean> = _isVoiceSettingsOpen.asStateFlow()

    fun setSkillLevel(level: com.example.ui.components.SkillLevel) {
        _skillLevel.value = level
        _voiceNotice.value = "Skill level updated to ${level.label}: AI Mentor will adapt repair steps!"
    }

    fun openTeamPanel() {
        _isTeamPanelOpen.value = true
    }

    fun closeTeamPanel() {
        _isTeamPanelOpen.value = false
    }

    fun openVoiceSettings() {
        _isVoiceSettingsOpen.value = true
    }

    fun closeVoiceSettings() {
        _isVoiceSettingsOpen.value = false
    }

    // Active Navigation Tab
    private val _currentTab = MutableStateFlow(MainTab.VIEW_3D)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    // Active System Filter (Engine, Intake, Trans, Cooling, A/C, Elect, Brakes)
    private val _activeSystem = MutableStateFlow(VehicleSystem.ALL)
    val activeSystem: StateFlow<VehicleSystem> = _activeSystem.asStateFlow()

    // Currently Selected 3D Component
    private val _selectedComponent = MutableStateFlow<Component3DModel?>(SportTracData.components.firstOrNull())
    val selectedComponent: StateFlow<Component3DModel?> = _selectedComponent.asStateFlow()

    // Flag to open Component Detail Sheet directly via voice
    private val _requestDetailSheetOpen = MutableStateFlow(false)
    val requestDetailSheetOpen: StateFlow<Boolean> = _requestDetailSheetOpen.asStateFlow()

    // Voice Command Feedback Notice
    private val _voiceNotice = MutableStateFlow<String?>(null)
    val voiceNotice: StateFlow<String?> = _voiceNotice.asStateFlow()

    // Gemini Diagnostic Chatbot Repository & State
    private val geminiRepo = com.example.data.GeminiDiagnosticRepository()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = ChatSender.GEMINI_MECHANIC,
                text = "Hello! I am your Gemini AI Master Mechanic specializing in the 2004 Ford Explorer Sport Trac 4.0L SOHC V6.\n\nDescribe any engine noises, rough idling, transmission flares, coolant leaks, or diagnostic trouble codes (like P0171, P0300, P0128, P0732) to get a targeted diagnostic breakdown and matching 3D repair parts!",
                urgencyLevel = "Monitor / Safe to Drive"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isGeminiThinking = MutableStateFlow(false)
    val isGeminiThinking: StateFlow<Boolean> = _isGeminiThinking.asStateFlow()

    // 3D Model Loading State
    private val _is3DModelLoading = MutableStateFlow(false)
    val is3DModelLoading: StateFlow<Boolean> = _is3DModelLoading.asStateFlow()

    private val _model3DLoadingProgress = MutableStateFlow<Int?>(null)
    val model3DLoadingProgress: StateFlow<Int?> = _model3DLoadingProgress.asStateFlow()

    // Global Snackbar Event State for Error Handling & Notifications
    private val _snackbarEvent = MutableStateFlow<SnackbarPayload?>(null)
    val snackbarEvent: StateFlow<SnackbarPayload?> = _snackbarEvent.asStateFlow()

    // Last Diagnostic Query for Instant Retries
    private val _lastDiagnosticQuery = MutableStateFlow<String?>(null)
    val lastDiagnosticQuery: StateFlow<String?> = _lastDiagnosticQuery.asStateFlow()

    // Search Query for Repair Manual
    private val _manualSearchQuery = MutableStateFlow("")
    val manualSearchQuery: StateFlow<String> = _manualSearchQuery.asStateFlow()

    // Room Database State Flows
    val maintenanceLogs: StateFlow<List<MaintenanceEntity>> = repository.allLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val vehicleProfile: StateFlow<VehicleProfileEntity?> = repository.vehicleProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VehicleProfileEntity()
    )

    val upcomingTasks: StateFlow<List<com.example.data.local.UpcomingTaskEntity>> = repository.upcomingTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Components List filtered by active system and search query
    val filteredComponents: StateFlow<List<Component3DModel>> = combine(
        _activeSystem,
        _manualSearchQuery
    ) { system, query ->
        var list = SportTracData.components
        if (system != VehicleSystem.ALL) {
            list = list.filter { it.system == system }
        }
        if (query.isNotBlank()) {
            list = list.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.oemPartNumber.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.commonSymptoms.any { sym -> sym.contains(query, ignoreCase = true) }
            }
        }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SportTracData.components
    )

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
            acousticRepository.seedInitialDatabaseIfEmpty()
            offlineCacheRepo.seedAndSyncOfflineCache()
        }
    }

    fun setTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun setSystemFilter(system: VehicleSystem) {
        _activeSystem.value = system
    }

    fun selectComponent(component: Component3DModel?) {
        _selectedComponent.value = component
        if (component != null) {
            trigger3DModelLoading(component.name)
        }
    }

    fun selectComponentById(componentId: String) {
        val found = SportTracData.components.find { it.id == componentId }
        if (found != null) {
            _selectedComponent.value = found
            _activeSystem.value = found.system
            _currentTab.value = MainTab.VIEW_3D
            trigger3DModelLoading(found.name)
        }
    }

    fun trigger3DModelLoading(partName: String) {
        viewModelScope.launch {
            _is3DModelLoading.value = true
            _model3DLoadingProgress.value = 15
            delay(150)
            _model3DLoadingProgress.value = 55
            delay(200)
            _model3DLoadingProgress.value = 90
            delay(150)
            _model3DLoadingProgress.value = 100
            delay(100)
            _is3DModelLoading.value = false
            _model3DLoadingProgress.value = null
        }
    }

    fun trigger3DModelError(partName: String, errorMessage: String) {
        showSnackbar(
            SnackbarPayload(
                type = SnackbarType.MODEL_3D_ERROR,
                message = "Failed to load high-res 3D asset for $partName",
                subtext = "$errorMessage. Switched to procedural 3D vector CAD mesh fallback.",
                actionLabel = "Retry Load",
                onAction = {
                    trigger3DModelLoading(partName)
                    showSnackbar(
                        SnackbarPayload(
                            type = SnackbarType.SUCCESS,
                            message = "3D CAD mesh reloaded successfully for $partName",
                            durationMillis = 3000L
                        )
                    )
                }
            )
        )
    }

    fun showSnackbar(payload: SnackbarPayload) {
        _snackbarEvent.value = payload
    }

    fun dismissSnackbar() {
        _snackbarEvent.value = null
    }

    fun retryLastGeminiQuery() {
        val lastQuery = _lastDiagnosticQuery.value
        if (!lastQuery.isNullOrBlank()) {
            dismissSnackbar()
            sendDiagnosticQuery(lastQuery)
        }
    }

    fun setManualSearchQuery(query: String) {
        _manualSearchQuery.value = query
    }

    fun logMaintenance(log: MaintenanceEntity) {
        viewModelScope.launch {
            repository.logMaintenance(log)
            repository.updateVehicleMileage(log.mileageAtService)
        }
    }

    fun updateMaintenanceLog(log: MaintenanceEntity) {
        viewModelScope.launch {
            repository.updateLog(log)
        }
    }

    fun deleteMaintenanceLog(id: Long) {
        viewModelScope.launch {
            repository.deleteLog(id)
        }
    }

    fun updateMileage(newMileage: Int) {
        viewModelScope.launch {
            repository.updateVehicleMileage(newMileage)
        }
    }

    fun clearDetailSheetRequest() {
        _requestDetailSheetOpen.value = false
    }

    fun dismissVoiceNotice() {
        _voiceNotice.value = null
    }

    fun processVoiceCommand(spokenText: String): String {
        val text = spokenText.lowercase().trim()
        val currentList = filteredComponents.value

        val feedback: String = when {
            // System Filters
            text.contains("engine") || text.contains("motor") || text.contains("powertrain") -> {
                setSystemFilter(VehicleSystem.ENGINE)
                setTab(MainTab.VIEW_3D)
                "Filtered 3D model to Engine System"
            }
            text.contains("intake") || text.contains("vacuum") || text.contains("air intake") -> {
                setSystemFilter(VehicleSystem.AIR_INTAKE)
                setTab(MainTab.VIEW_3D)
                "Filtered 3D model to Intake System"
            }
            text.contains("brake") || text.contains("rotor") || text.contains("abs") -> {
                setSystemFilter(VehicleSystem.BRAKES_CHASSIS)
                setTab(MainTab.VIEW_3D)
                "Filtered 3D model to Brake System"
            }
            text.contains("transmission") || text.contains("tranny") || text.contains("gearbox") -> {
                setSystemFilter(VehicleSystem.TRANSMISSION)
                setTab(MainTab.VIEW_3D)
                "Filtered 3D model to Transmission"
            }
            text.contains("cooling") || text.contains("coolant") || text.contains("radiator") -> {
                setSystemFilter(VehicleSystem.COOLING)
                setTab(MainTab.VIEW_3D)
                "Filtered 3D model to Cooling System"
            }
            text.contains("electrical") || text.contains("battery") || text.contains("alternator") || text.contains("wire") -> {
                setSystemFilter(VehicleSystem.ELECTRICAL)
                setTab(MainTab.VIEW_3D)
                "Filtered 3D model to Electrical System"
            }
            text.contains("air condition") || text.contains("climate") || text.contains("a/c") || text.contains("ac") || text.contains("head and air") -> {
                setSystemFilter(VehicleSystem.AIR_CONDITIONING)
                setTab(MainTab.VIEW_3D)
                "Filtered 3D model to Heating & A/C System"
            }
            text.contains("dash") || text.contains("interior") || text.contains("gauge") || text.contains("cluster") || text.contains("airbag") -> {
                setSystemFilter(VehicleSystem.INTERIOR_DASH)
                setTab(MainTab.VIEW_3D)
                "Filtered 3D model to Dash & Interior"
            }
            text.contains("sunroof") || text.contains("roof") || text.contains("back window") -> {
                setSystemFilter(VehicleSystem.SUNROOF_ROOF)
                setTab(MainTab.VIEW_3D)
                "Filtered 3D model to Power Sunroof & Glass"
            }
            text.contains("light") || text.contains("headlight") || text.contains("taillight") || text.contains("fog") -> {
                setSystemFilter(VehicleSystem.LIGHTING_BODY)
                setTab(MainTab.VIEW_3D)
                "Filtered 3D model to Lighting & Body"
            }
            text.contains("drivetrain") || text.contains("4wd") || text.contains("4x4") || text.contains("differential") || text.contains("transfer case") || text.contains("driveshaft") -> {
                setSystemFilter(VehicleSystem.DRIVETRAIN_4WD)
                setTab(MainTab.VIEW_3D)
                "Filtered 3D model to 4WD Drivetrain"
            }
            text.contains("all") || text.contains("reset") || text.contains("show all") -> {
                setSystemFilter(VehicleSystem.ALL)
                setTab(MainTab.VIEW_3D)
                "Reset system filter to Show All"
            }

            // Tab Navigation
            text.contains("3d") || text.contains("model") || text.contains("view model") -> {
                setTab(MainTab.VIEW_3D)
                "Switched to 3D Model view"
            }
            text.contains("manual") || text.contains("repair") || text.contains("specs") -> {
                setTab(MainTab.REPAIR_MANUAL)
                "Switched to Repair Manual"
            }
            text.contains("diagnostic") || text.contains("dtc") || text.contains("forscan") || text.contains("troubleshoot") -> {
                setTab(MainTab.DIAGNOSTICS)
                "Switched to Diagnostics & FORScan"
            }
            text.contains("schedule") || text.contains("maintenance") || text.contains("service") -> {
                setTab(MainTab.MAINTENANCE)
                "Switched to Maintenance Schedule"
            }

            // Next / Previous Component Navigation
            text.contains("next") -> {
                if (currentList.isNotEmpty()) {
                    val currIdx = currentList.indexOfFirst { it.id == selectedComponent.value?.id }
                    val nextIdx = if (currIdx == -1 || currIdx == currentList.lastIndex) 0 else currIdx + 1
                    val nextComp = currentList[nextIdx]
                    selectComponent(nextComp)
                    setTab(MainTab.VIEW_3D)
                    "Selected next part: ${nextComp.name}"
                } else "No components available"
            }
            text.contains("previous") || text.contains("prior") || text.contains("back") -> {
                if (currentList.isNotEmpty()) {
                    val currIdx = currentList.indexOfFirst { it.id == selectedComponent.value?.id }
                    val prevIdx = if (currIdx <= 0) currentList.lastIndex else currIdx - 1
                    val prevComp = currentList[prevIdx]
                    selectComponent(prevComp)
                    setTab(MainTab.VIEW_3D)
                    "Selected previous part: ${prevComp.name}"
                } else "No components available"
            }

            // Open Detail Sheet
            text.contains("detail") || text.contains("open sheet") || text.contains("info") || text.contains("how to fix") -> {
                _requestDetailSheetOpen.value = true
                setTab(MainTab.VIEW_3D)
                "Opened repair sheet for ${selectedComponent.value?.name ?: "component"}"
            }

            // Direct Part Name Matches
            text.contains("pcv") || text.contains("elbow") || text.contains("boot") -> {
                selectComponentById("pcv_elbow")
                _requestDetailSheetOpen.value = true
                "Selected PCV Valve Boot & opened repair guide"
            }
            text.contains("intake manifold") || text.contains("gasket") -> {
                selectComponentById("intake_manifold")
                _requestDetailSheetOpen.value = true
                "Selected Intake Manifold & opened repair guide"
            }
            text.contains("coil") || text.contains("spark") || text.contains("plug") || text.contains("misfire") -> {
                selectComponentById("spark_plugs_coils")
                _requestDetailSheetOpen.value = true
                "Selected Motorcraft EDIS Coil Pack & Spark Plugs"
            }
            text.contains("thermostat") || text.contains("housing") || text.contains("overheat") -> {
                selectComponentById("thermostat_housing")
                _requestDetailSheetOpen.value = true
                "Selected Aluminum Thermostat Housing"
            }
            text.contains("maf") || text.contains("mass air") -> {
                selectComponentById("maf_sensor")
                _requestDetailSheetOpen.value = true
                "Selected Mass Air Flow (MAF) Sensor"
            }
            text.contains("egr") || text.contains("dpfe") -> {
                selectComponentById("egr_valve")
                _requestDetailSheetOpen.value = true
                "Selected EGR Valve & DPFE Sensor"
            }
            text.contains("rotor") || text.contains("pad") || text.contains("caliper") -> {
                selectComponentById("front_brakes_rotors")
                _requestDetailSheetOpen.value = true
                "Selected Front Brake Rotors & Pads"
            }
            text.contains("transmission") || text.contains("5r55e") || text.contains("fluid") -> {
                selectComponentById("transmission_5r55e")
                _requestDetailSheetOpen.value = true
                "Selected 5R55E Automatic Transmission"
            }
            text.contains("4wd") || text.contains("4x4") || text.contains("transfer case") || text.contains("driveshaft") || text.contains("diagram") -> {
                selectComponentById("driveshaft_4x4")
                setTab(MainTab.REPAIR_MANUAL)
                "Selected Control Trac 4WD Transfer Case & Driveshafts"
            }
            text.contains("alternator") || text.contains("battery") -> {
                selectComponentById("battery_alternator")
                _requestDetailSheetOpen.value = true
                "Selected Motorcraft 130A Alternator & Battery"
            }

            else -> {
                "Unrecognized command: \"$spokenText\". Say 'Engine', 'Next', 'PCV Valve', 'Manual', etc."
            }
        }

        _voiceNotice.value = feedback
        return feedback
    }

    fun sendDiagnosticQuery(userQueryText: String) {
        val trimmed = userQueryText.trim()
        if (trimmed.isEmpty() || _isGeminiThinking.value) return

        _lastDiagnosticQuery.value = trimmed

        val userMessage = ChatMessage(
            sender = ChatSender.USER,
            text = trimmed
        )

        // Append user message immediately
        val currentHistory = _chatMessages.value.toMutableList()
        currentHistory.add(userMessage)
        _chatMessages.value = currentHistory
        _isGeminiThinking.value = true

        viewModelScope.launch {
            try {
                val mechanicResponse = geminiRepo.analyzeSymptom(trimmed, currentHistory)
                val updated = _chatMessages.value.toMutableList()
                updated.add(mechanicResponse)
                _chatMessages.value = updated

                // If response was generated via local fallback, notify user
                val isFallback = com.example.BuildConfig.GEMINI_API_KEY.isBlank() || 
                                 com.example.BuildConfig.GEMINI_API_KEY == "MY_GEMINI_API_KEY"
                if (isFallback) {
                    showSnackbar(
                        SnackbarPayload(
                            type = SnackbarType.WARNING,
                            message = "Offline 2004 Sport Trac AI Mechanic active",
                            subtext = "Live Gemini cloud API is currently unreachable. Used local Ford TSB diagnostic engine.",
                            actionLabel = "Retry Cloud AI",
                            onAction = { retryLastGeminiQuery() },
                            durationMillis = 4000L
                        )
                    )
                }
            } catch (e: Exception) {
                val errorMsg = ChatMessage(
                    sender = ChatSender.GEMINI_MECHANIC,
                    text = "Sorry, unable to analyze symptom right now: ${e.message}",
                    isError = true
                )
                val updated = _chatMessages.value.toMutableList()
                updated.add(errorMsg)
                _chatMessages.value = updated

                showSnackbar(
                    SnackbarPayload(
                        type = SnackbarType.GEMINI_ERROR,
                        message = "Gemini API Request Failed",
                        subtext = "${e.localizedMessage ?: "Network/timeout error occurred while contacting AI service."}",
                        actionLabel = "Retry Analysis",
                        onAction = { retryLastGeminiQuery() },
                        durationMillis = 6500L
                    )
                )
            } finally {
                _isGeminiThinking.value = false
            }
        }
    }

    fun clearChatHistory() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = ChatSender.GEMINI_MECHANIC,
                text = "Chat history cleared. Describe any new engine symptoms or OBD-II fault codes to start a new diagnosis!",
                urgencyLevel = "Monitor / Safe to Drive"
            )
        )
    }

    // O'Reilly Commercial Account State
    private val _commercialAccount = MutableStateFlow(SportTracPartsCatalog.defaultCommercialAccount)
    val commercialAccount: StateFlow<OreillyCommercialAccount> = _commercialAccount.asStateFlow()

    // Shopping Cart State
    private val _cartItems = MutableStateFlow<List<CartItem>>(
        listOf(
            CartItem(
                part = SportTracPartsCatalog.catalog[0], // Spark plugs
                quantity = 1,
                fulfillment = FulfillmentType.LOCAL_PICKUP
            ),
            CartItem(
                part = SportTracPartsCatalog.catalog[2], // Oil Filter FL-820S
                quantity = 1,
                fulfillment = FulfillmentType.LOCAL_PICKUP
            ),
            CartItem(
                part = SportTracPartsCatalog.catalog[4], // Dorman Thermostat Housing Kit
                quantity = 1,
                fulfillment = FulfillmentType.ONLINE_DELIVERY
            )
        )
    )
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Default Cart Sorting Option: CHEAPEST FIRST
    private val _cartSortOption = MutableStateFlow(CartSortOption.CHEAPEST_FIRST)
    val cartSortOption: StateFlow<CartSortOption> = _cartSortOption.asStateFlow()

    // Order Success Notification State
    private val _orderSuccessNotice = MutableStateFlow<String?>(null)
    val orderSuccessNotice: StateFlow<String?> = _orderSuccessNotice.asStateFlow()

    fun setCartSortOption(sortOption: CartSortOption) {
        _cartSortOption.value = sortOption
    }

    fun addPartToCart(part: PartItem, fulfillment: FulfillmentType = FulfillmentType.LOCAL_PICKUP) {
        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.part.id == part.id }
        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            currentList[existingIndex] = existing.copy(quantity = existing.quantity + 1)
        } else {
            currentList.add(CartItem(part = part, quantity = 1, fulfillment = fulfillment))
        }
        _cartItems.value = currentList
        _voiceNotice.value = "🛒 Added '${part.partName}' to O'Reilly Commercial Cart!"
    }

    fun addPartForComponent(component: Component3DModel) {
        val availableParts = SportTracPartsCatalog.getPartsForComponent(component.id)
        val cheapestPart = availableParts.minByOrNull { it.oreillyCommercialPrice } ?: availableParts.first()
        addPartToCart(cheapestPart)
    }

    fun updateCartItemQuantity(partId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(partId)
            return
        }
        val currentList = _cartItems.value.toMutableList()
        val idx = currentList.indexOfFirst { it.part.id == partId }
        if (idx >= 0) {
            currentList[idx] = currentList[idx].copy(quantity = newQuantity)
            _cartItems.value = currentList
        }
    }

    fun updateCartItemFulfillment(partId: String, fulfillment: FulfillmentType) {
        val currentList = _cartItems.value.toMutableList()
        val idx = currentList.indexOfFirst { it.part.id == partId }
        if (idx >= 0) {
            currentList[idx] = currentList[idx].copy(fulfillment = fulfillment)
            _cartItems.value = currentList
        }
    }

    fun removeFromCart(partId: String) {
        _cartItems.value = _cartItems.value.filterNot { it.part.id == partId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun checkoutOrder(paymentMethod: String) {
        val total = _cartItems.value.sumOf { it.itemTotal }
        val count = _cartItems.value.sumOf { it.quantity }
        val orderNum = "OR-2026-${(100000..999999).random()}"
        
        _orderSuccessNotice.value = "✅ Order #$orderNum Confirmed!\n$count parts total: \$${String.format("%.2f", total)} charged via $paymentMethod.\nO'Reilly Store #1428 notification sent for pickup/delivery!"
        clearCart()
    }

    fun dismissOrderSuccessNotice() {
        _orderSuccessNotice.value = null
    }

    fun deleteLog(id: Long) {
        viewModelScope.launch {
            repository.deleteLog(id)
        }
    }

    fun addUpcomingTask(task: com.example.data.local.UpcomingTaskEntity) {
        viewModelScope.launch {
            repository.addUpcomingTask(task)
            _voiceNotice.value = "Upcoming maintenance task added to Room DB!"
        }
    }

    fun completeUpcomingTask(
        task: com.example.data.local.UpcomingTaskEntity,
        actualMileage: Int,
        costUsd: Double,
        notes: String
    ) {
        viewModelScope.launch {
            repository.completeUpcomingTask(task, actualMileage, costUsd, notes)
            _voiceNotice.value = "Task '${task.title}' marked completed in Room DB!"
        }
    }

    fun deleteUpcomingTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteUpcomingTask(taskId)
        }
    }

    fun updateUpcomingTask(task: com.example.data.local.UpcomingTaskEntity) {
        viewModelScope.launch {
            repository.updateUpcomingTask(task)
        }
    }
}

