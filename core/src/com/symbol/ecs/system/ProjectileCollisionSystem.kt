package com.symbol.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.utils.Array
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.HealthComponent
import com.symbol.ecs.component.projectile.ProjectileComponent
import com.symbol.ecs.entity.Player
import com.symbol.map.MapObject

private const val KNOCKBACK_TIME = 0.1f

class ProjectileCollisionSystem : IteratingSystem(Family.all(ProjectileComponent::class.java).get()) {

    private var mapObjects: Array<MapObject> = Array()
    private var mapWidth: Int = 0
    private var mapHeight: Int = 0

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

        if (position.x < -mapWidth - width || position.x > mapWidth * 2 ||
                position.y < -mapHeight - height || position.y > mapHeight * 2) {
            remove.shouldRemove = true
        }
        else if (!pj.unstoppable) {
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
                if (knockback != null) {
                    prevVelocities[e] = ev.dx
                    ev.dx = if (bb.rect.x < ebb.rect.x + ebb.rect.width / 2) pj.knockback else -pj.knockback
                    startKnockback[e] = true
                    knockback.knockingBack = true

                    hit(e, pj.damage)
                    remove.shouldRemove = true
                    break
                }
                else if (!pj.enemy || e is Player) {
                    hit(e, pj.damage)
                    remove.shouldRemove = true
                    break
                }
            }
        }
    }

    fun setMapData(mapObjects: Array<MapObject>, mapWidth: Int, mapHeight: Int) {
        this.mapObjects.clear()
        this.mapObjects.addAll(mapObjects)
        this.mapWidth = mapWidth
        this.mapHeight = mapHeight

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
    }

}