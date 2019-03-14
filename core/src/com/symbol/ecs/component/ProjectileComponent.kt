package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ProjectileComponent : Component, Pool.Poolable {

    var lifeTime: Float = 0f

    var textureStr: String? = null
    var collidesWithTerrain: Boolean = true
    var collidesWithProjectiles: Boolean = false
    var enemy: Boolean = false
    var damage: Int = 0
    var knockback: Float = 0f

    var detonateTime: Float = 0f
    var acceleration: Float = 0f

    override fun reset() {
        lifeTime = 0f
        textureStr = null
        collidesWithTerrain = true
        collidesWithProjectiles = false
        enemy = false
        damage = 0
        knockback = 0f
        detonateTime = 0f
        acceleration = 0f
    }
}