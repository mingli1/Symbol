package com.symbol.game.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.ProjectileComponent
import com.symbol.game.ecs.component.StatusEffect
import com.symbol.game.ecs.component.StatusEffectComponent

class StatusEffectSystem : IteratingSystem(Family.all(StatusEffectComponent::class.java)
        .exclude(ProjectileComponent::class.java).get()) {

    private var durationTimers: MutableMap<Entity, Float> = HashMap()
    private var startEffect: MutableMap<Entity, Boolean> = HashMap()

    fun reset() {
        durationTimers.clear()
        startEffect.clear()
        for (entity in entities) {
            durationTimers[entity] = 0f
            startEffect[entity] = false
        }
    }

    override fun processEntity(entity: Entity?, dt: Float) {
        val se = Mapper.STATUS_EFFECT_MAPPER.get(entity)

        if (se.type != StatusEffect.None && se.duration != 0f && !startEffect[entity]!!) {
            startEffect[entity!!] = true
            se.entityApplied = true
        }

        if (startEffect[entity]!!) {
            if (se.statusChange) {
                durationTimers[entity!!] = 0f
                se.statusChange = false
            }
            durationTimers[entity!!] = durationTimers[entity]?.plus(dt)!!

            when (se.type) {
                StatusEffect.Stun -> handleStun(entity)
                StatusEffect.Snare -> handleSnare(entity)
                StatusEffect.Slow -> handleSlow(entity, se)
                StatusEffect.Grounded -> handleGrounded(entity)
                StatusEffect.SpeedBoostRight -> handleSpeedBoostRight(entity, se)
                StatusEffect.SpeedBoostLeft -> handleSpeedBoostLeft(entity, se)
                StatusEffect.JumpBoost -> handleJumpBoost(entity)
                else -> {}
            }

            if (durationTimers[entity]!! >= se.duration) {
                startEffect[entity] = false
                durationTimers[entity] = 0f
                se.entityApplied = false
                se.finish()
            }
        }
    }

    private fun handleStun(entity: Entity?) {
        handleSnare(entity)

        val player = Mapper.PLAYER_MAPPER.get(entity)
        player?.canShoot = false
        player?.canJump = false
        player?.canDoubleJump = false
        val attack = Mapper.ATTACK_MAPPER.get(entity)
        attack?.canAttack = false
    }

    private fun handleSnare(entity: Entity?) {
        val velocity = Mapper.VEL_MAPPER.get(entity)
        if (velocity.dx != 0f) velocity.dx = 0f
    }

    private fun handleSlow(entity: Entity?, se: StatusEffectComponent) {
        val velocity = Mapper.VEL_MAPPER.get(entity)
        if (velocity.dx > 0f) velocity.dx = velocity.speed * se.value
        else if (velocity.dx < 0f) velocity.dx = -velocity.speed * se.value
    }

    private fun handleGrounded(entity: Entity?) {
        val player = Mapper.PLAYER_MAPPER.get(entity)
        player?.canJump = false
        player?.canDoubleJump = false
    }

    private fun handleSpeedBoostRight(entity: Entity?, se: StatusEffectComponent) {
        val velocity = Mapper.VEL_MAPPER.get(entity)
        if (velocity.dx > 0 && velocity.dx == velocity.speed) velocity.dx += se.value
    }

    private fun handleSpeedBoostLeft(entity: Entity?, se: StatusEffectComponent) {
        val velocity = Mapper.VEL_MAPPER.get(entity)
        if (velocity.dx < 0 && velocity.dx == -velocity.speed) velocity.dx -= se.value
    }

    private fun handleJumpBoost(entity: Entity?) {
        Mapper.PLAYER_MAPPER.get(entity)?.hasJumpBoost = true
    }

}