package com.example.physics

import com.example.model.Face3D
import com.example.model.Point3D
import com.example.model.SubAssemblyPart
import com.example.model.SubAssemblyType
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Axis-Aligned Bounding Box (AABB) in 3D Space.
 */
data class AABB3D(
    val min: Point3D,
    val max: Point3D
) {
    fun intersects(other: AABB3D): Boolean {
        return (min.x <= other.max.x && max.x >= other.min.x) &&
                (min.y <= other.max.y && max.y >= other.min.y) &&
                (min.z <= other.max.z && max.z >= other.min.z)
    }

    fun getCenter(): Point3D {
        return Point3D(
            (min.x + max.x) * 0.5f,
            (min.y + max.y) * 0.5f,
            (min.z + max.z) * 0.5f
        )
    }

    fun getExtents(): Point3D {
        return Point3D(
            (max.x - min.x) * 0.5f,
            (max.y - min.y) * 0.5f,
            (max.z - min.z) * 0.5f
        )
    }

    companion object {
        fun fromVertices(vertices: List<Point3D>, offset: Point3D = Point3D(0f, 0f, 0f)): AABB3D {
            if (vertices.isEmpty()) {
                return AABB3D(
                    min = Point3D(offset.x - 0.1f, offset.y - 0.1f, offset.z - 0.1f),
                    max = Point3D(offset.x + 0.1f, offset.y + 0.1f, offset.z + 0.1f)
                )
            }
            var minX = Float.MAX_VALUE
            var minY = Float.MAX_VALUE
            var minZ = Float.MAX_VALUE
            var maxX = -Float.MAX_VALUE
            var maxY = -Float.MAX_VALUE
            var maxZ = -Float.MAX_VALUE

            for (v in vertices) {
                val x = v.x + offset.x
                val y = v.y + offset.y
                val z = v.z + offset.z

                if (x < minX) minX = x
                if (y < minY) minY = y
                if (z < minZ) minZ = z
                if (x > maxX) maxX = x
                if (y > maxY) maxY = y
                if (z > maxZ) maxZ = z
            }

            return AABB3D(
                min = Point3D(minX, minY, minZ),
                max = Point3D(maxX, maxY, maxZ)
            )
        }
    }
}

/**
 * 3D Rigid Body representation for components, sub-assemblies, and fasteners.
 */
data class RigidBody3D(
    val id: String,
    val name: String,
    val isFixed: Boolean = false, // Fixed/Static body (e.g. Engine Block Housing)
    val massKg: Float = 1.0f,
    var position: Point3D = Point3D(0f, 0f, 0f),
    var initialPosition: Point3D = Point3D(0f, 0f, 0f),
    var velocity: Point3D = Point3D(0f, 0f, 0f),
    var acceleration: Point3D = Point3D(0f, 0f, 0f),
    var rotationDeg: Point3D = Point3D(0f, 0f, 0f),
    var angularVelocity: Point3D = Point3D(0f, 0f, 0f),
    val restitution: Float = 0.45f, // Bounciness coefficient
    val friction: Float = 0.35f, // Friction coefficient
    val linearDamping: Float = 0.85f, // Air resistance / velocity damping
    val angularDamping: Float = 0.90f,
    val boundingBox: AABB3D = AABB3D(Point3D(-0.1f, -0.1f, -0.1f), Point3D(0.1f, 0.1f, 0.1f)),
    val colorHex: String = "#0284C7",
    val vertices: List<Point3D> = emptyList(),
    val faces: List<Face3D> = emptyList(),
    val isFastener: Boolean = false,
    val subAssemblyType: SubAssemblyType = SubAssemblyType.MAIN_BODY
) {
    val invMass: Float
        get() = if (isFixed || massKg <= 0f) 0f else 1.0f / massKg

    fun getCurrentAABB(): AABB3D {
        return AABB3D.fromVertices(vertices, position)
    }
}

/**
 * Collision Contact Point and Impulse Resolution details.
 */
data class CollisionContact3D(
    val bodyAId: String,
    val bodyBId: String,
    val contactPoint: Point3D,
    val normal: Point3D,
    val penetrationDepth: Float,
    val impulseMagnitude: Float
)

/**
 * Assembly Constraint Types for realistic mechanical motion during repair simulations.
 */
sealed class AssemblyConstraint3D {

    /**
     * Prismatic / Slider Constraint:
     * Restricts movement strictly along a linear axis (e.g., fastener extraction/insertion vector along threads).
     */
    data class PrismaticSlider(
        val bodyId: String,
        val slideAxisVector: Point3D = Point3D(0f, 1f, 0f),
        val minTravelDistance: Float = 0.0f,
        val maxTravelDistance: Float = 1.2f,
        val springRestorationK: Float = 15f,
        val dampingC: Float = 2.5f,
        val lockAtEndDetent: Boolean = true
    ) : AssemblyConstraint3D()

    /**
     * Revolute / Hinge Constraint:
     * Restricts translational motion while allowing rotational motion around an anchor axis (e.g., pulleys, gears, water pump impeller).
     */
    data class RevoluteHinge(
        val bodyId: String,
        val anchorPoint: Point3D,
        val rotationAxis: Point3D = Point3D(0f, 0f, 1f),
        val targetRPM: Float = 120f,
        val frictionTorque: Float = 0.1f
    ) : AssemblyConstraint3D()

    /**
     * Spring-Damper Constraint:
     * Simulates tensioner springs, valve springs, or exploded view separation force between parent and child bodies.
     */
    data class SpringDamper(
        val bodyAId: String,
        val bodyBId: String,
        val restLength: Float = 0.5f,
        val stiffnessK: Float = 80.0f,
        val dampingC: Float = 8.0f
    ) : AssemblyConstraint3D()

    /**
     * Fastener Detent Lock:
     * Locks fastener/part in place until breakaway force threshold (torque) is exceeded.
     */
    data class FastenerDetentLock(
        val bodyId: String,
        val breakawayTorqueNm: Float = 45.0f,
        var isUnlocked: Boolean = false
    ) : AssemblyConstraint3D()
}

/**
 * Simulation Preset Configurations for Exploded View & Assembly testing.
 */
enum class PhysicsPreset(val label: String, val description: String) {
    EXPLODED_PHYSICS("Exploded View Physics", "Spring-damper separation with AABB collisions and slider constraints"),
    FASTENER_EJECTION("Fastener Ejection & Retain", "Threaded prismatic sliders with breakaway detents and gravity collection"),
    ROTATIONAL_PULLEY("Rotational Pulley & Belt", "Hinge constraints simulating rotating pulleys, gears, and serpentine belt drive"),
    GRAVITY_COLLISION("Gravity Drop Test", "Freeform rigid body gravity drop with restitution bounces on housing fixture")
}

/**
 * Real-time state of the 3D Physics Engine.
 */
data class PhysicsSimulationState(
    val bodies: List<RigidBody3D> = emptyList(),
    val constraints: List<AssemblyConstraint3D> = emptyList(),
    val activeCollisions: List<CollisionContact3D> = emptyList(),
    val gravity: Point3D = Point3D(0f, -9.81f, 0f),
    val isSimulating: Boolean = false,
    val explodeFactor: Float = 0.0f,
    val totalKineticEnergyJoules: Float = 0f,
    val stepCount: Long = 0L,
    val activePreset: PhysicsPreset = PhysicsPreset.EXPLODED_PHYSICS,
    val collisionsEnabled: Boolean = true,
    val gravityEnabled: Boolean = false
)

/**
 * Lightweight 3D Physics Simulator Engine.
 */
class Physics3DSimulator {

    private val _bodies = mutableMapOf<String, RigidBody3D>()
    private val _constraints = mutableListOf<AssemblyConstraint3D>()
    private val _collisions = mutableListOf<CollisionContact3D>()

    private var _isSimulating = false
    private var _explodeFactor = 0f
    private var _gravityEnabled = false
    private var _collisionsEnabled = true
    private var _activePreset = PhysicsPreset.EXPLODED_PHYSICS
    private var _stepCount = 0L

    /**
     * Initializes simulation with a set of rigid bodies and assembly constraints.
     */
    fun setupSimulation(
        bodies: List<RigidBody3D>,
        constraints: List<AssemblyConstraint3D>,
        preset: PhysicsPreset = PhysicsPreset.EXPLODED_PHYSICS
    ) {
        _bodies.clear()
        bodies.forEach { _bodies[it.id] = it.copy() }

        _constraints.clear()
        _constraints.addAll(constraints)

        _collisions.clear()
        _activePreset = preset
        _stepCount = 0L

        // Default gravity setting based on preset
        _gravityEnabled = preset == PhysicsPreset.GRAVITY_COLLISION || preset == PhysicsPreset.FASTENER_EJECTION
        _collisionsEnabled = true
    }

    fun startSimulation() { _isSimulating = true }
    fun pauseSimulation() { _isSimulating = false }
    fun resetSimulation() {
        _bodies.values.forEach { body ->
            body.position = body.initialPosition
            body.velocity = Point3D(0f, 0f, 0f)
            body.acceleration = Point3D(0f, 0f, 0f)
            body.rotationDeg = Point3D(0f, 0f, 0f)
            body.angularVelocity = Point3D(0f, 0f, 0f)
        }
        _collisions.clear()
        _stepCount = 0L
    }

    fun setExplodeFactor(factor: Float) {
        _explodeFactor = factor.coerceIn(0f, 2.5f)
    }

    fun toggleGravity(enabled: Boolean) { _gravityEnabled = enabled }
    fun toggleCollisions(enabled: Boolean) { _collisionsEnabled = enabled }

    /**
     * Main Physics Integration Tick (Euler / Verlet Integration + Impulse Constraints).
     */
    fun step(dtSeconds: Float = 0.016f): PhysicsSimulationState {
        if (!_isSimulating) {
            return getCurrentState()
        }

        _stepCount++
        _collisions.clear()

        val dt = dtSeconds.coerceIn(0.001f, 0.05f)
        val gravityVec = if (_gravityEnabled) Point3D(0f, -9.81f, 0f) else Point3D(0f, 0f, 0f)

        // 1. Apply Forces & Explode Impulses to Rigid Bodies
        _bodies.values.forEach { body ->
            if (!body.isFixed) {
                // Base gravity acceleration
                var fx = body.invMass * gravityVec.x
                var fy = body.invMass * gravityVec.y
                var fz = body.invMass * gravityVec.z

                // Exploded view separation drive
                val targetExplodeOffset = Point3D(
                    body.initialPosition.x * (1f + _explodeFactor * 1.5f),
                    body.initialPosition.y + (if (body.isFastener) _explodeFactor * 0.8f else _explodeFactor * 0.4f),
                    body.initialPosition.z * (1f + _explodeFactor * 1.5f)
                )

                // Spring force pulling body towards exploded view position
                val springK = 35.0f
                val dampC = 6.0f
                val dx = targetExplodeOffset.x - body.position.x
                val dy = targetExplodeOffset.y - body.position.y
                val dz = targetExplodeOffset.z - body.position.z

                fx += (dx * springK - body.velocity.x * dampC)
                fy += (dy * springK - body.velocity.y * dampC)
                fz += (dz * springK - body.velocity.z * dampC)

                body.acceleration = Point3D(fx, fy, fz)

                // Integrate Velocity
                body.velocity = Point3D(
                    (body.velocity.x + body.acceleration.x * dt) * body.linearDamping,
                    (body.velocity.y + body.acceleration.y * dt) * body.linearDamping,
                    (body.velocity.z + body.acceleration.z * dt) * body.linearDamping
                )

                // Integrate Position
                body.position = Point3D(
                    body.position.x + body.velocity.x * dt,
                    body.position.y + body.velocity.y * dt,
                    body.position.z + body.velocity.z * dt
                )

                // Integrate Angular Velocity & Rotation
                body.angularVelocity = Point3D(
                    body.angularVelocity.x * body.angularDamping,
                    body.angularVelocity.y * body.angularDamping,
                    body.angularVelocity.z * body.angularDamping
                )

                body.rotationDeg = Point3D(
                    (body.rotationDeg.x + body.angularVelocity.x * dt) % 360f,
                    (body.rotationDeg.y + body.angularVelocity.y * dt) % 360f,
                    (body.rotationDeg.z + body.angularVelocity.z * dt) % 360f
                )
            }
        }

        // 2. Solve Assembly Constraints
        solveAssemblyConstraints(dt)

        // 3. Collision Detection & Response (Broadphase AABB + Narrowphase Impulse)
        if (_collisionsEnabled) {
            solveCollisions()
        }

        return getCurrentState()
    }

    /**
     * Enforces Prismatic Sliders, Revolute Hinges, and Spring Constraints.
     */
    private fun solveAssemblyConstraints(dt: Float) {
        _constraints.forEach { constraint ->
            when (constraint) {
                is AssemblyConstraint3D.PrismaticSlider -> {
                    val body = _bodies[constraint.bodyId] ?: return@forEach
                    val axis = constraint.slideAxisVector
                    val axisLen = sqrt(axis.x * axis.x + axis.y * axis.y + axis.z * axis.z).coerceAtLeast(0.001f)
                    val normAxis = Point3D(axis.x / axisLen, axis.y / axisLen, axis.z / axisLen)

                    // Project current relative position onto slide axis
                    val relPos = Point3D(
                        body.position.x - body.initialPosition.x,
                        body.position.y - body.initialPosition.y,
                        body.position.z - body.initialPosition.z
                    )

                    val travelDistance = relPos.x * normAxis.x + relPos.y * normAxis.y + relPos.z * normAxis.z
                    val clampedTravel = travelDistance.coerceIn(constraint.minTravelDistance, constraint.maxTravelDistance)

                    // Constrain position along slider line strictly
                    body.position = Point3D(
                        body.initialPosition.x + normAxis.x * clampedTravel,
                        body.initialPosition.y + normAxis.y * clampedTravel,
                        body.initialPosition.z + normAxis.z * clampedTravel
                    )

                    // Constrain velocity along slide axis
                    val dotVel = body.velocity.x * normAxis.x + body.velocity.y * normAxis.y + body.velocity.z * normAxis.z
                    body.velocity = Point3D(
                        normAxis.x * dotVel,
                        normAxis.y * dotVel,
                        normAxis.z * dotVel
                    )
                }

                is AssemblyConstraint3D.RevoluteHinge -> {
                    val body = _bodies[constraint.bodyId] ?: return@forEach
                    // Lock position to anchor point, rotate around hinge axis
                    val targetAngularSpeedDeg = (constraint.targetRPM * 360f) / 60f
                    body.angularVelocity = Point3D(
                        constraint.rotationAxis.x * targetAngularSpeedDeg,
                        constraint.rotationAxis.y * targetAngularSpeedDeg,
                        constraint.rotationAxis.z * targetAngularSpeedDeg
                    )
                }

                is AssemblyConstraint3D.SpringDamper -> {
                    val bodyA = _bodies[constraint.bodyAId] ?: return@forEach
                    val bodyB = _bodies[constraint.bodyBId] ?: return@forEach

                    val dx = bodyB.position.x - bodyA.position.x
                    val dy = bodyB.position.y - bodyA.position.y
                    val dz = bodyB.position.z - bodyA.position.z
                    val dist = sqrt(dx * dx + dy * dy + dz * dz).coerceAtLeast(0.001f)

                    val delta = dist - constraint.restLength
                    val springForce = constraint.stiffnessK * delta

                    val nx = dx / dist
                    val ny = dy / dist
                    val nz = dz / dist

                    if (!bodyA.isFixed) {
                        bodyA.velocity = Point3D(
                            bodyA.velocity.x + (nx * springForce * bodyA.invMass) * dt,
                            bodyA.velocity.y + (ny * springForce * bodyA.invMass) * dt,
                            bodyA.velocity.z + (nz * springForce * bodyA.invMass) * dt
                        )
                    }
                    if (!bodyB.isFixed) {
                        bodyB.velocity = Point3D(
                            bodyB.velocity.x - (nx * springForce * bodyB.invMass) * dt,
                            bodyB.velocity.y - (ny * springForce * bodyB.invMass) * dt,
                            bodyB.velocity.z - (nz * springForce * bodyB.invMass) * dt
                        )
                    }
                }

                is AssemblyConstraint3D.FastenerDetentLock -> {
                    val body = _bodies[constraint.bodyId] ?: return@forEach
                    if (!constraint.isUnlocked && _explodeFactor < 0.1f) {
                        body.velocity = Point3D(0f, 0f, 0f)
                        body.position = body.initialPosition
                    }
                }
            }
        }
    }

    /**
     * Broadphase & Narrowphase AABB Collision Resolution.
     */
    private fun solveCollisions() {
        val bodyList = _bodies.values.toList()

        for (i in bodyList.indices) {
            for (j in i + 1 until bodyList.size) {
                val a = bodyList[i]
                val b = bodyList[j]

                if (a.isFixed && b.isFixed) continue

                val boxA = a.getCurrentAABB()
                val boxB = b.getCurrentAABB()

                if (boxA.intersects(boxB)) {
                    val centerA = boxA.getCenter()
                    val centerB = boxB.getCenter()

                    val dx = centerB.x - centerA.x
                    val dy = centerB.y - centerA.y
                    val dz = centerB.z - centerA.z
                    val dist = sqrt(dx * dx + dy * dy + dz * dz).coerceAtLeast(0.001f)

                    val normX = dx / dist
                    val normY = dy / dist
                    val normZ = dz / dist

                    val extentsA = boxA.getExtents()
                    val extentsB = boxB.getExtents()
                    val overlapX = (extentsA.x + extentsB.x) - abs(centerA.x - centerB.x)
                    val overlapY = (extentsA.y + extentsB.y) - abs(centerA.y - centerB.y)
                    val overlapZ = (extentsA.z + extentsB.z) - abs(centerA.z - centerB.z)

                    val penetration = min(overlapX, min(overlapY, overlapZ)).coerceAtLeast(0.001f)

                    // Relative velocity
                    val relVelX = b.velocity.x - a.velocity.x
                    val relVelY = b.velocity.y - a.velocity.y
                    val relVelZ = b.velocity.z - a.velocity.z

                    val velAlongNormal = relVelX * normX + relVelY * normY + relVelZ * normZ

                    // Do not resolve if velocities are separating
                    if (velAlongNormal < 0f) {
                        val e = min(a.restitution, b.restitution)
                        val jImpulse = -(1f + e) * velAlongNormal / (a.invMass + b.invMass).coerceAtLeast(0.001f)

                        val impulseX = jImpulse * normX
                        val impulseY = jImpulse * normY
                        val impulseZ = jImpulse * normZ

                        if (!a.isFixed) {
                            a.velocity = Point3D(
                                a.velocity.x - impulseX * a.invMass,
                                a.velocity.y - impulseY * a.invMass,
                                a.velocity.z - impulseZ * a.invMass
                            )
                            // Positional correction
                            a.position = Point3D(
                                a.position.x - normX * penetration * 0.5f,
                                a.position.y - normY * penetration * 0.5f,
                                a.position.z - normZ * penetration * 0.5f
                            )
                        }

                        if (!b.isFixed) {
                            b.velocity = Point3D(
                                b.velocity.x + impulseX * b.invMass,
                                b.velocity.y + impulseY * b.invMass,
                                b.velocity.z + impulseZ * b.invMass
                            )
                            // Positional correction
                            b.position = Point3D(
                                b.position.x + normX * penetration * 0.5f,
                                b.position.y + normY * penetration * 0.5f,
                                b.position.z + normZ * penetration * 0.5f
                            )
                        }

                        // Add angular tumble to colliding fasteners
                        if (a.isFastener) {
                            a.angularVelocity = Point3D(
                                a.angularVelocity.x + normY * 180f,
                                a.angularVelocity.y + normZ * 180f,
                                a.angularVelocity.z + normX * 180f
                            )
                        }
                        if (b.isFastener) {
                            b.angularVelocity = Point3D(
                                b.angularVelocity.x - normY * 180f,
                                b.angularVelocity.y - normZ * 180f,
                                b.angularVelocity.z - normX * 180f
                            )
                        }

                        val contactPoint = Point3D(
                            (centerA.x + centerB.x) * 0.5f,
                            (centerA.y + centerB.y) * 0.5f,
                            (centerA.z + centerB.z) * 0.5f
                        )

                        _collisions.add(
                            CollisionContact3D(
                                bodyAId = a.id,
                                bodyBId = b.id,
                                contactPoint = contactPoint,
                                normal = Point3D(normX, normY, normZ),
                                penetrationDepth = penetration,
                                impulseMagnitude = jImpulse
                            )
                        )
                    }
                }
            }
        }
    }

    /**
     * Calculates total Kinetic Energy in the system (0.5 * m * v^2 + 0.5 * I * w^2).
     */
    fun calculateKineticEnergy(): Float {
        var ke = 0f
        _bodies.values.forEach { b ->
            if (!b.isFixed) {
                val vSq = b.velocity.x * b.velocity.x + b.velocity.y * b.velocity.y + b.velocity.z * b.velocity.z
                ke += 0.5f * b.massKg * vSq
            }
        }
        return ke
    }

    fun getCurrentState(): PhysicsSimulationState {
        return PhysicsSimulationState(
            bodies = _bodies.values.toList(),
            constraints = _constraints.toList(),
            activeCollisions = _collisions.toList(),
            gravity = if (_gravityEnabled) Point3D(0f, -9.81f, 0f) else Point3D(0f, 0f, 0f),
            isSimulating = _isSimulating,
            explodeFactor = _explodeFactor,
            totalKineticEnergyJoules = calculateKineticEnergy(),
            stepCount = _stepCount,
            activePreset = _activePreset,
            collisionsEnabled = _collisionsEnabled,
            gravityEnabled = _gravityEnabled
        )
    }
}
