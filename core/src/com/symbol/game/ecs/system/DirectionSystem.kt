package com.symbol.game.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.DirectionComponent

class DirectionSystem : IteratingSystem(Family.all(DirectionComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val vel = Mapper.VEL_MAPPER.get(entity)
        val dir = Mapper.DIR_MAPPER.get(entity)

        val knockback = Mapper.KNOCKBACK_MAPPER.get(entity)
        val knockingBack = knockback?.knockingBack ?: false

        if (vel.dx > 0 && !knockingBack) dir.facingRight = true
        else if (vel.dx < 0 && !knockingBack) dir.facingRight = false

        if (vel.dy > 0) dir.facingUp = true
        else if (vel.dy < 0) dir.facingUp = false
    }

}