package com.symbol.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.map.MapEntityComponent
import com.symbol.ecs.entity.MapEntityType

class MapEntitySystem : IteratingSystem(Family.all(MapEntityComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, dt: Float) {
        val mapEntityComponent = Mapper.MAP_ENTITY_MAPPER.get(entity)
        when (mapEntityComponent.mapEntityType) {
            MapEntityType.None -> return
            MapEntityType.MovingPlatform -> handleMovingPlatform(entity)
        }
    }

    private fun handleMovingPlatform(entity: Entity?) {
        val mp = Mapper.MOVING_PLATFORM_MAPPER.get(entity)
        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)
        val position = Mapper.POS_MAPPER.get(entity)
        val velocity = Mapper.VEL_MAPPER.get(entity)

        if (velocity.dx != 0f) {
            if (mp.positive) {
                val trueX = position.x + bounds.rect.width
                if ((velocity.dx > 0 && trueX - mp.originX >= mp.distance) ||
                        (velocity.dx < 0 && position.x <= mp.originX)) {
                    velocity.dx = -velocity.dx
                }
            }
            else {
                if ((velocity.dx < 0 && mp.originX - position.x >= mp.distance) ||
                        (velocity.dx > 0 && position.x >= mp.originX)) {
                    velocity.dx = -velocity.dx
                }
            }
        }
        else if (velocity.dy != 0f) {
            if (mp.positive) {
                val trueY = position.y + bounds.rect.height
                if ((velocity.dy > 0 && trueY - mp.originY >= mp.distance) ||
                        (velocity.dy < 0 && position.y <= mp.originY)) {
                    velocity.dy = -velocity.dy
                }
            }
            else {
                if ((velocity.dy < 0 && mp.originY - position.y >= mp.distance) ||
                        (velocity.dy > 0 && position.y >= mp.originY)) {
                    velocity.dy = -velocity.dy
                }
            }
        }
    }

}