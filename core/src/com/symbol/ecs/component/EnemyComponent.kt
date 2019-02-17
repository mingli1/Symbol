package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.symbol.ecs.entity.EnemyAttackType
import com.symbol.ecs.entity.EnemyMovementType
import com.symbol.ecs.entity.EnemyType

class EnemyComponent : Component, Pool.Poolable {

    var type: EnemyType = EnemyType.None
    var movementType: EnemyMovementType = EnemyMovementType.None
    var attackType: EnemyAttackType = EnemyAttackType.None

    var jumpImpulse: Float = 0f

    var activationRange = -1f
    var active: Boolean = false

    var damage: Int = 0
    var attackRate: Float = 0f
    var canAttack: Boolean = true
    var attackTexture: String? = null
    var projectileSpeed: Float = 0f

    var attackDetonateTime: Float = 0f
    var explodeOnDeath: Boolean = false
    var teleportOnHit: Boolean = false
    var lastStand: Boolean = false

    override fun reset() {
        type = EnemyType.None
        movementType = EnemyMovementType.None
        attackType = EnemyAttackType.None

        jumpImpulse = 0f

        damage = 0
        activationRange = -1f
        active = false
        attackRate = 0f
        canAttack = true
        attackTexture = null
        projectileSpeed = 0f

        attackDetonateTime = 0f
        explodeOnDeath = false
        teleportOnHit = false
        lastStand = false
    }

}