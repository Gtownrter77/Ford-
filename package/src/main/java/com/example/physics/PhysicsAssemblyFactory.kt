package com.example.physics

import com.example.model.Component3DModel
import com.example.model.Point3D
import com.example.model.SubAssemblyPart
import com.example.model.SubAssemblyType

object PhysicsAssemblyFactory {

    /**
     * Converts a Component3DModel and its sub-assembly parts into physics rigid bodies
     * and mechanical assembly constraints.
     */
    fun createPhysicsRigidBodiesAndConstraints(
        component: Component3DModel,
        preset: PhysicsPreset = PhysicsPreset.EXPLODED_PHYSICS
    ): Pair<List<RigidBody3D>, List<AssemblyConstraint3D>> {

        val bodies = mutableListOf<RigidBody3D>()
        val constraints = mutableListOf<AssemblyConstraint3D>()

        // 1. Add Main Component Body (Fixed Anchor or Primary Mass)
        val mainBodyId = "rigid_main_${component.id}"
        val mainBody = RigidBody3D(
            id = mainBodyId,
            name = component.name,
            isFixed = true, // Engine block / main component acts as static fixture
            massKg = 15.0f,
            position = component.centerOffset,
            initialPosition = component.centerOffset,
            vertices = component.vertices,
            faces = component.faces,
            colorHex = component.system.hexColor,
            subAssemblyType = SubAssemblyType.MAIN_BODY
        )
        bodies.add(mainBody)

        // 2. Convert Sub-Assembly Parts (Fasteners, Gaskets, Pulleys, Brackets)
        if (component.subAssemblies.isNotEmpty()) {
            component.subAssemblies.forEachIndexed { index, part ->
                val bodyId = "rigid_sub_${component.id}_${part.id}"
                val worldPos = Point3D(
                    component.centerOffset.x + part.localOffset.x,
                    component.centerOffset.y + part.localOffset.y,
                    component.centerOffset.z + part.localOffset.z
                )

                val isFastenerPart = part.type == SubAssemblyType.BOLT ||
                        part.type == SubAssemblyType.SCREW ||
                        part.type == SubAssemblyType.WASHER ||
                        part.type == SubAssemblyType.SPARK_PLUG

                val mass = when (part.type) {
                    SubAssemblyType.BOLT, SubAssemblyType.SCREW -> 0.15f
                    SubAssemblyType.BELT -> 0.40f
                    SubAssemblyType.GASKET, SubAssemblyType.SEAL_O_RING -> 0.05f
                    SubAssemblyType.WASHER -> 0.02f
                    else -> 1.5f
                }

                val rigidPart = RigidBody3D(
                    id = bodyId,
                    name = part.name,
                    isFixed = false,
                    massKg = mass,
                    position = worldPos,
                    initialPosition = worldPos,
                    restitution = if (isFastenerPart) 0.65f else 0.25f,
                    friction = 0.30f,
                    boundingBox = AABB3D.fromVertices(part.vertices, worldPos),
                    colorHex = part.type.defaultHexColor,
                    vertices = part.vertices,
                    faces = part.faces,
                    isFastener = isFastenerPart,
                    subAssemblyType = part.type
                )
                bodies.add(rigidPart)

                // 3. Add Assembly Constraints according to Part Type
                when (part.type) {
                    SubAssemblyType.BOLT, SubAssemblyType.SCREW, SubAssemblyType.SPARK_PLUG -> {
                        // Prismatic Slider along Fastener Thread Vector
                        constraints.add(
                            AssemblyConstraint3D.PrismaticSlider(
                                bodyId = bodyId,
                                slideAxisVector = part.explodeDirection,
                                minTravelDistance = 0.0f,
                                maxTravelDistance = 0.8f * part.explodeDistanceMultiplier,
                                springRestorationK = 25.0f,
                                dampingC = 4.0f,
                                lockAtEndDetent = true
                            )
                        )
                        // Fastener Breakaway Detent
                        constraints.add(
                            AssemblyConstraint3D.FastenerDetentLock(
                                bodyId = bodyId,
                                breakawayTorqueNm = 35.0f
                            )
                        )
                    }

                    SubAssemblyType.BELT, SubAssemblyType.MAIN_BODY -> {
                        // Revolute Hinge for rotational pulley or belt motion
                        constraints.add(
                            AssemblyConstraint3D.RevoluteHinge(
                                bodyId = bodyId,
                                anchorPoint = worldPos,
                                rotationAxis = Point3D(0f, 0f, 1f),
                                targetRPM = 120f
                            )
                        )
                    }

                    SubAssemblyType.GASKET, SubAssemblyType.SEAL_O_RING, SubAssemblyType.WASHER -> {
                        // Spring-Damper coupling with main housing fixture
                        constraints.add(
                            AssemblyConstraint3D.SpringDamper(
                                bodyAId = mainBodyId,
                                bodyBId = bodyId,
                                restLength = 0.25f * part.explodeDistanceMultiplier,
                                stiffnessK = 50.0f,
                                dampingC = 5.0f
                            )
                        )
                    }
                }
            }
        } else {
            // Generate synthetic fasteners for component if subAssemblies list is empty
            val synthTypes = listOf(
                Pair("Hex Flange Bolt M10", SubAssemblyType.BOLT),
                Pair("Lock Washer", SubAssemblyType.WASHER),
                Pair("Mounting Bracket Bolt", SubAssemblyType.BOLT),
                Pair("High-Temp Gasket Seal", SubAssemblyType.GASKET)
            )

            synthTypes.forEachIndexed { idx, (pName, pType) ->
                val bId = "synth_rigid_${component.id}_$idx"
                val offset = Point3D(
                    component.centerOffset.x + (idx * 0.12f - 0.18f),
                    component.centerOffset.y + 0.25f,
                    component.centerOffset.z + (idx * 0.08f - 0.12f)
                )

                val body = RigidBody3D(
                    id = bId,
                    name = pName,
                    isFixed = false,
                    massKg = 0.2f,
                    position = offset,
                    initialPosition = offset,
                    restitution = 0.5f,
                    colorHex = pType.defaultHexColor,
                    isFastener = pType == SubAssemblyType.BOLT,
                    subAssemblyType = pType
                )
                bodies.add(body)

                constraints.add(
                    AssemblyConstraint3D.PrismaticSlider(
                        bodyId = bId,
                        slideAxisVector = Point3D(0f, 1f, 0f),
                        minTravelDistance = 0f,
                        maxTravelDistance = 0.6f
                    )
                )
            }
        }

        return Pair(bodies, constraints)
    }
}
