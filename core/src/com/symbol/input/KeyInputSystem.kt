package com.symbol.input

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.VelocityComponent
import com.symbol.ecs.component.player.PlayerComponent
import com.symbol.ecs.entity.PLAYER_JUMP_IMPULSE

class KeyInputSystem : EntitySystem(), KeyInputHandler {

    private lateinit var player: Entity
    private lateinit var vel: VelocityComponent

    override fun addedToEngine(engine: Engine?) {
        player = engine!!.getEntitiesFor(Family.all(PlayerComponent::class.java).get()).get(0)
        vel = Mapper.VEL_MAPPER.get(player)
    }

    override fun move(right: Boolean) {
        val speed = Mapper.SPEED_MAPPER.get(player)
        vel.move(right, speed.speed)
    }

    override fun stop(right: Boolean) {
        when (right) {
            true -> if (vel.dx > 0) vel.dx = 0f
            false -> if (vel.dx < 0) vel.dx = 0f
        }
    }

    override fun jump() {
        val gravity = Mapper.GRAVITY_MAPPER.get(player)
        val playerComp = Mapper.PLAYER_MAPPER.get(player)

        if (gravity.onGround) {
            vel.dy = PLAYER_JUMP_IMPULSE
            playerComp.canDoubleJump = true
        }
        else if (playerComp.canDoubleJump) {
            vel.dy = PLAYER_JUMP_IMPULSE
            playerComp.canDoubleJump = false
        }
    }

    override fun shoot() {}

}