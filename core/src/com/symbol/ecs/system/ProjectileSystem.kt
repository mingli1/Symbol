package com.symbol.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.symbol.ecs.EntityBuilder
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.*
import com.symbol.ecs.component.map.MapEntityComponent
import com.symbol.ecs.entity.MapEntityType
import com.symbol.effects.particle.DEFAULT_INTESITY
import com.symbol.effects.particle.DEFAULT_LIFETIME
import com.symbol.effects.particle.DEFAULT_VX_SCALING
import com.symbol.effects.particle.ParticleSpawner
import com.symbol.map.MapObject
import com.symbol.util.Resources

const val DIAGONAL_PROJECTILE_SCALING = 0.75f
private const val KNOCKBACK_TIME = 0.1f

class ProjectileSystem(private val res: Resources) : IteratingSystem(Family.all(ProjectileComponent::class.java).get()) {

    private var mapObjects: Array<MapObject> = Array()

    private lateinit var allEntities: ImmutableArray<Entity>
    private lateinit var mapEntities: ImmutableArray<Entity>

    private var knockbackTimes: MutableMap<Entity, Float> = HashMap()
    private var prevVelocities: MutableMap<Entity, Float> = HashMap()
    private var startKnockback: MutableMap<Entity, Boolean> = HashMap()

    private var waveTimers: MutableMap<Entity, Float> = HashMap()

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        allEntities = engine!!.getEntitiesFor(Family.all(HealthComponent::class.java).get())
        mapEntities = engine.getEntitiesFor(Family.all(MapEntityComponent::class.java).get())
    }

    fun setMapData(mapObjects: Array<MapObject>) {
        this.mapObjects.clear()
        this.mapObjects.addAll(mapObjects)

        knockbackTimes.clear()
        prevVelocities.clear()
        startKnockback.clear()
        waveTimers.clear()
        for (entity in allEntities) {
            knockbackTimes[entity] = 0f
            prevVelocities[entity] = 0f
            startKnockback[entity] = false
        }
    }

    override fun update(dt: Float) {
        super.update(dt)

        for (e in allEntities) {
            val vel = Mapper.VEL_MAPPER.get(e)
            if (startKnockback[e]!!) {
                val knockback = Mapper.KNOCKBACK_MAPPER.get(e)
                knockbackTimes[e] = knockbackTimes[e]?.plus(dt)!!
                if (knockbackTimes[e]!! > KNOCKBACK_TIME) {
                    vel.dx = prevVelocities[e]!!
                    knockbackTimes[e] = 0f
                    startKnockback[e] = false
                    knockback.knockingBack = false
                }
            }
        }
    }

    override fun processEntity(entity: Entity?, dt: Float) {
        val pj = Mapper.PROJ_MAPPER.get(entity)
        val color = Mapper.COLOR_MAPPER.get(entity)
        val bb = Mapper.BOUNDING_BOX_MAPPER.get(entity)
        val position = Mapper.POS_MAPPER.get(entity)
        val velocity = Mapper.VEL_MAPPER.get(entity)
        val width = Mapper.TEXTURE_MAPPER.get(entity).texture!!.regionWidth
        val height = Mapper.TEXTURE_MAPPER.get(entity).texture!!.regionHeight
        val remove = Mapper.REMOVE_MAPPER.get(entity)
        bb.rect.setPosition(position.x + (width - bb.rect.width) / 2, position.y + (height - bb.rect.height) / 2)

        pj.lifeTime += dt

        if (pj.acceleration != 0f && pj.movementType == ProjectileMovementType.Normal) {
            if (velocity.dx != 0f) velocity.dx += if (velocity.dx > 0f) pj.acceleration * dt else -pj.acceleration * dt
            if (velocity.dy != 0f) velocity.dy += if (velocity.dy > 0f) pj.acceleration * dt else -pj.acceleration * dt
        }

        if (pj.collidesWithTerrain) {
            for (mapObject in mapObjects) {
                if (bb.rect.overlaps(mapObject.bounds)) {
                    remove.shouldRemove = true
                    ParticleSpawner.spawn(res, color.hex!!,
                            DEFAULT_LIFETIME, DEFAULT_INTESITY + pj.damage,
                            position.x + width / 2,
                            position.y + height / 2)
                    break
                }
            }
            for (mapEntity in mapEntities) {
                val me = Mapper.MAP_ENTITY_MAPPER.get(mapEntity)
                val bounds = Mapper.BOUNDING_BOX_MAPPER.get(mapEntity)
                if (me.projectileCollidable) {
                    if (bb.rect.overlaps(bounds.rect)) {
                        ParticleSpawner.spawn(res, color.hex!!,
                                DEFAULT_LIFETIME, DEFAULT_INTESITY + pj.damage,
                                position.x + width / 2,
                                position.y + height / 2)
                        remove.shouldRemove = true
                        break
                    }
                }
                if (me.mapEntityType == MapEntityType.Mirror && !pj.enemy) {
                    if (bb.rect.overlaps(bounds.rect)) {
                        pj.enemy = true
                        velocity.dx = -velocity.dx
                        break
                    }
                }
            }
        }

        for (projectile in entities) {
            val projectileComp = Mapper.PROJ_MAPPER.get(projectile)
            if (entity!! != projectile && projectileComp.collidesWithProjectiles) {
                val bounds = Mapper.BOUNDING_BOX_MAPPER.get(projectile)
                if (bb.rect.overlaps(bounds.rect)) {
                    remove.shouldRemove = true
                    val projectileRemove = Mapper.REMOVE_MAPPER.get(projectile)
                    projectileRemove.shouldRemove = true
                    break
                }
            }
        }

        for (e in allEntities) {
            val ebb = Mapper.BOUNDING_BOX_MAPPER.get(e)
            val ev = Mapper.VEL_MAPPER.get(e)

            if (bb.rect.overlaps(ebb.rect)) {
                val knockback = Mapper.KNOCKBACK_MAPPER.get(e)
                val player = Mapper.PLAYER_MAPPER.get(e)

                if ((pj.enemy && player != null) || (!pj.enemy && player == null)) {
                    if (knockback != null) {
                        prevVelocities[e] = ev.dx
                        ev.dx = if (bb.rect.x < ebb.rect.x + ebb.rect.width / 2) pj.knockback else -pj.knockback
                        startKnockback[e] = true
                        knockback.knockingBack = true
                    }
                    val enemy = Mapper.ENEMY_MAPPER.get(e)
                    if (enemy != null && !enemy.corporeal) break

                    hit(e, pj.damage)

                    val entityColor = Mapper.COLOR_MAPPER.get(e)
                    ParticleSpawner.spawn(res, entityColor.hex!!,
                            DEFAULT_LIFETIME, DEFAULT_INTESITY + pj.damage,
                            ebb.rect.x + ebb.rect.width / 2,
                            ebb.rect.y + ebb.rect.height / 2)

                    remove.shouldRemove = true
                    break
                }
            }
        }

        when (pj.movementType) {
            ProjectileMovementType.Normal -> {}
            ProjectileMovementType.Arc -> handleArcMovement(entity, dt, pj)
            ProjectileMovementType.Wave -> handleWaveMovement(entity, dt, pj)
        }

        handleDetonation(entity, pj, bb.rect, remove)
    }

    private fun hit(entity: Entity, damage: Int) {
        val health = Mapper.HEALTH_MAPPER.get(entity)
        health.hit(damage)

        handleTeleportation(entity)
        handleLastStand(entity)
    }

    private fun handleDetonation(entity: Entity?, pj: ProjectileComponent, bounds: Rectangle, remove: RemoveComponent) {
        if (pj.enemy && !pj.collidesWithTerrain && pj.detonateTime != 0f) {
            if (pj.lifeTime >= pj.detonateTime) {
                val vel = Mapper.VEL_MAPPER.get(entity)
                val speed = if (vel.dx != 0f) Math.abs(vel.dx) else Math.abs(vel.dy)
                val texture = res.getSubProjectileTextureFor(pj.textureStr!!)!!

                createSubProjectile(pj, bounds, speed, 0f, texture)
                createSubProjectile(pj, bounds, speed * DIAGONAL_PROJECTILE_SCALING, -speed * DIAGONAL_PROJECTILE_SCALING, texture)
                createSubProjectile(pj, bounds, 0f, -speed, texture)
                createSubProjectile(pj, bounds, -speed * DIAGONAL_PROJECTILE_SCALING, -speed * DIAGONAL_PROJECTILE_SCALING, texture)
                createSubProjectile(pj, bounds, -speed, 0f, texture)
                createSubProjectile(pj, bounds, -speed * DIAGONAL_PROJECTILE_SCALING, speed * DIAGONAL_PROJECTILE_SCALING, texture)
                createSubProjectile(pj, bounds, 0f, speed, texture)
                createSubProjectile(pj, bounds, speed * DIAGONAL_PROJECTILE_SCALING, speed * DIAGONAL_PROJECTILE_SCALING, texture)

                remove.shouldRemove = true
            }
        }
    }

    private fun handleArcMovement(entity: Entity?, dt: Float, pj: ProjectileComponent) {
        val vel = Mapper.VEL_MAPPER.get(entity)
        val ay = (pj.acceleration / 1.5f) * dt
        vel.dx += if (pj.parentFacingRight) pj.acceleration * dt else -pj.acceleration * dt

        if (!pj.arcHalf) {
            if (vel.dy > 0) {
                vel.dy -= ay
                if (vel.dy < 0) pj.arcHalf = true
            } else if (vel.dy < 0) {
                vel.dy += ay
                if (vel.dy > 0) pj.arcHalf = true
            }
        }
        else {
            if (vel.dy < 0) vel.dy -= ay
            else if (vel.dy > 0) vel.dy += ay
        }
    }

    private fun handleWaveMovement(entity: Entity?, dt: Float, pj: ProjectileComponent) {
        if (waveTimers[entity] == null) waveTimers[entity!!] = 0f
        waveTimers[entity!!] = waveTimers[entity]?.plus(dt * (pj.acceleration / 10f))!!
        if (waveTimers[entity]!! >= MathUtils.PI2) waveTimers[entity] = 0f
        val offset = MathUtils.sin(waveTimers[entity]!!) * pj.acceleration

        val velocity = Mapper.VEL_MAPPER.get(entity)

        when (pj.waveDir) {
            Direction.Left, Direction.Right -> velocity.dy = offset
            Direction.Up, Direction.Down -> velocity.dx = offset
        }
    }

    private fun createSubProjectile(pj: ProjectileComponent, bounds: Rectangle,
                                 dx: Float = 0f, dy: Float = 0f, texture: TextureRegion) {
        val bw = texture.regionWidth - 1
        val bh = texture.regionHeight - 1
        EntityBuilder.instance(engine as PooledEngine)
                .projectile(collidesWithTerrain = false, enemy = true, damage = pj.damage)
                .position(bounds.x + (bounds.width / 2) - (bw / 2), bounds.y + (bounds.height / 2) - (bh / 2))
                .velocity(dx = dx, dy = dy)
                .boundingBox(bw.toFloat(), bh.toFloat())
                .texture(texture)
                .direction(yFlip = true).remove().build()
    }

    private fun handleTeleportation(entity: Entity?) {
        val enemyComp = Mapper.ENEMY_MAPPER.get(entity)
        if (enemyComp != null) {
            if (enemyComp.teleportOnHit) {
                val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)
                val position = Mapper.POS_MAPPER.get(entity)
                val velocity = Mapper.VEL_MAPPER.get(entity)

                val platform = mapObjects.random().bounds
                val randX = MathUtils.random(platform.x, platform.x + platform.width - bounds.rect.width)
                val newY = platform.y + platform.height + bounds.rect.height / 2

                position.set(randX, newY)
                velocity.dx = 0f
            }
        }
    }

    private fun handleLastStand(entity: Entity?) {
        val enemyComp = Mapper.ENEMY_MAPPER.get(entity)
        if (enemyComp != null) {
            if (enemyComp.lastStand) {
                val health = Mapper.HEALTH_MAPPER.get(entity)
                val scale = 1f / health.maxHp
                enemyComp.attackRate -= enemyComp.attackRate * scale
            }
        }
    }

}