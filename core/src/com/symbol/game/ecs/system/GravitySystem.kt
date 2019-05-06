package com.symbol.game.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.GravityComponent

const val GRAVITY = -750.8f
const val TERMINAL_VELOCITY = -80.8f

class GravitySystem : IteratingSystem(Family.all(GravityComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, dt: Float) {
        val vel = Mapper.VEL_MAPPER.get(entity)
        val grav = Mapper.GRAVITY_MAPPER.get(entity)

        if (grav.reverse) {
            if (vel.dy < -grav.terminalVelocity) vel.dy -= grav.gravity * dt
            else vel.dy = -grav.terminalVelocity
        }
        else {
            if (vel.dy > grav.terminalVelocity) vel.dy += grav.gravity * dt
            else vel.dy = grav.terminalVelocity
        }
    }

}