package com.symbol.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.utils.Array
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.BoundingBoxComponent
import com.symbol.ecs.component.GravityComponent
import com.symbol.ecs.component.PositionComponent
import com.symbol.ecs.component.RemoveComponent
import com.symbol.map.MapObject
import com.symbol.map.MapObjectType

private const val NUM_SUB_STEPS = 30

class MapCollisionSystem : IteratingSystem(
        Family.all(BoundingBoxComponent::class.java, GravityComponent::class.java).get()
) {

    private var mapObjects: Array<MapObject> = Array()
    private var mapWidth: Int = 0
    private var mapHeight: Int = 0

    private var stepX: Float = 0f
    private var stepY: Float = 0f

    private lateinit var removableEntities: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        removableEntities = engine!!.getEntitiesFor(Family.all(RemoveComponent::class.java).get())
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

        stepX = velocity.dx * dt / NUM_SUB_STEPS
        for (i in 0 until NUM_SUB_STEPS) {
            savePreviousPosition(position)
            position.x += stepX
            bb.rect.setPosition(position.x + (width - bb.rect.width) / 2, position.y + (height - bb.rect.height) / 2)
            for (mapObject in mapObjects) {
                if (mapObject.type == MapObjectType.Ground && bb.rect.overlaps(mapObject.bounds)) {
                    revertCurrentPosition(position)
                }
            }
        }

        stepY = velocity.dy * dt / NUM_SUB_STEPS
        for (i in 0 until NUM_SUB_STEPS) {
            savePreviousPosition(position)
            position.y += stepY
            bb.rect.setPosition(position.x + (width - bb.rect.width) / 2, position.y + (height - bb.rect.height) / 2)
            for (mapObject in mapObjects) {
                if (mapObject.type == MapObjectType.Ground && bb.rect.overlaps(mapObject.bounds)) {
                    revertCurrentPosition(position)
                    if (velocity.dy < 0) {
                        gravity.onGround = true
                        gravity.platform.set(mapObject.bounds)
                    }
                    velocity.dy = 0f
                }
            }
        }
        if (velocity.dy != 0f) gravity.onGround = false
    }

    fun setMapData(mapObjects: Array<MapObject>, mapWidth: Int, mapHeight: Int) {
        this.mapObjects.clear()
        this.mapObjects.addAll(mapObjects)
        this.mapWidth = mapWidth
        this.mapHeight = mapHeight
    }

    private fun savePreviousPosition(position: PositionComponent) {
        position.setPrev(position.x, position.y)
    }

    private fun revertCurrentPosition(position: PositionComponent) {
        position.set(position.prevX, position.prevY)
    }

}