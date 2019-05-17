package com.symbol.game.ecs.entity

import com.badlogic.ashley.core.Entity
import com.symbol.game.ecs.component.*
import com.symbol.game.ecs.component.player.ChargeComponent
import com.symbol.game.ecs.component.player.PlayerComponent
import com.symbol.game.util.Data
import com.symbol.game.util.Resources

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

        with (data) {
            color.hex = getColor("player")
            bounds.rect.setSize(getPlayerData("boundsWidth").asFloat(),
                    getPlayerData("boundsHeight").asFloat())
            texture.texture = res.getTexture("player")
            texture.textureStr = "player"
            velocity.speed = getPlayerData("speed").asFloat()
            health.hp = getPlayerData("hp").asInt()
            health.maxHp = getPlayerData("hp").asInt()
            jump.impulse = getPlayerData("jumpImpulse").asFloat()
        }
    }

}