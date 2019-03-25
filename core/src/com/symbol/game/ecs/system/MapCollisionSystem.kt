package com.symbol.game.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.utils.Array
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.*
import com.symbol.game.ecs.component.map.MapEntityComponent
import com.symbol.game.ecs.component.map.MovingPlatformComponent
import com.symbol.game.effects.particle.DEFAULT_INTESITY
import com.symbol.game.effects.particle.DEFAULT_LIFETIME
import com.symbol.game.effects.particle.ParticleSpawner
import com.symbol.game.map.MapObject
import com.symbol.game.map.MapObjectType
import com.symbol.game.util.Resources

private const val NUM_SUB_STEPS = 30
private const val MAP_OBJECT_DAMAGE_RATE = 1f
private const val MAP_OBJECT_SLOW_PERCENTAGE = 0.4f
private const val MAP_OBJECT_PUSH = 45f
const val MAP_OBJECT_JUMP_BOOST_PERCENTAGE = 1.5f

class MapCollisionSystem(private val res: Resources) : IteratingSystem(
        Family.all(BoundingBoxComponent::class.java, GravityComponent::class.java).get()
) {

    private var mapObjects: Array<MapObject> = Array()
    private var mapWidth = 0
    private var mapHeight = 0

    private var stepX = 0f
    private var stepY = 0f

    private lateinit var removableEntities: ImmutableArray<Entity>
    private lateinit var movingPlatforms: ImmutableArray<Entity>
    private lateinit var mapEntities: ImmutableArray<Entity>

    private var damageTimes: MutableMap<Entity, Float> = HashMap()
    private var startDamage: MutableMap<Entity, Boolean> = HashMap()

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        removableEntities = engine!!.getEntitiesFor(Family.all(RemoveComponent::class.java).get())
        movingPlatforms = engine.getEntitiesFor(Family.all(MovingPlatformComponent::class.java).get())
        mapEntities = engine.getEntitiesFor(Family.all(MapEntityComponent::class.java).exclude(MovingPlatformComponent::class.java).get())
    }

    override fun update(dt: Float) {
        super.update(dt)
        for (entity in removableEntities) {
            val position = Mapper.POS_MAPPER.get(entity)
            val width = Mapper.TEXTURE_MAPPER.get(entity).texture!!.regionWidth
            val height = Mapper.TEXTURE_MAPPER.get(entity).texture!!.regionHeight
            val remove = Mapper.REMOVE_MAPPER.get(entity)
            if (position.x < -mapWidth - width || position.x > mapWidth * 2 ||
                    position.y < -mapHeight - height || position.y > mapHeight * 2) {
                remove.shouldRemove = true
            }
        }
    }

    override fun processEntity(entity: Entity?, dt: Float) {
        val bb = Mapper.BOUNDING_BOX_MAPPER.get(entity)
        val position = Mapper.POS_MAPPER.get(entity)
        val velocity = Mapper.VEL_MAPPER.get(entity)
        val width = Mapper.TEXTURE_MAPPER.get(entity).texture!!.regionWidth
        val height = Mapper.TEXTURE_MAPPER.get(entity).texture!!.regionHeight
        val gravity = Mapper.GRAVITY_MAPPER.get(entity)
        val player = Mapper.PLAYER_MAPPER.get(entity)

        stepX = (if (gravity.onMovingPlatform) velocity.platformDx else velocity.dx) * dt / NUM_SUB_STEPS
        for (i in 0 until NUM_SUB_STEPS) {
            savePreviousPosition(position)
            position.x += stepX
            bb.rect.setPosition(position.x + (width - bb.rect.width) / 2, position.y + (height - bb.rect.height) / 2)

            if (gravity.collidable) {
                for (mapObject in mapObjects) {
                    if (mapObject.type.solid && bb.rect.overlaps(mapObject.bounds)) {
                        revertCurrentPosition(position)
                    }
                }
                for (mapEntity in mapEntities) {
                    val comp = Mapper.MAP_ENTITY_MAPPER.get(mapEntity)
                    val bounds = Mapper.BOUNDING_BOX_MAPPER.get(mapEntity)
                    if (comp.mapCollidable && bb.rect.overlaps(bounds.rect)) {
                        revertCurrentPosition(position)
                    }
                }
                for (mplatform in movingPlatforms) {
                    val bounds = Mapper.BOUNDING_BOX_MAPPER.get(mplatform)
                    val vel = Mapper.VEL_MAPPER.get(mplatform)
                    if (bb.rect.overlaps(bounds.rect)) {
                        val collisionLeft = (velocity.dx >= 0 && vel.dx < 0) || (velocity.dx > 0 && vel.dx > 0)
                        val collisionRight = (velocity.dx <= 0 && vel.dx > 0) || (velocity.dx < 0 && vel.dx < 0)

                        if (bb.rect.x < bounds.rect.x && collisionLeft)
                            position.x = bounds.rect.x - bb.rect.width - 1
                        else if (bb.rect.x + bb.rect.width > bounds.rect.x + bounds.rect.width && collisionRight)
                            position.x = bounds.rect.x + bounds.rect.width + 1
                    }
                }
            }
        }

        stepY = velocity.dy * dt / NUM_SUB_STEPS
        for (i in 0 until NUM_SUB_STEPS) {
            savePreviousPosition(position)
            position.y += stepY
            bb.rect.setPosition(position.x + (width - bb.rect.width) / 2, position.y + (height - bb.rect.height) / 2)

            if (gravity.collidable) {
                for (mapObject in mapObjects) {
                    if (mapObject.type.solid && bb.rect.overlaps(mapObject.bounds)) {
                        revertCurrentPosition(position)
                        if (velocity.dy < 0 || (gravity.reverse && velocity.dy > 0 )) {
                            gravity.onGround = true
                            gravity.platform.set(mapObject.bounds)

                            val se = Mapper.STATUS_EFFECT_MAPPER.get(entity)

                            if (!se!!.entityApplied && se.type != StatusEffect.None) se.finish()

                            handleGroundedMapObject(mapObject, player, se)
                            handleSlowMapObject(mapObject, velocity, se)
                            handlePushRightMapObject(mapObject, velocity, se)
                            handlePushLeftMapObject(mapObject, velocity, se)
                            handleJumpBoostMapObject(mapObject, player, se)
                        }
                        velocity.dy = 0f
                    }
                }
                for (mapEntity in mapEntities) {
                    val comp = Mapper.MAP_ENTITY_MAPPER.get(mapEntity)
                    val bounds = Mapper.BOUNDING_BOX_MAPPER.get(mapEntity)
                    if (comp.mapCollidable && bb.rect.overlaps(bounds.rect)) {
                        revertCurrentPosition(position)
                        if (velocity.dy < 0 || (gravity.reverse && velocity.dy > 0 )) {
                            gravity.onGround = true
                            gravity.platform.set(bounds.rect)
                            player?.canJump = true
                        }
                        velocity.dy = 0f
                    }
                }
                for (mplatform in movingPlatforms) {
                    val bounds = Mapper.BOUNDING_BOX_MAPPER.get(mplatform)
                    if (bb.rect.overlaps(bounds.rect)) {
                        revertCurrentPosition(position)
                        if ((velocity.dy < 0 || (gravity.reverse && velocity.dy > 0)) &&
                                bb.rect.x + bb.rect.width > bounds.rect.x &&
                                bb.rect.x < bounds.rect.x + bounds.rect.width) {
                            gravity.onGround = true
                            gravity.onMovingPlatform = true
                            gravity.platform.set(bounds.rect)
                            player?.canJump = true
                        }
                        velocity.dy = 0f

                        val vel = Mapper.VEL_MAPPER.get(mplatform)
                        if (gravity.onMovingPlatform) {
                            if ((velocity.dx < 0 && vel.dx > 0) || (velocity.dx > 0 && vel.dx < 0))
                                velocity.platformDx = velocity.dx / 2 - vel.dx
                            else
                                velocity.platformDx = if (velocity.dx != 0f) vel.dx + velocity.dx / 2 else vel.dx + velocity.dx
                        }
                    }
                }
            }
        }
        if (velocity.dy != 0f) {
            gravity.onGround = false
            gravity.onMovingPlatform = false
            velocity.platformDx = 0f
        }

        if (Mapper.PROJ_MAPPER.get(entity) != null) return

        for (mapObject in mapObjects) {
            if (bb.rect.overlaps(mapObject.bounds)) {
                when (mapObject.type) {
                    MapObjectType.Lethal -> handleLethalMapObject(entity)
                    MapObjectType.Damage -> handleDamageMapObject(mapObject, entity)
                    else -> {}
                }
            }
        }

        if (startDamage[entity]!!) {
            damageTimes[entity!!] = damageTimes[entity]?.plus(dt)!!
            if (damageTimes[entity]!! >= MAP_OBJECT_DAMAGE_RATE) {
                damageTimes[entity] = 0f
                startDamage[entity] = false
            }
        }
    }

    fun setMapData(mapObjects: Array<MapObject>, mapWidth: Int, mapHeight: Int) {
        this.mapObjects.clear()
        this.mapObjects.addAll(mapObjects)
        this.mapWidth = mapWidth
        this.mapHeight = mapHeight

        damageTimes.clear()
        startDamage.clear()
        for (entity in entities) {
            damageTimes[entity] = 0f
            startDamage[entity] = false
        }
    }

    private fun handleLethalMapObject(entity: Entity?) {
        val health = Mapper.HEALTH_MAPPER.get(entity)
        health?.hp = 0

        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)
        val color = Mapper.COLOR_MAPPER.get(entity)
        ParticleSpawner.spawn(res, color.hex!!, DEFAULT_LIFETIME, DEFAULT_INTESITY + health.maxHp,
                bounds.rect.x + bounds.rect.width / 2, bounds.rect.y + bounds.rect.height / 2)
    }

    private fun handleDamageMapObject(mapObject: MapObject, entity: Entity?) {
        if (damageTimes[entity] == 0f) {
            val health = Mapper.HEALTH_MAPPER.get(entity)
            health?.hit(mapObject.damage)
            startDamage[entity!!] = true

            val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)
            val color = Mapper.COLOR_MAPPER.get(entity)
            ParticleSpawner.spawn(res, color.hex!!, DEFAULT_LIFETIME, DEFAULT_INTESITY + mapObject.damage,
                    bounds.rect.x + bounds.rect.width / 2, bounds.rect.y + bounds.rect.height / 2)
        }
    }

    private fun handleGroundedMapObject(mapObject: MapObject, player: PlayerComponent?, se: StatusEffectComponent?) {
        val grounded = mapObject.type == MapObjectType.Grounded
        if (player != null) {
            player.canJump = !grounded
            if (grounded) {
                player.canDoubleJump = false
                if (!se!!.entityApplied) se.apply(StatusEffect.Grounded)
            }
        }
    }

    private fun handleSlowMapObject(mapObject: MapObject, velocity: VelocityComponent, se: StatusEffectComponent?) {
        if (mapObject.type == MapObjectType.Slow) {
            if (velocity.dx > 0) velocity.dx = velocity.speed * MAP_OBJECT_SLOW_PERCENTAGE
            else if (velocity.dx < 0) velocity.dx = -velocity.speed * MAP_OBJECT_SLOW_PERCENTAGE

            if (!se!!.entityApplied) se.apply(StatusEffect.Slow)
        }
        else {
            if (velocity.dx != 0f && Math.abs(velocity.dx) == velocity.speed * MAP_OBJECT_SLOW_PERCENTAGE) {
                if (velocity.dx > 0) velocity.dx = velocity.speed
                else if (velocity.dx < 0) velocity.dx = -velocity.speed
            }
        }
    }

    private fun handlePushRightMapObject(mapObject: MapObject, velocity: VelocityComponent, se: StatusEffectComponent?) {
        if (mapObject.type == MapObjectType.PushRight) {
            if (velocity.dx > 0 && velocity.dx == velocity.speed) velocity.dx += MAP_OBJECT_PUSH
            if (!se!!.entityApplied) se.apply(StatusEffect.SpeedBoostRight)
        }
        else if (velocity.dx > 0 && velocity.dx == velocity.speed + MAP_OBJECT_PUSH) velocity.dx = velocity.speed
    }

    private fun handlePushLeftMapObject(mapObject: MapObject, velocity: VelocityComponent, se: StatusEffectComponent?) {
        if (mapObject.type == MapObjectType.PushLeft) {
            if (velocity.dx < 0 && velocity.dx == -velocity.speed) velocity.dx -= MAP_OBJECT_PUSH
            if (!se!!.entityApplied) se.apply(StatusEffect.SpeedBoostLeft)
        }
        else if (velocity.dx < 0 && velocity.dx == -velocity.speed - MAP_OBJECT_PUSH) velocity.dx = -velocity.speed
    }

    private fun handleJumpBoostMapObject(mapObject: MapObject, player: PlayerComponent?, se: StatusEffectComponent?) {
        player?.hasJumpBoost = mapObject.type == MapObjectType.JumpBoost
        if (player != null && mapObject.type == MapObjectType.JumpBoost) {
            if (!se!!.entityApplied) se.apply(StatusEffect.JumpBoost)
        }
    }

    private fun savePreviousPosition(position: PositionComponent) {
        position.setPrev(position.x, position.y)
    }

    private fun revertCurrentPosition(position: PositionComponent) {
        position.set(position.prevX, position.prevY)
    }

}