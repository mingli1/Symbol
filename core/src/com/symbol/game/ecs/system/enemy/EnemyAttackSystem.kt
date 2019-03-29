package com.symbol.game.ecs.system.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.symbol.game.ecs.EntityBuilder
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.Direction
import com.symbol.game.ecs.component.DirectionComponent
import com.symbol.game.ecs.component.ProjectileMovementType
import com.symbol.game.ecs.component.enemy.AttackComponent
import com.symbol.game.ecs.component.enemy.EnemyComponent
import com.symbol.game.ecs.entity.EnemyAttackType
import com.symbol.game.ecs.entity.EntityColor
import com.symbol.game.ecs.entity.Player
import com.symbol.game.ecs.system.DIAGONAL_PROJECTILE_SCALING
import com.symbol.game.effects.particle.DEFAULT_INTESITY
import com.symbol.game.effects.particle.DEFAULT_LIFETIME
import com.symbol.game.effects.particle.ParticleSpawner
import com.symbol.game.map.camera.CameraShake
import com.symbol.game.util.Resources
import com.symbol.game.util.TOP
import com.symbol.game.util.TOP_RIGHT

private const val CAMERA_SHAKE_POWER = 3f
private const val CAMERA_SHAKE_DURATION = 0.7f

private const val TRAP_EXPLODE_TIME = 2f

class EnemyAttackSystem(private val player: Player, private val res: Resources) :
        IteratingSystem(Family.all(EnemyComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, dt: Float) {
        val enemyComponent = Mapper.ENEMY_MAPPER.get(entity)
        val activation = Mapper.ACTIVATION_MAPPER.get(entity)
        val attack = Mapper.ATTACK_MAPPER.get(entity)
        val remove = Mapper.REMOVE_MAPPER.get(entity)
        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity).rect
        val playerBounds = Mapper.BOUNDING_BOX_MAPPER.get(player).rect
        val dir = Mapper.DIR_MAPPER.get(entity)

        if (bounds.overlaps(playerBounds)) {
            val playerHealth = Mapper.HEALTH_MAPPER.get(player)
            playerHealth.hit(attack.damage)
            remove.shouldRemove = true

            val color = Mapper.COLOR_MAPPER.get(entity)
            ParticleSpawner.spawn(res, color.hex!!, DEFAULT_LIFETIME, DEFAULT_INTESITY + attack.damage,
                    bounds.x + bounds.width / 2, bounds.y + bounds.height / 2)
            return
        }

        if (activation.active) {
            if (enemyComponent.attackType == EnemyAttackType.ShootAndQuake) {
                val gravity = Mapper.GRAVITY_MAPPER.get(entity)
                if (gravity.onGround) {
                    CameraShake.shakeFor(CAMERA_SHAKE_POWER, CAMERA_SHAKE_DURATION)
                }
            }
            if (attack.canAttack) {
                when (enemyComponent.attackType) {
                    EnemyAttackType.None -> {}
                    EnemyAttackType.ShootOne -> shootOne(attack, dir, bounds)
                    EnemyAttackType.ShootTwoHorizontal -> shootTwoHorizontal(attack, dir, bounds)
                    EnemyAttackType.ShootTwoVertical -> shootTwoVertical(attack, dir, bounds)
                    EnemyAttackType.ShootFour -> shootFour(attack, dir, bounds)
                    EnemyAttackType.ShootFourDiagonal -> shootFourDiagonal(attack, dir, bounds)
                    EnemyAttackType.ShootEight -> shootEight(attack, dir, bounds)
                    EnemyAttackType.ShootAtPlayer -> shootAtPlayer(attack, dir, bounds, playerBounds)
                    EnemyAttackType.SprayThree -> sprayThree(attack, bounds)
                    EnemyAttackType.ShootAndQuake -> shootAtPlayer(attack, dir, bounds, playerBounds)
                    EnemyAttackType.Random -> random(attack, bounds, dir)
                    EnemyAttackType.ArcTwo -> arcTwo(attack, bounds, dir)
                    EnemyAttackType.HorizontalWave -> horizontalWave(attack, bounds, dir)
                    EnemyAttackType.VerticalWave -> verticalWave(attack, bounds, dir)
                    EnemyAttackType.TwoHorizontalWave -> twoHorizontalWave(attack, bounds, dir)
                    EnemyAttackType.TwoVerticalWave -> twoVerticalWave(attack, bounds, dir)
                    EnemyAttackType.FourWave -> fourWave(attack, bounds, dir)
                }
                attack.canAttack = false
            }
        }

        if (Mapper.EXPLODE_MAPPER.get(entity) != null) {
            explodeOnDeath(entity, attack, dir, bounds)
        }

        val trap = Mapper.TRAP_MAPPER.get(entity)
        if (trap != null) {
            if (trap.countdown) {
                trap.timer += dt
                if (trap.timer >= TRAP_EXPLODE_TIME) {
                    remove.shouldRemove = true
                    if (trap.hits != 3) explodeOnDeath(entity, attack, dir, bounds)
                }
            }
        }

        if (!attack.canAttack) {
            attack.timer += dt
            if (attack.timer >= attack.attackRate) {
                attack.timer = 0f
                attack.canAttack = true
            }
        }
    }

    private fun shootOne(attackComp: AttackComponent, dir: DirectionComponent, bounds: Rectangle) {
        val texture = res.getTexture(attackComp.attackTexture!!)!!
        createProjectile(attackComp, dir, bounds,
                if (dir.facingRight) attackComp.projectileSpeed else -attackComp.projectileSpeed, 0f, texture)
    }

    private fun shootTwoHorizontal(attackComp: AttackComponent, dir: DirectionComponent, bounds: Rectangle) {
        val texture = res.getTexture(attackComp.attackTexture!!)!!
        createProjectile(attackComp, dir, bounds, attackComp.projectileSpeed, 0f, texture)
        createProjectile(attackComp, dir, bounds, -attackComp.projectileSpeed, 0f, texture)
    }

    private fun shootTwoVertical(attackComp: AttackComponent, dir: DirectionComponent, bounds: Rectangle) {
        val topTexture = res.getTexture(attackComp.attackTexture + TOP) ?: res.getTexture(attackComp.attackTexture!!)!!
        createProjectile(attackComp, dir, bounds, 0f, attackComp.projectileSpeed, topTexture)
        createProjectile(attackComp, dir, bounds, 0f, -attackComp.projectileSpeed, topTexture)
    }

    private fun shootFour(attackComp: AttackComponent, dir: DirectionComponent, bounds: Rectangle) {
        shootTwoHorizontal(attackComp, dir, bounds)
        shootTwoVertical(attackComp, dir, bounds)
    }

    private fun shootFourDiagonal(attackComp: AttackComponent, dir: DirectionComponent, bounds: Rectangle) {
        val trTexture = res.getTexture(attackComp.attackTexture + TOP_RIGHT) ?: res.getTexture(attackComp.attackTexture!!)!!
        createProjectile(attackComp, dir, bounds, -attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING,
                attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, trTexture)
        createProjectile(attackComp, dir, bounds, attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING,
                attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, trTexture)
        createProjectile(attackComp, dir, bounds, -attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING,
                -attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, trTexture)
        createProjectile(attackComp, dir, bounds, attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING,
                -attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, trTexture)
    }

    private fun shootEight(attackComp: AttackComponent, dir: DirectionComponent, bounds: Rectangle) {
        shootFour(attackComp, dir, bounds)
        shootFourDiagonal(attackComp, dir, bounds)
    }

    private fun shootAtPlayer(attackComp: AttackComponent, dir: DirectionComponent, bounds: Rectangle, playerBounds: Rectangle) {
        val topTexture = res.getTexture(attackComp.attackTexture + TOP) ?: res.getTexture(attackComp.attackTexture!!)!!
        val texture = res.getTexture(attackComp.attackTexture!!)!!

        val xBiased = Math.abs(bounds.x - playerBounds.x) > Math.abs(bounds.y - playerBounds.y)
        val xCenter = playerBounds.x + playerBounds.width / 2
        val yCenter = playerBounds.y + playerBounds.height / 2

        dir.facingRight = bounds.x < xCenter

        if (bounds.x < xCenter && xBiased)
            createProjectile(attackComp, dir, bounds, attackComp.projectileSpeed, 0f, texture)
        if (bounds.x >= xCenter && xBiased)
            createProjectile(attackComp, dir, bounds, -attackComp.projectileSpeed, 0f, texture)
        if (bounds.y < yCenter && !xBiased)
            createProjectile(attackComp, dir, bounds, 0f, attackComp.projectileSpeed, topTexture)
        if (bounds.y >= yCenter && !xBiased)
            createProjectile(attackComp, dir, bounds, 0f, -attackComp.projectileSpeed, topTexture)
    }

    private fun sprayThree(attackComp: AttackComponent, bounds: Rectangle) {
        val topTexture = res.getTexture(attackComp.attackTexture + TOP) ?: res.getTexture(attackComp.attackTexture!!)!!
        val side = res.getTexture(attackComp.attackTexture + TOP_RIGHT) ?: res.getTexture(attackComp.attackTexture!!)!!
        createGravityProjectile(attackComp, bounds, 0f, attackComp.projectileSpeed, topTexture)
        createGravityProjectile(attackComp, bounds, -attackComp.projectileSpeed / 4, attackComp.projectileSpeed, side)
        createGravityProjectile(attackComp, bounds, attackComp.projectileSpeed / 4, attackComp.projectileSpeed, side)
    }

    private fun random(attackComp: AttackComponent, bounds: Rectangle, dir: DirectionComponent) {
        val action = MathUtils.random(3)
        val texture = res.getTexture(attackComp.attackTexture!!)!!
        val topTexture = res.getTexture(attackComp.attackTexture + TOP) ?: res.getTexture(attackComp.attackTexture!!)!!
        when (action) {
            0 -> {
                dir.facingRight = true
                createProjectile(attackComp, dir, bounds, attackComp.projectileSpeed, 0f, texture)
            }
            1 -> {
                dir.facingRight = false
                createProjectile(attackComp, dir, bounds, -attackComp.projectileSpeed, 0f, texture)
            }
            2 -> {
                createProjectile(attackComp, dir, bounds, 0f, attackComp.projectileSpeed, topTexture)
            }
            3 -> {
                createProjectile(attackComp, dir, bounds, 0f, -attackComp.projectileSpeed, topTexture)
            }
        }
    }

    private fun arcTwo(attackComp: AttackComponent, bounds: Rectangle, dir: DirectionComponent) {
        val texture = res.getTexture(attackComp.attackTexture + TOP_RIGHT) ?: res.getTexture(attackComp.attackTexture!!)!!
        val initialDx = if (dir.facingRight) -attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING
                            else attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING
        createProjectile(attackComp, dir, bounds, initialDx,
                attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, texture, ProjectileMovementType.Arc)
        createProjectile(attackComp, dir, bounds, initialDx,
                -attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, texture, ProjectileMovementType.Arc)
    }

    private fun explodeOnDeath(entity: Entity?, attackComp: AttackComponent, dir: DirectionComponent, bounds: Rectangle) {
        val remove = Mapper.REMOVE_MAPPER.get(entity)
        if (remove.shouldRemove) {
            shootEight(attackComp, dir, bounds)
        }
    }

    private fun horizontalWave(attackComp: AttackComponent, bounds: Rectangle, dir: DirectionComponent) {
        val texture = res.getTexture(attackComp.attackTexture + TOP_RIGHT) ?: res.getTexture(attackComp.attackTexture!!)!!
        val proj = createProjectile(attackComp, dir, bounds, if (dir.facingRight) attackComp.projectileSpeed else -attackComp.projectileSpeed,
                0f, texture, ProjectileMovementType.Wave)
        val projComp = Mapper.PROJ_MAPPER.get(proj)
        projComp.waveDir = Direction.Right
    }

    private fun verticalWave(attackComp: AttackComponent, bounds: Rectangle, dir: DirectionComponent) {
        val texture = res.getTexture(attackComp.attackTexture + TOP_RIGHT) ?: res.getTexture(attackComp.attackTexture!!)!!
        val proj = createProjectile(attackComp, dir, bounds, 0f,
                if (MathUtils.randomBoolean()) attackComp.projectileSpeed else -attackComp.projectileSpeed,
                texture, ProjectileMovementType.Wave)
        val projComp = Mapper.PROJ_MAPPER.get(proj)
        projComp.waveDir = Direction.Up
    }

    private fun twoHorizontalWave(attackComp: AttackComponent, bounds: Rectangle, dir: DirectionComponent) {
        val texture = res.getTexture(attackComp.attackTexture + TOP_RIGHT) ?: res.getTexture(attackComp.attackTexture!!)!!
        val projLeft = createProjectile(attackComp, dir, bounds, -attackComp.projectileSpeed,
                0f, texture, ProjectileMovementType.Wave)
        val projRight = createProjectile(attackComp, dir, bounds, attackComp.projectileSpeed,
                0f, texture, ProjectileMovementType.Wave)
        val pl = Mapper.PROJ_MAPPER.get(projLeft)
        val pr = Mapper.PROJ_MAPPER.get(projRight)
        pl.waveDir = Direction.Left
        pr.waveDir = Direction.Right
    }

    private fun twoVerticalWave(attackComp: AttackComponent, bounds: Rectangle, dir: DirectionComponent) {
        val texture = res.getTexture(attackComp.attackTexture + TOP_RIGHT) ?: res.getTexture(attackComp.attackTexture!!)!!
        val projTop = createProjectile(attackComp, dir, bounds, 0f, attackComp.projectileSpeed,
                texture, ProjectileMovementType.Wave)
        val projBot = createProjectile(attackComp, dir, bounds, 0f, -attackComp.projectileSpeed,
                texture, ProjectileMovementType.Wave)
        val pt = Mapper.PROJ_MAPPER.get(projTop)
        val pb = Mapper.PROJ_MAPPER.get(projBot)
        pt.waveDir = Direction.Up
        pb.waveDir = Direction.Down
    }

    private fun fourWave(attackComp: AttackComponent, bounds: Rectangle, dir: DirectionComponent) {
        twoHorizontalWave(attackComp, bounds, dir)
        twoVerticalWave(attackComp, bounds, dir)
    }

    private fun createProjectile(attackComp: AttackComponent, dir: DirectionComponent, bounds: Rectangle,
                                 dx: Float = 0f, dy: Float = 0f, texture: TextureRegion,
                                 movementType: ProjectileMovementType = ProjectileMovementType.Normal) : Entity? {
        val bw = texture.regionWidth - 1
        val bh = texture.regionHeight - 1
        return EntityBuilder.instance(engine as PooledEngine)
                .projectile(movementType = movementType,
                        parentFacingRight = dir.facingRight,
                        collidesWithTerrain = false, collidesWithProjectiles = attackComp.projectileDestroyable,
                        textureStr = attackComp.attackTexture, enemy = true,
                        damage = attackComp.damage, detonateTime = attackComp.attackDetonateTime, acceleration = attackComp.projectileAcceleration)
                .color(EntityColor.getProjectileColor(attackComp.attackTexture)!!)
                .position(bounds.x + (bounds.width / 2) - (bw / 2), bounds.y + (bounds.height / 2) - (bh / 2))
                .velocity(dx = dx, dy = dy)
                .boundingBox(bw.toFloat(), bh.toFloat())
                .texture(texture)
                .direction(yFlip = true).remove().build()
    }

    private fun createGravityProjectile(attackComp: AttackComponent, bounds: Rectangle,
                                        dx: Float, dy: Float, texture: TextureRegion) : Entity? {
        val bw = texture.regionWidth - 1
        val bh = texture.regionHeight - 1
        return EntityBuilder.instance(engine as PooledEngine)
                .projectile(collidesWithTerrain = false, collidesWithProjectiles = attackComp.projectileDestroyable,
                        textureStr = attackComp.attackTexture, enemy = true, damage = attackComp.damage)
                .color(EntityColor.getProjectileColor(attackComp.attackTexture)!!)
                .position(bounds.x + (bounds.width / 2) - (bw / 2), bounds.y + (bounds.height / 2) - (bh / 2))
                .velocity(dx = dx, dy = dy)
                .boundingBox(bw.toFloat(), bh.toFloat())
                .texture(texture)
                .direction(yFlip = true).gravity(collidable = false).remove().build()
    }

}