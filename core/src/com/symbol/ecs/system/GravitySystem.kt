package com.symbol.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.GravityComponent

private const val GRAVITY = -9.8f
private const val TERMINAL_VELOCITY = -32.8f

class GravitySystem : IteratingSystem(Family.all(GravityComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val vel = Mapper.VEL_MAPPER.get(entity)

        if (vel.dy > TERMINAL_VELOCITY) vel.dy += GRAVITY
        else vel.dy = TERMINAL_VELOCITY
    }

}