package com.symbol.ecs.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.math.Rectangle
import com.symbol.ecs.EntityBuilder
import com.symbol.util.Resources

object EnemyFactory {

    fun createEnemy(engine: PooledEngine, res: Resources, type: EnemyType, rect: Rectangle, facingRight: Boolean) : Entity? {
        val texture = res.getTexture("e_${type.typeStr}")!!
        return when (type) {
            EnemyType.EConstant -> {
                EntityBuilder.instance(engine)
                        .enemy(type = type, movementType = EnemyMovementType.BackAndForth, damage = 2)
                        .health(2)
                        .boundingBox(7f, 7f)
                        .position(rect.x, rect.y)
                        .velocity(speed = 25f)
                        .direction(facingRight = facingRight)
                        .texture(texture)
                        .knockback().gravity().remove().build()
            }
            EnemyType.SquareRoot -> {
                EntityBuilder.instance(engine)
                        .enemy(type = type, movementType = EnemyMovementType.Charge,
                                damage = 3, activationRange = 75f)
                        .health(3)
                        .boundingBox(10f, 8f)
                        .position(rect.x, rect.y)
                        .velocity(speed = 60f)
                        .direction(facingRight = facingRight)
                        .texture(texture)
                        .gravity().remove().build()
            }
            EnemyType.Exists -> {
                EntityBuilder.instance(engine)
                        .enemy(type = type, movementType = EnemyMovementType.Charge,
                                damage = PLAYER_HP, activationRange = 90f)
                        .health(2)
                        .boundingBox(9f, 13f)
                        .position(rect.x, rect.y)
                        .velocity(speed = 75f)
                        .direction(facingRight = facingRight)
                        .texture(texture)
                        .gravity().remove().build()
            }
            EnemyType.Summation -> {
                EntityBuilder.instance(engine)
                        .enemy(type = type, damage = 2, activationRange = 120f, attackType = EnemyAttackType.ShootOne,
                                attackTexture = "p_angle_bracket", attackRate = 0.8f, projectileSpeed = 45f)
                        .health(2)
                        .boundingBox(10f, 13f)
                        .position(rect.x, rect.y)
                        .velocity()
                        .direction(facingRight = facingRight)
                        .texture(texture)
                        .gravity().remove().build()
            }
            else -> null
        }
    }

}