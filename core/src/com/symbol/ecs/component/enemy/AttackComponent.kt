package com.symbol.ecs.component.enemy

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class AttackComponent : Component, Pool.Poolable {

    var damage: Int = 0
    var attackRate: Float = 0f
    var canAttack: Boolean = true
    var attackTexture: String? = null
    var projectileSpeed: Float = 0f
    var projectileAcceleration: Float = 0f
    var projectileDestroyable: Boolean = false
    var attackDetonateTime: Float = 0f

    override fun reset() {
        damage = 0
        attackRate = 0f
        canAttack = true
        attackTexture = null
        projectileSpeed = 0f
        projectileAcceleration = 0f
        projectileDestroyable = false
        attackDetonateTime = 0f
    }

}