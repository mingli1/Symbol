package com.symbol.game.ecs.component.enemy

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import com.symbol.game.ecs.entity.EnemyAttackType
import com.symbol.game.ecs.entity.EnemyMovementType

class EnemyComponent : Component, Pool.Poolable {

    var movementType = EnemyMovementType.None
    var attackType = EnemyAttackType.None
    var parent: Entity? = null
    var movementTimer = 0f

    override fun reset() {
        movementType = EnemyMovementType.None
        attackType = EnemyAttackType.None
        parent = null
        movementTimer = 0f
    }

}