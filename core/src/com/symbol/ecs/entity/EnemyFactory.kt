package com.symbol.ecs.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.math.Rectangle
import com.symbol.ecs.EntityBuilder
import com.symbol.util.Resources

object EnemyFactory {

    fun createEnemy(engine: PooledEngine, res: Resources, type: EnemyType, rect: Rectangle, facingRight: Boolean) : Entity? {
        val texture = res.getSingleTexture("e_${type.typeStr}")!!
        return when (type) {
            EnemyType.EConstant -> {
                EntityBuilder.instance(engine)
                        .enemy(type = type, movementType = EnemyMovementType.BackAndForth)
                        .health(2)
                        .boundingBox(rect.width, rect.height)
                        .position(rect.x, rect.y)
                        .velocity(speed = 25f)
                        .direction(facingRight = facingRight)
                        .texture(texture)
                        .knockback().gravity().remove().build()
            }
            else -> null
        }
    }

}