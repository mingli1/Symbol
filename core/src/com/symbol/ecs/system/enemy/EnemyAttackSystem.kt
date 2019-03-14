package com.symbol.ecs.system.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.symbol.ecs.EntityBuilder
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.DirectionComponent
import com.symbol.ecs.component.EnemyComponent
import com.symbol.ecs.entity.EnemyAttackType
import com.symbol.ecs.entity.EntityColor
import com.symbol.ecs.entity.Player
import com.symbol.ecs.system.DIAGONAL_PROJECTILE_SCALING
import com.symbol.effects.particle.DEFAULT_INTESITY
import com.symbol.effects.particle.DEFAULT_LIFETIME
import com.symbol.effects.particle.ParticleSpawner
import com.symbol.map.camera.CameraShake
import com.symbol.util.*

private const val CAMERA_SHAKE_POWER = 3f
private const val CAMERA_SHAKE_DURATION = 0.7f

class EnemyAttackSystem(private val player: Player, private val res: Resources) :
        IteratingSystem(Family.all(EnemyComponent::class.java).get()) {

    private var attackTimers: MutableMap<Entity, Float> = HashMap()

    fun reset() {
        attackTimers.clear()
        for (entity in entities) {
            attackTimers[entity] = 0f
        }
    }

    override fun processEntity(entity: Entity?, dt: Float) {
        val enemyComponent = Mapper.ENEMY_MAPPER.get(entity)
        val remove = Mapper.REMOVE_MAPPER.get(entity)
        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity).rect
        val playerBounds = Mapper.BOUNDING_BOX_MAPPER.get(player).rect
        val dir = Mapper.DIR_MAPPER.get(entity)

        if (bounds.overlaps(playerBounds)) {
            val playerHealth = Mapper.HEALTH_MAPPER.get(player)
            playerHealth.hit(enemyComponent.damage)
            remove.shouldRemove = true

            val color = Mapper.COLOR_MAPPER.get(entity)
            ParticleSpawner.spawn(res, color.hex!!, DEFAULT_LIFETIME, DEFAULT_INTESITY + enemyComponent.damage,
                    bounds.x + bounds.width / 2, bounds.y + bounds.height / 2)
            return
        }

        if (enemyComponent.active) {
            if (enemyComponent.attackType == EnemyAttackType.ShootAndQuake) {
                val gravity = Mapper.GRAVITY_MAPPER.get(entity)
                if (gravity.onGround) {
                    CameraShake.shakeFor(CAMERA_SHAKE_POWER, CAMERA_SHAKE_DURATION)
                }
            }
            if (enemyComponent.canAttack) {
                when (enemyComponent.attackType) {
                    EnemyAttackType.None -> {}
                    EnemyAttackType.ShootOne -> shootOne(enemyComponent, bounds, dir.facingRight)
                    EnemyAttackType.ShootTwoHorizontal -> shootTwoHorizontal(enemyComponent, bounds)
                    EnemyAttackType.ShootTwoVertical -> shootTwoVertical(enemyComponent, bounds)
                    EnemyAttackType.ShootFour -> shootFour(enemyComponent, bounds)
                    EnemyAttackType.ShootFourDiagonal -> shootFourDiagonal(enemyComponent, bounds)
                    EnemyAttackType.ShootEight -> shootEight(enemyComponent, bounds)
                    EnemyAttackType.ShootAtPlayer -> shootAtPlayer(enemyComponent, bounds, playerBounds, dir)
                    EnemyAttackType.SprayThree -> sprayThree(enemyComponent, bounds)
                    EnemyAttackType.ShootAndQuake -> shootAtPlayer(enemyComponent, bounds, playerBounds, dir)
                }
                enemyComponent.canAttack = false
            }
        }

        if (enemyComponent.explodeOnDeath) {
            explodeOnDeath(entity, enemyComponent, bounds)
        }

        if (!enemyComponent.canAttack) {
            attackTimers[entity!!] = attackTimers[entity]?.plus(dt)!!
            if (attackTimers[entity]!! >= enemyComponent.attackRate) {
                attackTimers[entity] = 0f
                enemyComponent.canAttack = true
            }
        }
    }

    private fun shootOne(enemyComp: EnemyComponent, bounds: Rectangle, facingRight: Boolean) {
        val texture = res.getTexture(enemyComp.attackTexture!!)!!
        createProjectile(enemyComp, bounds, if (facingRight) enemyComp.projectileSpeed else -enemyComp.projectileSpeed,
                0f, texture, enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
    }

    private fun shootTwoHorizontal(enemyComp: EnemyComponent, bounds: Rectangle) {
        val texture = res.getTexture(enemyComp.attackTexture!!)!!
        createProjectile(enemyComp, bounds, enemyComp.projectileSpeed, 0f, texture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
        createProjectile(enemyComp, bounds, -enemyComp.projectileSpeed, 0f, texture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
    }

    private fun shootTwoVertical(enemyComp: EnemyComponent, bounds: Rectangle) {
        val topTexture = res.getTexture(enemyComp.attackTexture + TOP) ?: res.getTexture(enemyComp.attackTexture!!)!!
        createProjectile(enemyComp, bounds, 0f, enemyComp.projectileSpeed, topTexture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
        createProjectile(enemyComp, bounds, 0f, -enemyComp.projectileSpeed, topTexture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
    }

    private fun shootFour(enemyComp: EnemyComponent, bounds: Rectangle) {
        shootTwoHorizontal(enemyComp, bounds)
        shootTwoVertical(enemyComp, bounds)
    }

    private fun shootFourDiagonal(enemyComp: EnemyComponent, bounds: Rectangle) {
        val trTexture = res.getTexture(enemyComp.attackTexture + TOP_RIGHT) ?: res.getTexture(enemyComp.attackTexture!!)!!
        createProjectile(enemyComp, bounds, -enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING,
                enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, trTexture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
        createProjectile(enemyComp, bounds, enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING,
                enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, trTexture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
        createProjectile(enemyComp, bounds, -enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING,
                -enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, trTexture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
        createProjectile(enemyComp, bounds, enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING,
                -enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, trTexture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
    }

    private fun shootEight(enemyComp: EnemyComponent, bounds: Rectangle) {
        shootFour(enemyComp, bounds)
        shootFourDiagonal(enemyComp, bounds)
    }

    private fun shootAtPlayer(enemyComp: EnemyComponent, bounds: Rectangle, playerBounds: Rectangle, dir: DirectionComponent) {
        val topTexture = res.getTexture(enemyComp.attackTexture + TOP) ?: res.getTexture(enemyComp.attackTexture!!)!!
        val texture = res.getTexture(enemyComp.attackTexture!!)!!

        val xBiased = Math.abs(bounds.x - playerBounds.x) > Math.abs(bounds.y - playerBounds.y)
        val xCenter = playerBounds.x + playerBounds.width / 2
        val yCenter = playerBounds.y + playerBounds.height / 2

        dir.facingRight = bounds.x < xCenter

        if (bounds.x < xCenter && xBiased)
            createProjectile(enemyComp, bounds, enemyComp.projectileSpeed, 0f, texture,
                    enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
        if (bounds.x >= xCenter && xBiased)
            createProjectile(enemyComp, bounds, -enemyComp.projectileSpeed, 0f, texture,
                    enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
        if (bounds.y < yCenter && !xBiased)
            createProjectile(enemyComp, bounds, 0f, enemyComp.projectileSpeed, topTexture,
                    enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
        if (bounds.y >= yCenter && !xBiased)
            createProjectile(enemyComp, bounds, 0f, -enemyComp.projectileSpeed, topTexture,
                    enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
    }

    private fun sprayThree(enemyComp: EnemyComponent, bounds: Rectangle) {
        val topTexture = res.getTexture(enemyComp.attackTexture + TOP) ?: res.getTexture(enemyComp.attackTexture!!)!!
        val side = res.getTexture(enemyComp.attackTexture + TOP_RIGHT) ?: res.getTexture(enemyComp.attackTexture!!)!!
        createGravityProjectile(enemyComp, bounds, 0f, enemyComp.projectileSpeed,
                topTexture, enemyComp.projectileDestroyable)
        createGravityProjectile(enemyComp, bounds, -enemyComp.projectileSpeed / 4,
                enemyComp.projectileSpeed, side, enemyComp.projectileDestroyable)
        createGravityProjectile(enemyComp, bounds, enemyComp.projectileSpeed / 4,
                enemyComp.projectileSpeed, side, enemyComp.projectileDestroyable)
    }

    private fun explodeOnDeath(entity: Entity?, enemyComp: EnemyComponent, bounds: Rectangle) {
        val remove = Mapper.REMOVE_MAPPER.get(entity)
        if (remove.shouldRemove) {
            shootEight(enemyComp, bounds)
        }
    }

    private fun createProjectile(enemyComp: EnemyComponent, bounds: Rectangle, dx: Float = 0f, dy: Float = 0f,
                                 texture: TextureRegion, detonateTime: Float = 0f, acceleration: Float = 0f,
                                 destroyable: Boolean = false) {
        val bw = texture.regionWidth - 1
        val bh = texture.regionHeight - 1
        EntityBuilder.instance(engine as PooledEngine)
                .projectile(collidesWithTerrain = false, collidesWithProjectiles = destroyable,
                        textureStr = enemyComp.attackTexture, enemy = true,
                        damage = enemyComp.damage, detonateTime = detonateTime, acceleration = acceleration)
                .color(EntityColor.getProjectileColor(enemyComp.attackTexture)!!)
                .position(bounds.x + (bounds.width / 2) - (bw / 2), bounds.y + (bounds.height / 2) - (bh / 2))
                .velocity(dx = dx, dy = dy)
                .boundingBox(bw.toFloat(), bh.toFloat())
                .texture(texture)
                .direction(yFlip = true).remove().build()
    }

    private fun createGravityProjectile(enemyComp: EnemyComponent, bounds: Rectangle,
                                        dx: Float, dy: Float, texture: TextureRegion, destroyable: Boolean = false) {
        val bw = texture.regionWidth - 1
        val bh = texture.regionHeight - 1
        EntityBuilder.instance(engine as PooledEngine)
                .projectile(collidesWithTerrain = false, collidesWithProjectiles = destroyable,
                        textureStr = enemyComp.attackTexture, enemy = true, damage = enemyComp.damage)
                .color(EntityColor.getProjectileColor(enemyComp.attackTexture)!!)
                .position(bounds.x + (bounds.width / 2) - (bw / 2), bounds.y + (bounds.height / 2) - (bh / 2))
                .velocity(dx = dx, dy = dy)
                .boundingBox(bw.toFloat(), bh.toFloat())
                .texture(texture)
                .direction(yFlip = true).gravity(collidable = false).remove().build()
    }

}