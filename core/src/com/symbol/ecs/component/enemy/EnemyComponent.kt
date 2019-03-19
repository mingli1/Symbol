package com.symbol.ecs.component.enemy

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import com.symbol.ecs.entity.EnemyAttackType
import com.symbol.ecs.entity.EnemyMovementType

class EnemyComponent : Component, Pool.Poolable {

    var movementType: EnemyMovementType = EnemyMovementType.None
    var attackType: EnemyAttackType = EnemyAttackType.None
    var parent: Entity? = null

    override fun reset() {
        movementType = EnemyMovementType.None
        attackType = EnemyAttackType.None
        parent = null
    }

}