package com.symbol.game.ecs.entity

import com.badlogic.ashley.core.Entity
import com.symbol.game.ecs.component.*
import com.symbol.game.ecs.component.player.ChargeComponent
import com.symbol.game.ecs.component.player.PlayerComponent
import com.symbol.game.util.Data
import com.symbol.game.util.Resources

const val PLAYER_WIDTH = 8f
const val PLAYER_HEIGHT = 8f

const val PLAYER_DEFAULT_DAMAGE = 1
const val PLAYER_PROJECTILE_SHOOT_DELAY = 0.25f
const val PLAYER_PROJECTILE_SPEED = 80f
const val PLAYER_PROJECTILE_RES_KEY = "p_dot"
const val PLAYER_PROJECTILE_KNOCKBACK = 75f
const val PLAYER_CHARGE_GAIN = 5
const val PLAYER_CHARGE_THRESHOLD = 25
const val PLAYER_MAX_CHARGE = 100
const val PLAYER_RAPID_SHOOT_DELAY = 0.075f

const val PLAYER_HP = 8

const val PLAYER_SLOW_PERCENTAGE = 0.4f
const val PLAYER_SLOW_DURATION = 2f
const val PLAYER_SNARE_DURATION = 2.5f
const val PLAYER_STUN_DURATION = 2f

const val PLAYER_JUMP_IMPULSE = 160f
private const val PLAYER_SPEED = 35f
private const val PLAYER_BOUNDS_WIDTH = 7f
private const val PLAYER_BOUNDS_HEIGHT = 7f

class Player(private val res: Resources, private val data: Data) : Entity() {

    private val player = PlayerComponent()
    private val position = PositionComponent()
    private val color = ColorComponent()
    private val bounds = BoundingBoxComponent()
    private val texture = TextureComponent()
    private val velocity = VelocityComponent()
    private val health = HealthComponent()
    private val charge = ChargeComponent()
    private val jump = JumpComponent()
    private val gravity = GravityComponent()
    private val statusEffect = StatusEffectComponent()
    private val direction = DirectionComponent()
    private val remove = RemoveComponent()

    init {
        add(player)
        add(position)
        add(color)
        add(bounds)
        add(texture)
        add(velocity)
        add(health)
        add(charge)
        add(jump)
        add(gravity)
        add(statusEffect)
        add(direction)
        add(remove)
    }

    fun reset() {
        player.reset()
        position.reset()
        color.reset()
        bounds.reset()
        texture.reset()
        velocity.reset()
        health.reset()
        charge.reset()
        jump.reset()
        gravity.reset()
        statusEffect.reset()
        direction.reset()
        remove.reset()

        color.hex = data.getColor("player")
        bounds.rect.setSize(PLAYER_BOUNDS_WIDTH, PLAYER_BOUNDS_HEIGHT)
        texture.texture = res.getTexture("player")
        texture.textureStr = "player"
        velocity.speed = PLAYER_SPEED
        health.hp = PLAYER_HP
        health.maxHp = PLAYER_HP
        jump.impulse = PLAYER_JUMP_IMPULSE
    }

}