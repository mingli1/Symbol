package com.symbol.game.ecs.component.enemy

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class AttackComponent : Component, Pool.Poolable {

    var damage = 0
    var attackRate = 0f
    var timer = 0f
    var canAttack = true
    var attackTexture: String? = null
    var projectileSpeed = 0f
    var projectileAcceleration = 0f
    var projectileDestroyable = false
    var attackDetonateTime = 0f

    override fun reset() {
        damage = 0
        attackRate = 0f
        timer = 0f
        canAttack = true
        attackTexture = null
        projectileSpeed = 0f
        projectileAcceleration = 0f
        projectileDestroyable = false
        attackDetonateTime = 0f
    }

}