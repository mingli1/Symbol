package com.symbol.ecs.system.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.symbol.ecs.EntityBuilder
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.Direction
import com.symbol.ecs.component.DirectionComponent
import com.symbol.ecs.component.EnemyComponent
import com.symbol.ecs.component.ProjectileMovementType
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
                    EnemyAttackType.ShootOne -> shootOne(enemyComponent, dir, bounds)
                    EnemyAttackType.ShootTwoHorizontal -> shootTwoHorizontal(enemyComponent, dir, bounds)
                    EnemyAttackType.ShootTwoVertical -> shootTwoVertical(enemyComponent, dir, bounds)
                    EnemyAttackType.ShootFour -> shootFour(enemyComponent, dir, bounds)
                    EnemyAttackType.ShootFourDiagonal -> shootFourDiagonal(enemyComponent, dir, bounds)
                    EnemyAttackType.ShootEight -> shootEight(enemyComponent, dir, bounds)
                    EnemyAttackType.ShootAtPlayer -> shootAtPlayer(enemyComponent, dir, bounds, playerBounds)
                    EnemyAttackType.SprayThree -> sprayThree(enemyComponent, bounds)
                    EnemyAttackType.ShootAndQuake -> shootAtPlayer(enemyComponent, dir, bounds, playerBounds)
                    EnemyAttackType.Random -> random(enemyComponent, bounds, dir)
                    EnemyAttackType.ArcTwo -> arcTwo(enemyComponent, bounds, dir)
                    EnemyAttackType.HorizontalWave -> horizontalWave(enemyComponent, bounds, dir)
                    EnemyAttackType.VerticalWave -> verticalWave(enemyComponent, bounds, dir)
                    EnemyAttackType.TwoHorizontalWave -> twoHorizontalWave(enemyComponent, bounds, dir)
                    EnemyAttackType.TwoVerticalWave -> twoVerticalWave(enemyComponent, bounds, dir)
                    EnemyAttackType.FourWave -> fourWave(enemyComponent, bounds, dir)
                }
                enemyComponent.canAttack = false
            }
        }

        if (enemyComponent.explodeOnDeath) {
            explodeOnDeath(entity, enemyComponent, dir, bounds)
        }

        if (!enemyComponent.canAttack) {
            attackTimers[entity!!] = attackTimers[entity]?.plus(dt)!!
            if (attackTimers[entity]!! >= enemyComponent.attackRate) {
                attackTimers[entity] = 0f
                enemyComponent.canAttack = true
            }
        }
    }

    private fun shootOne(enemyComp: EnemyComponent, dir: DirectionComponent, bounds: Rectangle) {
        val texture = res.getTexture(enemyComp.attackTexture!!)!!
        createProjectile(enemyComp, dir, bounds, if (dir.facingRight) enemyComp.projectileSpeed else -enemyComp.projectileSpeed,
                0f, texture, enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
    }

    private fun shootTwoHorizontal(enemyComp: EnemyComponent, dir: DirectionComponent, bounds: Rectangle) {
        val texture = res.getTexture(enemyComp.attackTexture!!)!!
        createProjectile(enemyComp, dir, bounds, enemyComp.projectileSpeed, 0f, texture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
        createProjectile(enemyComp, dir, bounds, -enemyComp.projectileSpeed, 0f, texture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
    }

    private fun shootTwoVertical(enemyComp: EnemyComponent, dir: DirectionComponent, bounds: Rectangle) {
        val topTexture = res.getTexture(enemyComp.attackTexture + TOP) ?: res.getTexture(enemyComp.attackTexture!!)!!
        createProjectile(enemyComp, dir, bounds, 0f, enemyComp.projectileSpeed, topTexture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
        createProjectile(enemyComp, dir, bounds, 0f, -enemyComp.projectileSpeed, topTexture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
    }

    private fun shootFour(enemyComp: EnemyComponent, dir: DirectionComponent, bounds: Rectangle) {
        shootTwoHorizontal(enemyComp, dir, bounds)
        shootTwoVertical(enemyComp, dir, bounds)
    }

    private fun shootFourDiagonal(enemyComp: EnemyComponent, dir: DirectionComponent, bounds: Rectangle) {
        val trTexture = res.getTexture(enemyComp.attackTexture + TOP_RIGHT) ?: res.getTexture(enemyComp.attackTexture!!)!!
        createProjectile(enemyComp, dir, bounds, -enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING,
                enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, trTexture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
        createProjectile(enemyComp, dir, bounds, enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING,
                enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, trTexture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
        createProjectile(enemyComp, dir, bounds, -enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING,
                -enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, trTexture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
        createProjectile(enemyComp, dir, bounds, enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING,
                -enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, trTexture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
    }

    private fun shootEight(enemyComp: EnemyComponent, dir: DirectionComponent, bounds: Rectangle) {
        shootFour(enemyComp, dir, bounds)
        shootFourDiagonal(enemyComp, dir, bounds)
    }

    private fun shootAtPlayer(enemyComp: EnemyComponent, dir: DirectionComponent, bounds: Rectangle, playerBounds: Rectangle) {
        val topTexture = res.getTexture(enemyComp.attackTexture + TOP) ?: res.getTexture(enemyComp.attackTexture!!)!!
        val texture = res.getTexture(enemyComp.attackTexture!!)!!

        val xBiased = Math.abs(bounds.x - playerBounds.x) > Math.abs(bounds.y - playerBounds.y)
        val xCenter = playerBounds.x + playerBounds.width / 2
        val yCenter = playerBounds.y + playerBounds.height / 2

        dir.facingRight = bounds.x < xCenter

        if (bounds.x < xCenter && xBiased)
            createProjectile(enemyComp, dir, bounds, enemyComp.projectileSpeed, 0f, texture,
                    enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
        if (bounds.x >= xCenter && xBiased)
            createProjectile(enemyComp, dir, bounds, -enemyComp.projectileSpeed, 0f, texture,
                    enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
        if (bounds.y < yCenter && !xBiased)
            createProjectile(enemyComp, dir, bounds, 0f, enemyComp.projectileSpeed, topTexture,
                    enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
        if (bounds.y >= yCenter && !xBiased)
            createProjectile(enemyComp, dir, bounds, 0f, -enemyComp.projectileSpeed, topTexture,
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

    private fun random(enemyComp: EnemyComponent, bounds: Rectangle, dir: DirectionComponent) {
        val action = MathUtils.random(3)
        val texture = res.getTexture(enemyComp.attackTexture!!)!!
        val topTexture = res.getTexture(enemyComp.attackTexture + TOP) ?: res.getTexture(enemyComp.attackTexture!!)!!
        when (action) {
            0 -> {
                dir.facingRight = true
                createProjectile(enemyComp, dir, bounds, enemyComp.projectileSpeed, 0f, texture,
                        enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
            }
            1 -> {
                dir.facingRight = false
                createProjectile(enemyComp, dir, bounds, -enemyComp.projectileSpeed, 0f, texture,
                        enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
            }
            2 -> {
                createProjectile(enemyComp, dir, bounds, 0f, enemyComp.projectileSpeed, topTexture,
                        enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
            }
            3 -> {
                createProjectile(enemyComp, dir, bounds, 0f, -enemyComp.projectileSpeed, topTexture,
                        enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable)
            }
        }
    }

    private fun arcTwo(enemyComp: EnemyComponent, bounds: Rectangle, dir: DirectionComponent) {
        val texture = res.getTexture(enemyComp.attackTexture + TOP_RIGHT) ?: res.getTexture(enemyComp.attackTexture!!)!!
        val initialDx = if (dir.facingRight) -enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING
                            else enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING
        createProjectile(enemyComp, dir, bounds, initialDx,
                enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, texture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable,
                ProjectileMovementType.Arc)
        createProjectile(enemyComp, dir, bounds, initialDx,
                -enemyComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, texture,
                enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable,
                ProjectileMovementType.Arc)
    }

    private fun explodeOnDeath(entity: Entity?, enemyComp: EnemyComponent, dir: DirectionComponent, bounds: Rectangle) {
        val remove = Mapper.REMOVE_MAPPER.get(entity)
        if (remove.shouldRemove) {
            shootEight(enemyComp, dir, bounds)
        }
    }

    private fun horizontalWave(enemyComp: EnemyComponent, bounds: Rectangle, dir: DirectionComponent) {
        val texture = res.getTexture(enemyComp.attackTexture + TOP_RIGHT) ?: res.getTexture(enemyComp.attackTexture!!)!!
        val proj = createProjectile(enemyComp, dir, bounds, if (dir.facingRight) enemyComp.projectileSpeed else -enemyComp.projectileSpeed,
                0f, texture, enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable,
                ProjectileMovementType.Wave)
        val projComp = Mapper.PROJ_MAPPER.get(proj)
        projComp.waveDir = Direction.Right
    }

    private fun verticalWave(enemyComp: EnemyComponent, bounds: Rectangle, dir: DirectionComponent) {
        val texture = res.getTexture(enemyComp.attackTexture + TOP_RIGHT) ?: res.getTexture(enemyComp.attackTexture!!)!!
        val proj = createProjectile(enemyComp, dir, bounds, 0f,
                if (MathUtils.randomBoolean()) enemyComp.projectileSpeed else -enemyComp.projectileSpeed,
                texture, enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable,
                ProjectileMovementType.Wave)
        val projComp = Mapper.PROJ_MAPPER.get(proj)
        projComp.waveDir = Direction.Up
    }

    private fun twoHorizontalWave(enemyComp: EnemyComponent, bounds: Rectangle, dir: DirectionComponent) {
        val texture = res.getTexture(enemyComp.attackTexture + TOP_RIGHT) ?: res.getTexture(enemyComp.attackTexture!!)!!
        val projLeft = createProjectile(enemyComp, dir, bounds, -enemyComp.projectileSpeed,
                0f, texture, enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable,
                ProjectileMovementType.Wave)
        val projRight = createProjectile(enemyComp, dir, bounds, enemyComp.projectileSpeed,
                0f, texture, enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable,
                ProjectileMovementType.Wave)
        val pl = Mapper.PROJ_MAPPER.get(projLeft)
        val pr = Mapper.PROJ_MAPPER.get(projRight)
        pl.waveDir = Direction.Left
        pr.waveDir = Direction.Right
    }

    private fun twoVerticalWave(enemyComp: EnemyComponent, bounds: Rectangle, dir: DirectionComponent) {
        val texture = res.getTexture(enemyComp.attackTexture + TOP_RIGHT) ?: res.getTexture(enemyComp.attackTexture!!)!!
        val projTop = createProjectile(enemyComp, dir, bounds, 0f, enemyComp.projectileSpeed,
                texture, enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable,
                ProjectileMovementType.Wave)
        val projBot = createProjectile(enemyComp, dir, bounds, 0f, -enemyComp.projectileSpeed,
                texture, enemyComp.attackDetonateTime, enemyComp.projectileAcceleration, enemyComp.projectileDestroyable,
                ProjectileMovementType.Wave)
        val pt = Mapper.PROJ_MAPPER.get(projTop)
        val pb = Mapper.PROJ_MAPPER.get(projBot)
        pt.waveDir = Direction.Up
        pb.waveDir = Direction.Down
    }

    private fun fourWave(enemyComp: EnemyComponent, bounds: Rectangle, dir: DirectionComponent) {
        twoHorizontalWave(enemyComp, bounds, dir)
        twoVerticalWave(enemyComp, bounds, dir)
    }

    private fun createProjectile(enemyComp: EnemyComponent, dir: DirectionComponent, bounds: Rectangle,
                                 dx: Float = 0f, dy: Float = 0f, texture: TextureRegion,
                                 detonateTime: Float = 0f, acceleration: Float = 0f, destroyable: Boolean = false,
                                 movementType: ProjectileMovementType = ProjectileMovementType.Normal) : Entity? {
        val bw = texture.regionWidth - 1
        val bh = texture.regionHeight - 1
        return EntityBuilder.instance(engine as PooledEngine)
                .projectile(movementType = movementType,
                        parentFacingRight = dir.facingRight,
                        collidesWithTerrain = false, collidesWithProjectiles = destroyable,
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
                                        dx: Float, dy: Float, texture: TextureRegion, destroyable: Boolean = false) : Entity? {
        val bw = texture.regionWidth - 1
        val bh = texture.regionHeight - 1
        return EntityBuilder.instance(engine as PooledEngine)
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