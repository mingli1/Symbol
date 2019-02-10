package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.symbol.ecs.entity.EnemyMovementType
import com.symbol.ecs.entity.EnemyType

class EnemyComponent : Component, Pool.Poolable {

    var type: EnemyType = EnemyType.None
    var movementType: EnemyMovementType = EnemyMovementType.None

    override fun reset() {
        type = EnemyType.None
        movementType = EnemyMovementType.None
    }

}