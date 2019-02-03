package com.symbol.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.GravityComponent
import com.symbol.ecs.component.PositionComponent
import com.symbol.ecs.component.VelocityComponent

class MovementSystem : IteratingSystem(
        Family.all(PositionComponent::class.java, VelocityComponent::class.java).exclude(GravityComponent::class.java).get()
) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val position = Mapper.POS_MAPPER.get(entity)
        val velocity = Mapper.VEL_MAPPER.get(entity)

        position.x += velocity.dx * deltaTime
        position.y += velocity.dy * deltaTime
    }

}