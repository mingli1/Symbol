package com.symbol.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.DirectionComponent

class DirectionSystem : IteratingSystem(Family.all(DirectionComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val vel = Mapper.VEL_MAPPER.get(entity)
        val dir = Mapper.DIR_MAPPER.get(entity)

        if (vel.dx > 0) dir.facingRight = true
        else if (vel.dx < 0) dir.facingRight = false
    }

}