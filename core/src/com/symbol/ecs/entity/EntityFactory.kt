package com.symbol.ecs.entity

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.symbol.ecs.EntityBuilder
import com.symbol.util.ORBIT
import com.symbol.util.Resources

object EntityFactory {

    fun createEnemy(engine: PooledEngine, res: Resources, type: EnemyType, rect: Rectangle, facingRight: Boolean) {
        val texture = res.getTexture("e_${type.typeStr}")!!
        when (type) {
            EnemyType.EConstant -> {
                EntityBuilder.instance(engine)
                        .enemy(movementType = EnemyMovementType.BackAndForth, damage = 2, activationRange = 150f)
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
                        .enemy(movementType = EnemyMovementType.Charge,
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
                        .enemy(movementType = EnemyMovementType.Charge,
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
                        .enemy(damage = 2, activationRange = 120f, attackType = EnemyAttackType.ShootOne,
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
                        .enemy(damage = 4, activationRange = 120f, attackType = EnemyAttackType.ShootOne,
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
                        .enemy(damage = 1, activationRange = 100f, attackType = EnemyAttackType.ShootOne,
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
                        .enemy(damage = 2, activationRange = 150f, attackType = EnemyAttackType.SprayThree,
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
                        .enemy(damage = 2, activationRange = 100f, explodeOnDeath = true,
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
                        .enemy(damage = 4, activationRange = 200f, attackType = EnemyAttackType.ShootAndQuake,
                                attackTexture = "p_alpha", attackRate = 1.5f, jumpImpulse = 150f, projectileSpeed = 60f, explodeOnDeath = true)
                        .health(10)
                        .boundingBox(14f, 16f)
                        .position(rect.x, rect.y)
                        .velocity()
                        .texture(texture)
                        .direction(facingRight = facingRight)
                        .gravity(gravity = -7.8f, terminalVelocity = -50f).remove().build()
            }
            EnemyType.Percent -> {
                val parent = EntityBuilder.instance(engine)
                        .enemy(damage = 1, activationRange = 120f, movementType = EnemyMovementType.BackAndForth)
                        .health(2)
                        .boundingBox(10f, 10f)
                        .position(rect.x, rect.y)
                        .velocity(speed = 20f)
                        .texture(texture)
                        .direction(facingRight = facingRight)
                        .gravity().knockback().remove().build()

                val angles = listOf(MathUtils.PI2 / 3f, MathUtils.PI2 * 2f / 3f, 0f)
                for (angle in angles) {
                    EntityBuilder.instance(engine)
                            .enemy(damage = 1, activationRange = 150f, movementType = EnemyMovementType.Orbit, parent = parent)
                            .health(1)
                            .boundingBox(6f, 6f)
                            .position(rect.x, rect.y)
                            .velocity()
                            .texture(res.getTexture("e_${type.typeStr}$ORBIT")!!)
                            .orbit(angle = angle, speed = 2f, radius = 15f)
                            .remove().build()
                }
            }
        }
    }

    private const val DIST = "dist"
    private const val VEL_X = "dx"
    private const val VEL_Y = "dy"

    fun createMapEntity(engine: PooledEngine, res: Resources, props: MapProperties, type: MapEntityType, rect: Rectangle) {
        when (type) {
            MapEntityType.MovingPlatform -> {
                val dist = (props[DIST] ?: 0f) as Float
                val dx = (props[VEL_X] ?: 0f) as Float
                val dy = (props[VEL_Y] ?: 0f) as Float
                val texture = res.getTexture("${type.typeStr}${MathUtils.ceil(rect.width / 8)}")!!

                EntityBuilder.instance(engine)
                        .mapEntity(type = type)
                        .movingPlatform(distance = dist, originX = rect.x, originY = rect.y, positive = dx > 0 || dy > 0)
                        .boundingBox(texture.regionWidth.toFloat(), texture.regionHeight.toFloat())
                        .position(rect.x, rect.y)
                        .velocity(dx = dx, dy = dy)
                        .texture(texture)
                        .build()
            }
        }
    }

}