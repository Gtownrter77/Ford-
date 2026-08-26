package com.example.mentor

import com.example.model.Component3DModel
import com.example.model.RepairStep

/**
 * One Mentor instruction bound to a 3D node. These are shown on the vehicle
 * hub, not only inside a dialog.
 */
data class OnModelInstruction(
    val index: Int,
    val title: String,
    val body: String,
    val warning: String?,
    val tip: String?,
    val nodeId: String,
    val nodeName: String
)

object OnModelInstructions {
    fun forComponent(component: Component3DModel): List<OnModelInstruction> {
        val steps = component.repairSteps
        if (steps.isNotEmpty()) {
            return steps.map { step -> step.toOnModel(component) }
        }
        return listOf(
            OnModelInstruction(
                index = 1,
                title = "Inspect ${component.name}",
                body = component.description.ifBlank { "No packaged procedure for this node yet." },
                warning = null,
                tip = component.locationDescription,
                nodeId = component.id,
                nodeName = component.name
            )
        )
    }

    private fun RepairStep.toOnModel(component: Component3DModel) = OnModelInstruction(
        index = stepNumber,
        title = title,
        body = instruction,
        warning = warning,
        tip = tip,
        nodeId = component.id,
        nodeName = component.name
    )
}
