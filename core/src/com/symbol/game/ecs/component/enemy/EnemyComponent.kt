package com.symbol.game.ecs.component.enemy

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import com.symbol.game.ecs.entity.EnemyAttackType
import com.symbol.game.ecs.entity.EnemyMovementType
import com.symbol.game.ecs.entity.EnemyType

class EnemyComponent : Component, Pool.Poolable {

    var enemyType = EnemyType.None
    var movementType = EnemyMovementType.None
    var attackType = EnemyAttackType.None
    var parent: Entity? = null
    var movementTimer = 0f
    var visible = true

    override fun reset() {
        enemyType = EnemyType.None
        movementType = EnemyMovementType.None
        attackType = EnemyAttackType.None
        parent = null
        movementTimer = 0f
        visible = true
    }

}