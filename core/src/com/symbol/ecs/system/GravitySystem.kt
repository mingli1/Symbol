package com.symbol.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.GravityComponent

const val GRAVITY = -750.8f
const val TERMINAL_VELOCITY = -80.8f

class GravitySystem : IteratingSystem(Family.all(GravityComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val vel = Mapper.VEL_MAPPER.get(entity)
        val grav = Mapper.GRAVITY_MAPPER.get(entity)

        if (vel.dy > grav.terminalVelocity) vel.dy += grav.gravity * deltaTime
        else vel.dy = grav.terminalVelocity
    }

}