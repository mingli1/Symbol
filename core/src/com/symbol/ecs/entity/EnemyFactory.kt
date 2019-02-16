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
                                attackTexture = "p_large_triangle", attackRate = 1.4f, projectileSpeed = 45f)
                        .health(2)
                        .boundingBox(10f, 13f)
                        .position(rect.x, rect.y)
                        .velocity()
                        .direction(facingRight = facingRight)
                        .texture(texture)
                        .gravity().remove().build()
            }
            EnemyType.BigPi -> {
                EntityBuilder.instance(engine)
                        .enemy(type = type, damage = 4, activationRange = 120f, attackType = EnemyAttackType.ShootOne,
                                attackTexture = "p_big_ll", attackRate = 1.4f, projectileSpeed = 45f)
                        .health(4)
                        .boundingBox(11f, 13f)
                        .position(rect.x, rect.y)
                        .velocity()
                        .direction(facingRight = facingRight)
                        .texture(texture)
                        .gravity().remove().build()
            }
            EnemyType.In -> {
                EntityBuilder.instance(engine)
                        .enemy(type = type, damage = 1, activationRange = 100f, attackType = EnemyAttackType.ShootOne,
                                attackTexture = "p_xor", attackRate = 2f, projectileSpeed = 45f, attackDetonateTime = 2f)
                        .health(3)
                        .boundingBox(11f, 11f)
                        .position(rect.x, rect.y)
                        .velocity()
                        .direction(facingRight = facingRight)
                        .texture(texture)
                        .gravity().remove().build()
            }
            EnemyType.BigOmega -> {
                EntityBuilder.instance(engine)
                        .enemy(type = type, damage = 2, activationRange = 150f, attackType = EnemyAttackType.SprayThree,
                                attackTexture = "p_cup", attackRate = 2.5f, projectileSpeed = 200f)
                        .health(3)
                        .boundingBox(12f, 13f)
                        .position(rect.x, rect.y)
                        .velocity()
                        .texture(texture)
                        .gravity().remove().build()
            }
            EnemyType.NaturalJoin -> {
                EntityBuilder.instance(engine)
                        .enemy(type = type, damage = 2, activationRange = 100f, attackType = EnemyAttackType.ExplodeOnDeath,
                                attackTexture = "p_ltimes", projectileSpeed = 45f, movementType = EnemyMovementType.BackAndForth)
                        .health(4)
                        .boundingBox(9f, 7f)
                        .position(rect.x, rect.y)
                        .velocity(speed = 30f)
                        .texture(texture)
                        .direction(facingRight = facingRight)
                        .knockback().gravity().remove().build()
            }
            EnemyType.BigPhi -> {
                EntityBuilder.instance(engine)
                        .enemy(type = type, damage = 4, activationRange = 200f, attackType = EnemyAttackType.ShootAndQuake,
                                attackTexture = "p_alpha", attackRate = 1.5f, jumpImpulse = 150f, projectileSpeed = 60f)
                        .health(10)
                        .boundingBox(14f, 16f)
                        .position(rect.x, rect.y)
                        .velocity()
                        .texture(texture)
                        .direction(facingRight = facingRight)
                        .gravity(gravity = -7.8f, terminalVelocity = -50f).remove().build()
            }
            else -> null
        }
    }

}