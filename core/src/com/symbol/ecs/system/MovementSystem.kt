package com.symbol.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.BoundingBoxComponent
import com.symbol.ecs.component.PositionComponent
import com.symbol.ecs.component.VelocityComponent

class MovementSystem : IteratingSystem(
        Family.all(PositionComponent::class.java, VelocityComponent::class.java).exclude(BoundingBoxComponent::class.java).get()
) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val position = Mapper.POS_MAPPER.get(entity)
        val prevPosition = Mapper.PREV_POS_MAPPER.get(entity)
        val velocity = Mapper.VEL_MAPPER.get(entity)

        prevPosition.x = position.x
        prevPosition.y = position.y

        position.x += velocity.dx * deltaTime
        position.y += velocity.dy * deltaTime
    }

}