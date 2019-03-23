package com.symbol.game.input

import com.badlogic.ashley.core.*
import com.symbol.game.ecs.EntityBuilder
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.PlayerComponent
import com.symbol.game.ecs.component.VelocityComponent
import com.symbol.game.ecs.entity.*
import com.symbol.game.ecs.system.MAP_OBJECT_JUMP_BOOST_PERCENTAGE
import com.symbol.game.util.Resources

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
        vel.move(right)
    }

    override fun stop(right: Boolean) {
        when (right) {
            true -> if (vel.dx > 0) vel.dx = 0f
            false -> if (vel.dx < 0) vel.dx = 0f
        }
    }

    override fun jump() {
        val gravity = Mapper.GRAVITY_MAPPER.get(player)
        val jump = Mapper.JUMP_MAPPER.get(player)

        if (gravity.onGround && playerComp.canJump) {
            if (gravity.reverse)
                vel.dy = if (playerComp.hasJumpBoost) -jump.impulse * MAP_OBJECT_JUMP_BOOST_PERCENTAGE else -jump.impulse
            else
                vel.dy = if (playerComp.hasJumpBoost) jump.impulse * MAP_OBJECT_JUMP_BOOST_PERCENTAGE else jump.impulse
            playerComp.canJump = false
            playerComp.canDoubleJump = true
        }
        else if (playerComp.canDoubleJump) {
            vel.dy = if (gravity.reverse) -jump.impulse else jump.impulse
            playerComp.canDoubleJump = false
        }
    }

    override fun shoot() {
        if (playerComp.canShoot) {
            val playerPos = Mapper.POS_MAPPER.get(player)
            val dir = Mapper.DIR_MAPPER.get(player)

            EntityBuilder.instance(engine as PooledEngine)
                    .projectile(damage = PLAYER_DAMAGE, knockback = PLAYER_PROJECTILE_KNOCKBACK)
                    .color(EntityColor.DOT_COLOR)
                    .position(playerPos.x + (PLAYER_WIDTH / 2) - (PLAYER_PROJECTILE_BOUNDS_WIDTH / 2),
                            playerPos.y + (PLAYER_HEIGHT / 2) - (PLAYER_PROJECTILE_BOUNDS_HEIGHT / 2))
                    .velocity(dx = if (dir.facingRight) PLAYER_PROJECTILE_SPEED else -PLAYER_PROJECTILE_SPEED)
                    .boundingBox(PLAYER_PROJECTILE_BOUNDS_WIDTH, PLAYER_PROJECTILE_BOUNDS_HEIGHT)
                    .texture(res.getTexture(PLAYER_PROJECTILE_RES_KEY)!!)
                    .direction().remove().build()

            playerComp.canShoot = false
        }
    }

}