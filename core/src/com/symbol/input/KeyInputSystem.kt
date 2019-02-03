package com.symbol.input

import com.badlogic.ashley.core.*
import com.symbol.ecs.EntityFactory
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.VelocityComponent
import com.symbol.ecs.component.player.PlayerComponent
import com.symbol.ecs.entity.*
import com.symbol.util.Resources

class KeyInputSystem(private val res: Resources) : EntitySystem(), KeyInputHandler {

    private lateinit var player: Entity
    private lateinit var playerComp: PlayerComponent
    private lateinit var vel: VelocityComponent

    override fun addedToEngine(engine: Engine?) {
        player = engine!!.getEntitiesFor(Family.all(PlayerComponent::class.java).get()).get(0)
        playerComp = Mapper.PLAYER_MAPPER.get(player)
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

        if (gravity.onGround) {
            vel.dy = PLAYER_JUMP_IMPULSE
            playerComp.canDoubleJump = true
        }
        else if (playerComp.canDoubleJump) {
            vel.dy = PLAYER_JUMP_IMPULSE
            playerComp.canDoubleJump = false
        }
    }

    override fun shoot(keyDown: Boolean) {
        if (keyDown && playerComp.canShoot) {
            val playerPos = Mapper.POS_MAPPER.get(player)
            val dir = Mapper.DIR_MAPPER.get(player)

            EntityFactory.createProjectile(engine as PooledEngine, false, false,
                    if (dir.facingRight) playerPos.x + PLAYER_WIDTH else playerPos.x - PLAYER_PROJECTILE_BOUNDS_WIDTH,
                    playerPos.y + (PLAYER_HEIGHT / 2) - (PLAYER_PROJECTILE_BOUNDS_HEIGHT / 2),
                    if (dir.facingRight) PLAYER_PROJECTILE_SPEED else -PLAYER_PROJECTILE_SPEED, 0f,
                    PLAYER_PROJECTILE_BOUNDS_WIDTH, PLAYER_PROJECTILE_BOUNDS_HEIGHT,
                    res.getSingleTexture(PLAYER_PROJECTILE_RES_KEY)!!)

            playerComp.canShoot = false
        }
        else if (!keyDown) {
            playerComp.canShoot = true
        }
    }

}