package com.symbol.game.input

import com.badlogic.ashley.core.*
import com.symbol.game.ecs.EntityBuilder
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.PlayerComponent
import com.symbol.game.ecs.component.StatusEffect
import com.symbol.game.ecs.component.VelocityComponent
import com.symbol.game.ecs.entity.*
import com.symbol.game.ecs.system.MAP_OBJECT_JUMP_BOOST_PERCENTAGE
import com.symbol.game.util.Resources

class KeyInputSystem(private val res: Resources) : EntitySystem(), KeyInputHandler {

    private lateinit var player: Entity
    private val playerComp: PlayerComponent by lazy { Mapper.PLAYER_MAPPER[player] }
    private val vel: VelocityComponent by lazy { Mapper.VEL_MAPPER[player] }

    override fun addedToEngine(engine: Engine?) {
        player = engine!!.getEntitiesFor(Family.all(PlayerComponent::class.java).get())[0]
    }

    override fun move(right: Boolean) {
        val se = Mapper.STATUS_EFFECT_MAPPER[player]
        val gravity = Mapper.GRAVITY_MAPPER[player]
        if (se.type != StatusEffect.Stun && se.type != StatusEffect.Snare) {
            vel.move(if (gravity.reverse) !right else right)
        }

        if (se.type == StatusEffect.Snare) {
            Mapper.DIR_MAPPER[player].run { facingRight = right }
        }
    }

    override fun stop(right: Boolean) {
        val gravity = Mapper.GRAVITY_MAPPER[player]
        val dir = if (gravity.reverse) !right else right

        when (dir) {
            true -> if (vel.dx > 0) vel.dx = 0f
            false -> if (vel.dx < 0) vel.dx = 0f
        }
    }

    override fun jump() {
        val gravity = Mapper.GRAVITY_MAPPER[player]
        val jump = Mapper.JUMP_MAPPER[player]

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
            val playerPos = Mapper.POS_MAPPER[player]
            val dir = Mapper.DIR_MAPPER[player]
            val key = PLAYER_PROJECTILE_RES_KEY
            val texture = res.getTexture(key)!!
            val width = texture.regionWidth.toFloat()
            val height = texture.regionHeight.toFloat()
            val x = playerPos.x + (PLAYER_WIDTH / 2) - (width / 2)
            val y = playerPos.y + (PLAYER_HEIGHT / 2) - (height / 2)

            EntityBuilder.instance(engine as PooledEngine)
                    .projectile(damage = PLAYER_DEFAULT_DAMAGE,
                            knockback = PLAYER_PROJECTILE_KNOCKBACK,
                            playerType = 1, textureStr = key)
                    .player()
                    .color(res.getColor(key)!!)
                    .position(x, y)
                    .velocity(dx = if (dir.facingRight) PLAYER_PROJECTILE_SPEED else -PLAYER_PROJECTILE_SPEED,
                            speed = PLAYER_PROJECTILE_SPEED)
                    .boundingBox(width, height)
                    .texture(texture, key)
                    .direction().remove().build()

            playerComp.canShoot = false
        }
    }

    override fun release() {

    }

}