package com.symbol.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.GravityComponent
import com.symbol.ecs.component.PositionComponent
import com.symbol.ecs.component.VelocityComponent

class MovementSystem : IteratingSystem(
        Family.all(PositionComponent::class.java, VelocityComponent::class.java).exclude(GravityComponent::class.java).get()
) {

    override fun processEntity(entity: Entity?, dt: Float) {
        val position = Mapper.POS_MAPPER.get(entity)
        val velocity = Mapper.VEL_MAPPER.get(entity)
        val orbit = Mapper.ORBIT_MAPPER.get(entity)

        if (orbit != null) {
            orbit.angle += if (!orbit.clockwise) orbit.speed * dt else -orbit.speed * dt
            if (orbit.angle >= MathUtils.PI2) orbit.angle -= MathUtils.PI2

            position.x = orbit.originX + MathUtils.cos(orbit.angle) * orbit.radius
            position.y = orbit.originY + MathUtils.sin(orbit.angle) * orbit.radius
        }
        else {
            position.x += velocity.dx * dt
            position.y += velocity.dy * dt
        }
    }

}