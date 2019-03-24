package com.symbol.game.ecs.entity

import com.badlogic.ashley.core.Entity
import com.symbol.game.ecs.component.*
import com.symbol.game.util.Resources

const val PLAYER_WIDTH = 8f
const val PLAYER_HEIGHT = 8f

const val PLAYER_PROJECTILE_SHOOT_DELAY = 0.15f
const val PLAYER_PROJECTILE_SPEED = 80f
const val PLAYER_PROJECTILE_RES_KEY = "p_dot"
const val PLAYER_PROJECTILE_KNOCKBACK = 75f

const val PLAYER_HP = 8

const val PLAYER_TIER_ONE_ATTACK_TIME = 0.6
const val PLAYER_TIER_TWO_ATTACK_TIME = 1.2
const val PLAYER_TIER_THREE_ATTACK_TIME = 1.8

private const val PLAYER_SPEED = 35f
private const val PLAYER_JUMP_IMPULSE = 160f
private const val PLAYER_BOUNDS_WIDTH = 7f
private const val PLAYER_BOUNDS_HEIGHT = 7f

class Player(res: Resources) : Entity() {

    init {
        val color = ColorComponent()
        val bounds = BoundingBoxComponent()
        val texture = TextureComponent()
        val velocity = VelocityComponent()
        val health = HealthComponent()
        val jump = JumpComponent()

        color.hex = EntityColor.PLAYER_COLOR
        bounds.rect.setSize(PLAYER_BOUNDS_WIDTH, PLAYER_BOUNDS_HEIGHT)
        texture.texture = res.getTexture("player")
        velocity.speed = PLAYER_SPEED
        health.hp = PLAYER_HP
        health.maxHp = PLAYER_HP
        jump.impulse = PLAYER_JUMP_IMPULSE

        add(PlayerComponent())
        add(PositionComponent())
        add(GravityComponent())
        add(DirectionComponent())
        add(color)
        add(bounds)
        add(texture)
        add(velocity)
        add(health)
        add(jump)
    }

}