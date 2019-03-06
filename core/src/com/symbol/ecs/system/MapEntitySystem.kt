package com.symbol.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.map.MapEntityComponent
import com.symbol.ecs.entity.MapEntityType
import com.symbol.ecs.entity.Player

class MapEntitySystem(private val player: Player) : IteratingSystem(Family.all(MapEntityComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, dt: Float) {
        val mapEntityComponent = Mapper.MAP_ENTITY_MAPPER.get(entity)
        when (mapEntityComponent.mapEntityType) {
            MapEntityType.None -> return
            MapEntityType.MovingPlatform -> handleMovingPlatform(entity)
            MapEntityType.TemporaryPlatform -> handleTempPlatform(entity)
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
    }

    private fun handleTempPlatform(entity: Entity?) {
        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)
        val remove = Mapper.REMOVE_MAPPER.get(entity)
        val playerBounds = Mapper.BOUNDING_BOX_MAPPER.get(player)
        val playerVel = Mapper.VEL_MAPPER.get(player)
        val playerComp = Mapper.PLAYER_MAPPER.get(player)

        if (playerBounds.rect.overlaps(bounds.rect)) {
            remove.shouldRemove = true
            playerComp.canDoubleJump = true
            playerVel.dy = 0f
        }
    }

}