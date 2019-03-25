package com.symbol.game.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class StatusEffectComponent : Component, Pool.Poolable {

    var type = StatusEffect.None
    var apply = StatusEffect.None
    var duration = 0f
    var value = 0f
    var entityApplied = false
    var statusChange = false

    fun apply(type: StatusEffect, duration: Float = 0f, value: Float = 0f) {
        if (this.type != StatusEffect.None) statusChange = true
        this.type = type
        this.duration = duration
        this.value = value
    }

    fun finish() {
        type = StatusEffect.None
        duration = 0f
        value = 0f
    }

    override fun reset() {
        finish()
        apply = StatusEffect.None
        entityApplied = false
    }

}

enum class StatusEffect(val typeStr: String) {

    None("none"),
    Stun("stun"),
    Snare("snare"),
    Slow("slow"),
    Grounded("grounded"),
    SpeedBoostRight("speed_boost_right"),
    SpeedBoostLeft("speed_boost_left"),
    JumpBoost("jump_boost"),
    LastStand("last_stand")

}