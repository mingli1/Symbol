package com.symbol.input

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.player.PlayerComponent

class KeyInputSystem : EntitySystem(), KeyInputHandler {

    private lateinit var player: Entity

    override fun addedToEngine(engine: Engine?) {
        player = engine!!.getEntitiesFor(Family.all(PlayerComponent::class.java).get()).get(0)
    }

    override fun move(right: Boolean) {
        val vel = Mapper.VEL_MAPPER.get(player)
        val speed = Mapper.SPEED_MAPPER.get(player)
        vel.move(right, speed.speed)
    }

    override fun stop(right: Boolean) {
        val vel = Mapper.VEL_MAPPER.get(player)
        when (right) {
            true -> if (vel.dx > 0) vel.dx = 0f
            false -> if (vel.dx < 0) vel.dx = 0f
        }
    }

    override fun jump() {}

    override fun shoot() {}

}