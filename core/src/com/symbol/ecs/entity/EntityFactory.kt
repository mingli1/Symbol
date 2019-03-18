package com.symbol.ecs.entity

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.symbol.ecs.EntityBuilder
import com.symbol.util.*

object EntityFactory {

    fun createEnemy(engine: PooledEngine, res: Resources, type: EnemyType, rect: Rectangle, facingRight: Boolean) {
        val textureStr = "e_${type.typeStr}"
        val texture = res.getTexture(textureStr)!!
        when (type) {
            EnemyType.EConstant -> {
                EntityBuilder.instance(engine)
                        .enemy(movementType = EnemyMovementType.BackAndForth, damage = 2)
                        .activation(150f)
                        .color(EntityColor.E_COLOR)
                        .health(2)
                        .boundingBox(7f, 7f)
                        .position(rect.x, rect.y)
                        .velocity(speed = 25f)
                        .direction(facingRight = facingRight)
                        .texture(texture, textureStr)
                        .knockback().gravity().remove().build()
            }
            EnemyType.SquareRoot -> {
                EntityBuilder.instance(engine)
                        .enemy(movementType = EnemyMovementType.Charge, damage = 3)
                        .activation(75f)
                        .color(EntityColor.SQRT_COLOR)
                        .health(3)
                        .boundingBox(10f, 8f)
                        .position(rect.x, rect.y)
                        .velocity(speed = 60f)
                        .direction(facingRight = facingRight)
                        .texture(texture, textureStr)
                        .gravity().remove().build()
            }
            EnemyType.Exists -> {
                EntityBuilder.instance(engine)
                        .enemy(movementType = EnemyMovementType.Charge, damage = PLAYER_HP)
                        .activation(90f)
                        .color(EntityColor.EXISTS_COLOR)
                        .health(2)
                        .boundingBox(9f, 13f)
                        .position(rect.x, rect.y)
                        .velocity(speed = 75f)
                        .direction(facingRight = facingRight)
                        .texture(texture, textureStr)
                        .gravity().remove().build()
            }
            EnemyType.Summation -> {
                EntityBuilder.instance(engine)
                        .enemy(damage = 2, attackType = EnemyAttackType.ShootOne,
                                attackTexture = "p_large_triangle", attackRate = 1.4f, projectileSpeed = 45f)
                        .activation(120f)
                        .color(EntityColor.SUM_COLOR)
                        .health(2)
                        .boundingBox(10f, 13f)
                        .position(rect.x, rect.y)
                        .velocity()
                        .direction(facingRight = facingRight)
                        .texture(texture, textureStr)
                        .gravity().remove().build()
            }
            EnemyType.BigPi -> {
                EntityBuilder.instance(engine)
                        .enemy(damage = 4, attackType = EnemyAttackType.ShootOne,
                                attackTexture = "p_big_ll", attackRate = 1.4f, projectileSpeed = 45f)
                        .activation(120f)
                        .color(EntityColor.BIG_PI_COLOR)
                        .health(4)
                        .boundingBox(11f, 13f)
                        .position(rect.x, rect.y)
                        .velocity()
                        .direction(facingRight = facingRight)
                        .texture(texture, textureStr)
                        .gravity().remove().build()
            }
            EnemyType.In -> {
                EntityBuilder.instance(engine)
                        .enemy(damage = 1, attackType = EnemyAttackType.ShootOne,
                                attackTexture = "p_xor", attackRate = 2f, projectileSpeed = 45f, attackDetonateTime = 2f)
                        .activation(100f)
                        .color(EntityColor.IN_COLOR)
                        .health(3)
                        .boundingBox(11f, 11f)
                        .position(rect.x, rect.y)
                        .velocity()
                        .direction(facingRight = facingRight)
                        .texture(texture, textureStr)
                        .gravity().remove().build()
            }
            EnemyType.BigOmega -> {
                EntityBuilder.instance(engine)
                        .enemy(damage = 2, attackType = EnemyAttackType.SprayThree,
                                attackTexture = "p_cup", attackRate = 2.5f, projectileSpeed = 200f)
                        .activation(150f)
                        .color(EntityColor.BIG_OMEGA_COLOR)
                        .health(3)
                        .boundingBox(12f, 13f)
                        .position(rect.x, rect.y)
                        .velocity()
                        .texture(texture, textureStr)
                        .gravity().remove().build()
            }
            EnemyType.NaturalJoin -> {
                EntityBuilder.instance(engine)
                        .enemy(damage = 2, explodeOnDeath = true, attackTexture = "p_ltimes",
                                projectileSpeed = 45f, movementType = EnemyMovementType.BackAndForth)
                        .activation(100f)
                        .color(EntityColor.NATURAL_JOIN_COLOR)
                        .health(4)
                        .boundingBox(9f, 7f)
                        .position(rect.x, rect.y)
                        .velocity(speed = 30f)
                        .texture(texture, textureStr)
                        .direction(facingRight = facingRight)
                        .knockback().gravity().remove().build()
            }
            EnemyType.BigPhi -> {
                EntityBuilder.instance(engine)
                        .enemy(damage = 4, attackType = EnemyAttackType.ShootAndQuake,
                                attackTexture = "p_alpha", attackRate = 1.5f, projectileSpeed = 60f, explodeOnDeath = true)
                        .activation(200f)
                        .color(EntityColor.BIG_PHI_COLOR)
                        .health(10)
                        .boundingBox(14f, 16f)
                        .position(rect.x, rect.y)
                        .velocity()
                        .jump(150f)
                        .texture(texture, textureStr)
                        .direction(facingRight = facingRight)
                        .gravity().remove().build()
            }
            EnemyType.Percent -> {
                val parent = EntityBuilder.instance(engine)
                        .enemy(damage = 1, movementType = EnemyMovementType.BackAndForth)
                        .activation(120f)
                        .color(EntityColor.PERCENT_COLOR)
                        .health(2)
                        .boundingBox(10f, 10f)
                        .position(rect.x, rect.y)
                        .velocity(speed = 20f)
                        .jump(120f)
                        .texture(texture, textureStr)
                        .direction(facingRight = facingRight)
                        .gravity(gravity = -480f, terminalVelocity = -240f).knockback().remove().build()

                val angles = listOf(MathUtils.PI2 / 5f, MathUtils.PI2 * 2f / 5f, MathUtils.PI2 * 3 / 5f, MathUtils.PI2 * 4 / 5f, 0f)
                for (angle in angles) {
                    EntityBuilder.instance(engine)
                            .enemy(damage = 1, movementType = EnemyMovementType.Orbit, parent = parent)
                            .activation(150f)
                            .color(EntityColor.PERCENT_ORBIT_COLOR)
                            .health(1)
                            .boundingBox(6f, 6f)
                            .position(rect.x, rect.y)
                            .velocity()
                            .texture(res.getTexture("e_${type.typeStr}$ORBIT")!!, "e_${type.typeStr}$ORBIT")
                            .orbit(angle = angle, speed = 2f, radius = 15f)
                            .remove().build()
                }
            }
            EnemyType.Nabla -> {
                EntityBuilder.instance(engine)
                        .enemy(damage = 4)
                        .activation(140f)
                        .color(EntityColor.NABLA_COLOR)
                        .health(1)
                        .gravity(gravity = -1200f, terminalVelocity = -160f, collidable = false)
                        .boundingBox(texture.regionWidth.toFloat() - 4, texture.regionHeight.toFloat())
                        .position(rect.x, rect.y)
                        .velocity()
                        .texture(texture, textureStr)
                        .remove().build()
            }
            EnemyType.CIntegral -> {
                EntityBuilder.instance(engine)
                        .enemy(damage = 4, attackType = EnemyAttackType.ArcTwo,
                                attackTexture = "p_succ", attackRate = 2f,
                                projectileSpeed = 80f, projectileAcceleration = 80f,
                                incorporealTime = 2f)
                        .activation(120f)
                        .color(EntityColor.CINTEGRAL_COLOR)
                        .health(5)
                        .boundingBox(8f, 16f)
                        .position(rect.x, rect.y)
                        .direction(facingRight)
                        .velocity()
                        .texture(texture, textureStr)
                        .gravity().remove().build()
            }
            else -> {}
        }
    }

    fun createMapEntity(engine: PooledEngine, res: Resources, props: MapProperties, type: MapEntityType, rect: Rectangle) {
        when (type) {
            MapEntityType.MovingPlatform -> {
                val dist = (props["dist"] ?: 0f) as Float
                val dx = (props["dx"] ?: 0f) as Float
                val textureStr = "${type.typeStr}${MathUtils.ceil(rect.width / 8)}"
                val texture = res.getTexture(textureStr)!!

                EntityBuilder.instance(engine)
                        .mapEntity(type = type, projectileCollidable = true)
                        .movingPlatform(distance = dist, originX = rect.x, originY = rect.y, positive = dx > 0)
                        .boundingBox(texture.regionWidth.toFloat(), texture.regionHeight.toFloat())
                        .position(rect.x, rect.y)
                        .velocity(dx = dx)
                        .texture(texture, textureStr)
                        .build()
            }
            MapEntityType.TemporaryPlatform -> {
                val texture = res.getTexture("approx")!!
                EntityBuilder.instance(engine)
                        .mapEntity(type = type)
                        .boundingBox(texture.regionWidth.toFloat(), texture.regionHeight.toFloat(), x = rect.x, y = rect.y)
                        .position(rect.x, rect.y)
                        .texture(texture, "approx")
                        .remove().build()
            }
            MapEntityType.Portal -> {
                val texture = res.getTexture("curly_brace_portal")!!
                val bw = texture.regionWidth.toFloat() - 4f
                val bh = texture.regionHeight.toFloat() - 4f
                val id = props["id"]!! as Int
                val target = props["target"]!! as Int

                EntityBuilder.instance(engine)
                        .mapEntity(type = type, projectileCollidable = true)
                        .portal(id, target)
                        .boundingBox(bw, bh)
                        .position(rect.x, rect.y)
                        .velocity()
                        .texture(texture, "curly_brace_portal")
                        .build()
            }
            MapEntityType.Clamp -> {
                val textureKey = (props["texture"] ?: "square_bracket") as String
                val acceleration = (props["accel"] ?: 144f) as Float
                val backVelocity = (props["backVel"] ?: 10f) as Float
                val textureLeft = res.getTexture(textureKey + BRACKET_LEFT)!!
                val textureRight = res.getTexture(textureKey + BRACKET_RIGHT)!!

                EntityBuilder.instance(engine)
                        .mapEntity(type = type, projectileCollidable = true)
                        .clamp(false, rect, acceleration, backVelocity)
                        .boundingBox(textureLeft.regionWidth.toFloat(), textureLeft.regionHeight.toFloat())
                        .position(rect.x, rect.y)
                        .velocity()
                        .texture(textureLeft)
                        .build()

                EntityBuilder.instance(engine)
                        .mapEntity(type = type, projectileCollidable = true)
                        .clamp(true, rect, acceleration, backVelocity)
                        .boundingBox(textureRight.regionWidth.toFloat(), textureRight.regionHeight.toFloat())
                        .position(rect.x + rect.width - textureRight.regionWidth, rect.y)
                        .velocity()
                        .texture(textureRight)
                        .build()
            }
            MapEntityType.HealthPack -> {
                val texture = res.getTexture("health_pack")!!
                val regen = (props["regen"] ?: 0) as Int

                EntityBuilder.instance(engine)
                        .mapEntity(type = type)
                        .healthPack(regen)
                        .boundingBox(texture.regionWidth.toFloat(), texture.regionHeight.toFloat())
                        .position(rect.x, rect.y)
                        .velocity()
                        .texture(texture, "health_pack")
                        .remove().build()
            }
            MapEntityType.Mirror -> {
                val texture = res.getTexture("between")!!

                EntityBuilder.instance(engine)
                        .mapEntity(type = type)
                        .boundingBox(texture.regionWidth.toFloat(), texture.regionHeight.toFloat())
                        .position(rect.x, rect.y)
                        .velocity()
                        .texture(texture, "between")
                        .build()
            }
            MapEntityType.GravitySwitch -> {
                val textureStr = "updownarrow"
                val texture = res.getTexture(textureStr + TOGGLE_OFF)!!

                EntityBuilder.instance(engine)
                        .mapEntity(type = type, projectileCollidable = true)
                        .boundingBox(texture.regionWidth.toFloat(), texture.regionHeight.toFloat())
                        .position(rect.x, rect.y)
                        .velocity()
                        .texture(texture, textureStr)
                        .build()
            }
            MapEntityType.SquareSwitch -> {
                val textureStr = "square_switch"
                val texture = res.getTexture(textureStr + TOGGLE_ON)!!
                val targetId = props["targetId"]!! as Int

                EntityBuilder.instance(engine)
                        .mapEntity(type = type, mapCollidable = true, projectileCollidable = true)
                        .squareSwitch(targetId)
                        .boundingBox(texture.regionWidth.toFloat(), texture.regionHeight.toFloat())
                        .position(rect.x, rect.y)
                        .velocity()
                        .texture(texture, textureStr)
                        .build()
            }
            MapEntityType.ToggleTile -> {
                val texture = res.getTexture("toggle_square")!!
                val id = props["id"]!! as Int

                EntityBuilder.instance(engine)
                        .mapEntity(type = type, mapCollidable = true, projectileCollidable = true)
                        .toggleTile(id)
                        .boundingBox(texture.regionWidth.toFloat(), texture.regionHeight.toFloat())
                        .position(rect.x, rect.y)
                        .velocity()
                        .texture(texture, "toggle_square")
                        .build()
            }
            else -> {}
        }
    }

}