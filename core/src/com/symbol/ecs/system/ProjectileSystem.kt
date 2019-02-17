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
import com.symbol.ecs.component.EnemyComponent
import com.symbol.ecs.component.HealthComponent
import com.symbol.ecs.component.ProjectileComponent
import com.symbol.ecs.component.RemoveComponent
import com.symbol.map.MapObject
import com.symbol.util.Resources

const val DIAGONAL_PROJECTILE_SCALING = 0.75f
private const val KNOCKBACK_TIME = 0.1f

class ProjectileSystem(private val res: Resources) : IteratingSystem(Family.all(ProjectileComponent::class.java).get()) {

    private var mapObjects: Array<MapObject> = Array()

    private lateinit var allEntities: ImmutableArray<Entity>

    private var knockbackTimes: MutableMap<Entity, Float> = HashMap()
    private var prevVelocities: MutableMap<Entity, Float> = HashMap()
    private var startKnockback: MutableMap<Entity, Boolean> = HashMap()

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        allEntities = engine!!.getEntitiesFor(Family.all(HealthComponent::class.java).get())
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
        val bb = Mapper.BOUNDING_BOX_MAPPER.get(entity)
        val position = Mapper.POS_MAPPER.get(entity)
        val width = Mapper.TEXTURE_MAPPER.get(entity).texture!!.regionWidth
        val height = Mapper.TEXTURE_MAPPER.get(entity).texture!!.regionHeight
        val remove = Mapper.REMOVE_MAPPER.get(entity)
        bb.rect.setPosition(position.x + (width - bb.rect.width) / 2, position.y + (height - bb.rect.height) / 2)

        pj.lifeTime += dt

        if (!pj.unstoppable) {
            for (mapObject in mapObjects) {
                if (bb.rect.overlaps(mapObject.bounds)) {
                    remove.shouldRemove = true
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
                    hit(e, pj.damage)
                    remove.shouldRemove = true
                    break
                }
            }
        }

        handleDetonation(entity, pj, bb.rect, remove)
    }

    fun setMapData(mapObjects: Array<MapObject>) {
        this.mapObjects.clear()
        this.mapObjects.addAll(mapObjects)

        knockbackTimes.clear()
        prevVelocities.clear()
        startKnockback.clear()
        for (entity in allEntities) {
            knockbackTimes[entity] = 0f
            prevVelocities[entity] = 0f
            startKnockback[entity] = false
        }
    }

    private fun hit(entity: Entity, damage: Int) {
        val health = Mapper.HEALTH_MAPPER.get(entity)
        health.hp -= damage

        handleTeleportation(entity)
    }

    private fun handleDetonation(entity: Entity?, pj: ProjectileComponent, bounds: Rectangle, remove: RemoveComponent) {
        if (pj.enemy && pj.unstoppable && pj.detonateTime != 0f) {
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

    private fun createSubProjectile(pj: ProjectileComponent, bounds: Rectangle,
                                 dx: Float = 0f, dy: Float = 0f, texture: TextureRegion) {
        val bw = texture.regionWidth - 1
        val bh = texture.regionHeight - 1
        EntityBuilder.instance(engine as PooledEngine)
                .projectile(unstoppable = true, enemy = true, damage = pj.damage)
                .position(bounds.x + (bounds.width / 2) - (bw / 2), bounds.y + (bounds.height / 2) - (bh / 2))
                .velocity(dx = dx, dy = dy)
                .boundingBox(bw.toFloat(), bh.toFloat())
                .texture(texture)
                .direction(yFlip = true).remove().build()
    }

    private fun handleTeleportation(entity: Entity?) {
        val enemyComp = Mapper.ENEMY_MAPPER.get(entity)
        if (enemyComp != null) {
            val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)
            val position = Mapper.POS_MAPPER.get(entity)
            val velocity = Mapper.VEL_MAPPER.get(entity)
            if (enemyComp.teleportOnHit) {
                val platform = mapObjects.random().bounds
                val randX = MathUtils.random(platform.x, platform.x + platform.width - bounds.rect.width)
                val newY = platform.y + platform.height + bounds.rect.height / 2

                position.set(randX, newY)
                velocity.dx = 0f
            }
        }
    }

}