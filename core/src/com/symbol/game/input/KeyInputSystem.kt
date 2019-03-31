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
    private lateinit var playerComp: PlayerComponent
    private lateinit var vel: VelocityComponent

    private var charging = false

    override fun addedToEngine(engine: Engine?) {
        player = engine!!.getEntitiesFor(Family.all(PlayerComponent::class.java).get()).get(0)
        playerComp = Mapper.PLAYER_MAPPER.get(player)
        vel = Mapper.VEL_MAPPER.get(player)
    }

    override fun update(dt: Float) {
        if (charging) {
            playerComp.chargeTime += dt
            playerComp.chargeIndex = when {
                playerComp.chargeTime < PLAYER_TIER_ONE_ATTACK_TIME -> 1
                playerComp.chargeTime < PLAYER_TIER_TWO_ATTACK_TIME -> 2
                playerComp.chargeTime < PLAYER_TIER_THREE_ATTACK_TIME -> 3
                else -> 4
            }
        }
    }

    override fun move(right: Boolean) {
        val se = Mapper.STATUS_EFFECT_MAPPER.get(player)
        if (se.type != StatusEffect.Stun && se.type != StatusEffect.Snare) vel.move(right)

        if (se.type == StatusEffect.Snare) {
            val dir = Mapper.DIR_MAPPER.get(player)
            dir.facingRight = right
        }
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

    override fun startCharge() {
        if (playerComp.canShoot) {
            charging = true
            playerComp.canShoot = false
        }
    }

    override fun endCharge() {
        if (charging) {
            val playerPos = Mapper.POS_MAPPER.get(player)
            val dir = Mapper.DIR_MAPPER.get(player)
            val key = PLAYER_PROJECTILE_RES_KEY + if (playerComp.chargeIndex > 1) playerComp.chargeIndex else ""
            val texture = res.getTexture(key)!!
            val width = texture.regionWidth.toFloat()
            val height = texture.regionHeight.toFloat()
            val x = playerPos.x + (PLAYER_WIDTH / 2) - (width / 2)
            val y = playerPos.y + (PLAYER_HEIGHT / 2) - (height / 2)

            playerComp.damage = playerComp.damageBoost
            playerComp.damage += playerComp.chargeIndex

            val builder = EntityBuilder.instance(engine as PooledEngine)
                    .projectile(originX = x, originY = y, damage = playerComp.damage,
                            knockback = PLAYER_PROJECTILE_KNOCKBACK,
                            playerType = playerComp.chargeIndex, textureStr = key)
                    .color(EntityColor.getProjectileColor(key)!!)
                    .position(x, y)
                    .velocity(dx = if (dir.facingRight) PLAYER_PROJECTILE_SPEED else -PLAYER_PROJECTILE_SPEED)
                    .boundingBox(width, height)
                    .texture(texture, key)
                    .direction().remove()

            if (playerComp.chargeIndex == 2) builder.statusEffect(apply = StatusEffect.Slow,
                    duration = PLAYER_SLOW_DURATION, value = PLAYER_SLOW_PERCENTAGE)
            if (playerComp.chargeIndex == 3) builder.statusEffect(apply = StatusEffect.Stun, duration = PLAYER_STUN_DURATION)

            builder.build()

            charging = false
            playerComp.chargeTime = 0f
        }
    }

}