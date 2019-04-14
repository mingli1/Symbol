package com.symbol.game.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.GravityComponent
import com.symbol.game.ecs.component.PositionComponent
import com.symbol.game.ecs.component.VelocityComponent

class MovementSystem : IteratingSystem(
        Family.all(PositionComponent::class.java, VelocityComponent::class.java).exclude(GravityComponent::class.java).get()
) {

    override fun processEntity(entity: Entity?, dt: Float) {
        val position = Mapper.POS_MAPPER.get(entity)
        val velocity = Mapper.VEL_MAPPER.get(entity)
        val orbit = Mapper.ORBIT_MAPPER.get(entity)
        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)
        val texture = Mapper.TEXTURE_MAPPER.get(entity)

        if (texture.texture == null) return

        val width = texture.texture!!.regionWidth
        val height = texture.texture!!.regionHeight

        bounds?.rect?.setPosition(position.x + (width - bounds.rect.width) / 2, position.y + (height - bounds.rect.height) / 2)

        if (orbit != null) {
            orbit.angle += if (!orbit.clockwise) orbit.speed * dt else -orbit.speed * dt
            if (orbit.angle >= MathUtils.PI2) orbit.angle -= MathUtils.PI2

            position.x = position.originX + MathUtils.cos(orbit.angle) * orbit.radius - bounds.rect.width / 2
            position.y = position.originY + MathUtils.sin(orbit.angle) * orbit.radius - bounds.rect.height / 2
        }
        else {
            position.x += velocity.dx * dt
            position.y += velocity.dy * dt
        }
    }

}