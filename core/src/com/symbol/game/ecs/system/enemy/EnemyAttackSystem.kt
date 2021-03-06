package com.symbol.game.ecs.system.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.*
import com.badlogic.gdx.math.Rectangle
import com.symbol.game.ecs.EntityBuilder
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.DirectionComponent
import com.symbol.game.ecs.component.ProjectileMovementType
import com.symbol.game.ecs.component.enemy.ActivationComponent
import com.symbol.game.ecs.component.enemy.AttackComponent
import com.symbol.game.ecs.component.enemy.EnemyComponent
import com.symbol.game.ecs.entity.EnemyAttackType
import com.symbol.game.ecs.entity.Player
import com.symbol.game.ecs.system.DIAGONAL_PROJECTILE_SCALING
import com.symbol.game.effects.particle.DEFAULT_INTESITY
import com.symbol.game.effects.particle.DEFAULT_LIFETIME
import com.symbol.game.effects.particle.ParticleSpawner
import com.symbol.game.map.camera.CameraShake
import com.symbol.game.util.Data
import com.symbol.game.util.Direction
import com.symbol.game.util.Resources
import kotlin.math.abs

private const val CAMERA_SHAKE_POWER = 3f
private const val CAMERA_SHAKE_DURATION = 0.7f

private const val TRAP_EXPLODE_TIME = 2f

class EnemyAttackSystem(private val player: Player,
                        private val res: Resources,
                        private val data: Data)
    : IteratingSystem(Family.all(EnemyComponent::class.java).get()) {

    private var mapWidth = 0f

    fun setMapData(mapWidth: Float) {
        this.mapWidth = mapWidth
    }

    override fun processEntity(entity: Entity?, dt: Float) {
        val enemyComponent = Mapper.ENEMY_MAPPER[entity]
        val activation = Mapper.ACTIVATION_MAPPER[entity]
        val attack = Mapper.ATTACK_MAPPER[entity]
        val remove = Mapper.REMOVE_MAPPER[entity]
        val bounds = Mapper.BOUNDING_BOX_MAPPER[entity].rect
        val playerBounds = Mapper.BOUNDING_BOX_MAPPER[player].rect
        val dir = Mapper.DIR_MAPPER[entity]

        if (bounds.overlaps(playerBounds) && Mapper.BLOCK_MAPPER[entity] == null) {
            Mapper.HEALTH_MAPPER[player].run { hit(attack.damage) }
            remove.shouldRemove = true

            val color = Mapper.COLOR_MAPPER[entity]
            ParticleSpawner.spawn(res, color.hex!!, DEFAULT_LIFETIME, (DEFAULT_INTESITY + attack.damage) * 2,
                    bounds.x + bounds.width / 2, bounds.y + bounds.height / 2)
            return
        }

        if (activation.active) {
            if (enemyComponent.attackType == EnemyAttackType.ShootAndQuake) {
                Mapper.GRAVITY_MAPPER[entity].run {
                    if (onGround) {
                        CameraShake.shakeFor(CAMERA_SHAKE_POWER, CAMERA_SHAKE_DURATION)
                    }
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
                    EnemyAttackType.ShootAtPlayer -> shootAtPlayer(attack, activation, dir, bounds, playerBounds)
                    EnemyAttackType.SprayThree -> sprayThree(attack, bounds)
                    EnemyAttackType.ShootAndQuake -> shootAtPlayer(attack, activation, dir, bounds, playerBounds)
                    EnemyAttackType.Random -> random(attack, bounds, dir)
                    EnemyAttackType.ArcTwo -> arcTwo(attack, bounds, dir)
                    EnemyAttackType.HorizontalWave -> horizontalWave(attack, bounds, dir)
                    EnemyAttackType.VerticalWave -> verticalWave(attack, bounds, dir)
                    EnemyAttackType.TwoHorizontalWave -> twoHorizontalWave(attack, bounds, dir)
                    EnemyAttackType.TwoVerticalWave -> twoVerticalWave(attack, bounds, dir)
                    EnemyAttackType.FourWave -> fourWave(attack, bounds, dir)
                    EnemyAttackType.ShootBoomerang -> shootBoomerang(attack, bounds, dir)
                    EnemyAttackType.ShootHoming -> shootHoming(attack, bounds, dir)
                }
                attack.canAttack = false
            }
        }

        if (Mapper.EXPLODE_MAPPER[entity] != null) {
            explodeOnDeath(entity, attack, dir, bounds)
        }

        Mapper.TRAP_MAPPER[entity]?.run {
            if (countdown) {
                timer += dt
                if (timer >= TRAP_EXPLODE_TIME) {
                    remove.shouldRemove = true
                    if (hits != 3) explodeOnDeath(entity, attack, dir, bounds)
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

    private fun shootOne(attackComp: AttackComponent, dir: DirectionComponent, bounds: Rectangle,
                         movementType: ProjectileMovementType = ProjectileMovementType.Normal) {
        val texture = res.getTexture(attackComp.attackTexture!!)!!
        createProjectile(attackComp, dir, bounds,
                if (dir.facingRight) attackComp.projectileSpeed else -attackComp.projectileSpeed, 0f, texture, movementType)
    }

    private fun shootTwoHorizontal(attackComp: AttackComponent, dir: DirectionComponent, bounds: Rectangle) {
        val texture = res.getTexture(attackComp.attackTexture!!)!!
        createProjectile(attackComp, dir, bounds, attackComp.projectileSpeed, 0f, texture)
        createProjectile(attackComp, dir, bounds, -attackComp.projectileSpeed, 0f, texture)
    }

    private fun shootTwoVertical(attackComp: AttackComponent, dir: DirectionComponent, bounds: Rectangle) {
        val texture = res.getTexture(attackComp.attackTexture!!)!!
        createProjectile(attackComp, dir, bounds, 0f, attackComp.projectileSpeed, texture)
        createProjectile(attackComp, dir, bounds, 0f, -attackComp.projectileSpeed, texture)
    }

    private fun shootFour(attackComp: AttackComponent, dir: DirectionComponent, bounds: Rectangle) {
        shootTwoHorizontal(attackComp, dir, bounds)
        shootTwoVertical(attackComp, dir, bounds)
    }

    private fun shootFourDiagonal(attackComp: AttackComponent, dir: DirectionComponent, bounds: Rectangle) {
        val texture = res.getTexture(attackComp.attackTexture!!)!!
        createProjectile(attackComp, dir, bounds, -attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING,
                attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, texture)
        createProjectile(attackComp, dir, bounds, attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING,
                attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, texture)
        createProjectile(attackComp, dir, bounds, -attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING,
                -attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, texture)
        createProjectile(attackComp, dir, bounds, attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING,
                -attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, texture)
    }

    private fun shootEight(attackComp: AttackComponent, dir: DirectionComponent, bounds: Rectangle) {
        shootFour(attackComp, dir, bounds)
        shootFourDiagonal(attackComp, dir, bounds)
    }

    private fun shootAtPlayer(attackComp: AttackComponent, activation: ActivationComponent,
                              dir: DirectionComponent, bounds: Rectangle, playerBounds: Rectangle) {
        val texture = res.getTexture(attackComp.attackTexture!!)!!

        val xCenter = bounds.x + bounds.width / 2
        val yCenter = bounds.y + bounds.height / 2
        val px = playerBounds.x + playerBounds.width / 2
        val py = playerBounds.y + playerBounds.height / 2
        val radius = if (activation.activationRange == -1f) mapWidth else activation.activationRange
        val speed = attackComp.projectileSpeed

        when {
            Intersector.isPointInTriangle(px, py, xCenter, yCenter,
                    xCenter + cos(PI / 8f) * radius, yCenter + sin(PI / 8f) * radius,
                    xCenter + cos(-PI / 8f) * radius, yCenter + sin(-PI / 8f) * radius) ->
                createProjectile(attackComp, dir, bounds, speed, 0f, texture)
            Intersector.isPointInTriangle(px, py, xCenter, yCenter,
                    xCenter + cos(PI / 8f) * radius, yCenter + sin(PI / 8f) * radius,
                    xCenter + cos(3 * PI / 8f) * radius, yCenter + sin(3 * PI / 8f) * radius) ->
                createProjectile(attackComp, dir, bounds, speed * DIAGONAL_PROJECTILE_SCALING,
                    speed * DIAGONAL_PROJECTILE_SCALING, texture)
            Intersector.isPointInTriangle(px, py, xCenter, yCenter,
                    xCenter + cos(3 * PI / 8f) * radius, yCenter + sin(3 * PI / 8f) * radius,
                    xCenter + cos(5 * PI / 8f) * radius, yCenter + sin(5 * PI / 8f) * radius) ->
                createProjectile(attackComp, dir, bounds, 0f, speed, texture)
            Intersector.isPointInTriangle(px, py, xCenter, yCenter,
                    xCenter + cos(5 * PI / 8f) * radius, yCenter + sin(5 * PI / 8f) * radius,
                    xCenter + cos(7 * PI / 8f) * radius, yCenter + sin(7 * PI / 8f) * radius) ->
                createProjectile(attackComp, dir, bounds, -speed * DIAGONAL_PROJECTILE_SCALING,
                    speed * DIAGONAL_PROJECTILE_SCALING, texture)
            Intersector.isPointInTriangle(px, py, xCenter, yCenter,
                    xCenter + cos(7 * PI / 8f) * radius, yCenter + sin(7 * PI / 8f) * radius,
                    xCenter + cos(9 * PI / 8f) * radius, yCenter + sin(9 * PI / 8f) * radius) ->
                createProjectile(attackComp, dir, bounds, -speed, 0f, texture)
            Intersector.isPointInTriangle(px, py, xCenter, yCenter,
                    xCenter + cos(9 * PI / 8f) * radius, yCenter + sin(9 * PI / 8f) * radius,
                    xCenter + cos(11 * PI / 8f) * radius, yCenter + sin(11 * PI / 8f) * radius) ->
                createProjectile(attackComp, dir, bounds, -speed * DIAGONAL_PROJECTILE_SCALING,
                    -speed * DIAGONAL_PROJECTILE_SCALING, texture)
            Intersector.isPointInTriangle(px, py, xCenter, yCenter,
                    xCenter + cos(11 * PI / 8f) * radius, yCenter + sin(11 * PI / 8f) * radius,
                    xCenter + cos(13 * PI / 8f) * radius, yCenter + sin(13 * PI / 8f) * radius) ->
                createProjectile(attackComp, dir, bounds, 0f, -speed, texture)
            Intersector.isPointInTriangle(px, py, xCenter, yCenter,
                    xCenter + cos(13 * PI / 8f) * radius, yCenter + sin(13 * PI / 8f) * radius,
                    xCenter + cos(-PI / 8f) * radius, yCenter + sin(-PI / 8f) * radius) ->
                createProjectile(attackComp, dir, bounds, speed * DIAGONAL_PROJECTILE_SCALING,
                    -speed * DIAGONAL_PROJECTILE_SCALING, texture)
        }
    }

    private fun sprayThree(attackComp: AttackComponent, bounds: Rectangle) {
        val texture = res.getTexture(attackComp.attackTexture!!)!!
        createGravityProjectile(attackComp, bounds, 0f, attackComp.projectileSpeed, texture)
        createGravityProjectile(attackComp, bounds, -attackComp.projectileSpeed / 4, attackComp.projectileSpeed, texture)
        createGravityProjectile(attackComp, bounds, attackComp.projectileSpeed / 4, attackComp.projectileSpeed, texture)
    }

    private fun random(attackComp: AttackComponent, bounds: Rectangle, dir: DirectionComponent) {
        val action = MathUtils.random(3)
        val texture = res.getTexture(attackComp.attackTexture!!)!!
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
                createProjectile(attackComp, dir, bounds, 0f, attackComp.projectileSpeed, texture)
            }
            3 -> {
                createProjectile(attackComp, dir, bounds, 0f, -attackComp.projectileSpeed, texture)
            }
        }
    }

    private fun arcTwo(attackComp: AttackComponent, bounds: Rectangle, dir: DirectionComponent) {
        val texture = res.getTexture(attackComp.attackTexture!!)!!
        val initialDx = if (dir.facingRight) -attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING
                            else attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING
        createProjectile(attackComp, dir, bounds, initialDx,
                attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, texture, ProjectileMovementType.Arc)
        createProjectile(attackComp, dir, bounds, initialDx,
                -attackComp.projectileSpeed * DIAGONAL_PROJECTILE_SCALING, texture, ProjectileMovementType.Arc)
    }

    private fun explodeOnDeath(entity: Entity?, attackComp: AttackComponent, dir: DirectionComponent, bounds: Rectangle) {
        Mapper.REMOVE_MAPPER[entity].run { if (shouldRemove) shootEight(attackComp, dir, bounds) }
    }

    private fun horizontalWave(attackComp: AttackComponent, bounds: Rectangle, dir: DirectionComponent) {
        val texture = res.getTexture(attackComp.attackTexture!!)!!
        val proj = createProjectile(attackComp, dir, bounds, if (dir.facingRight) attackComp.projectileSpeed else -attackComp.projectileSpeed,
                0f, texture, ProjectileMovementType.Wave)
        Mapper.PROJ_MAPPER[proj].run { waveDir = Direction.Right }
    }

    private fun verticalWave(attackComp: AttackComponent, bounds: Rectangle, dir: DirectionComponent) {
        val texture = res.getTexture(attackComp.attackTexture!!)!!
        val proj = createProjectile(attackComp, dir, bounds, 0f,
                if (MathUtils.randomBoolean()) attackComp.projectileSpeed else -attackComp.projectileSpeed,
                texture, ProjectileMovementType.Wave)
        val projComp = Mapper.PROJ_MAPPER[proj]
        projComp.waveDir = Direction.Up
    }

    private fun twoHorizontalWave(attackComp: AttackComponent, bounds: Rectangle, dir: DirectionComponent) {
        val texture = res.getTexture(attackComp.attackTexture!!)!!
        val projLeft = createProjectile(attackComp, dir, bounds, -attackComp.projectileSpeed,
                0f, texture, ProjectileMovementType.Wave)
        val projRight = createProjectile(attackComp, dir, bounds, attackComp.projectileSpeed,
                0f, texture, ProjectileMovementType.Wave)
        Mapper.PROJ_MAPPER[projLeft].run { waveDir = Direction.Left }
        Mapper.PROJ_MAPPER[projRight].run { waveDir = Direction.Right }
    }

    private fun twoVerticalWave(attackComp: AttackComponent, bounds: Rectangle, dir: DirectionComponent) {
        val texture = res.getTexture(attackComp.attackTexture!!)!!
        val projTop = createProjectile(attackComp, dir, bounds, 0f, attackComp.projectileSpeed,
                texture, ProjectileMovementType.Wave)
        val projBot = createProjectile(attackComp, dir, bounds, 0f, -attackComp.projectileSpeed,
                texture, ProjectileMovementType.Wave)
        Mapper.PROJ_MAPPER[projTop].run { waveDir = Direction.Up }
        Mapper.PROJ_MAPPER[projBot].run { waveDir = Direction.Down }
    }

    private fun fourWave(attackComp: AttackComponent, bounds: Rectangle, dir: DirectionComponent) {
        twoHorizontalWave(attackComp, bounds, dir)
        twoVerticalWave(attackComp, bounds, dir)
    }

    private fun shootBoomerang(attackComp: AttackComponent, bounds: Rectangle, dir: DirectionComponent) {
        shootOne(attackComp, dir, bounds, ProjectileMovementType.Boomerang)
    }

    private fun shootHoming(attackComp: AttackComponent, bounds: Rectangle, dir: DirectionComponent) {
        shootOne(attackComp, dir, bounds, ProjectileMovementType.Homing)
    }

    private fun createProjectile(attackComp: AttackComponent, dir: DirectionComponent, bounds: Rectangle,
                                 dx: Float = 0f, dy: Float = 0f, texture: TextureRegion,
                                 movementType: ProjectileMovementType = ProjectileMovementType.Normal) : Entity? {
        val bw = texture.regionWidth - 1
        val bh = texture.regionHeight - 1
        val originX = bounds.x + (bounds.width / 2) - (bw / 2)
        val originY = bounds.y + (bounds.height / 2) - (bh / 2)

        return EntityBuilder.instance(engine as PooledEngine)
                .projectile(movementType = movementType,
                        parentFacingRight = dir.facingRight,
                        collidesWithTerrain = false, collidesWithProjectiles = attackComp.projectileDestroyable,
                        textureStr = attackComp.attackTexture,
                        damage = attackComp.damage, detonateTime = attackComp.attackDetonateTime, acceleration = attackComp.projectileAcceleration)
                .color(data.getColor(attackComp.attackTexture!!)!!)
                .position(originX, originY)
                .velocity(dx = dx, dy = dy, speed = abs(if (dx != 0f) dx else dy))
                .boundingBox(bw.toFloat(), bh.toFloat())
                .texture(texture, attackComp.attackTexture)
                .direction(yFlip = true).remove().build()
    }

    private fun createGravityProjectile(attackComp: AttackComponent, bounds: Rectangle,
                                        dx: Float, dy: Float, texture: TextureRegion) : Entity? {
        val bw = texture.regionWidth - 1
        val bh = texture.regionHeight - 1
        val originX = bounds.x + (bounds.width / 2) - (bw / 2)
        val originY = bounds.y + (bounds.height / 2) - (bh / 2)

        return EntityBuilder.instance(engine as PooledEngine)
                .projectile(collidesWithTerrain = false, collidesWithProjectiles = attackComp.projectileDestroyable,
                        textureStr = attackComp.attackTexture, damage = attackComp.damage)
                .color(data.getColor(attackComp.attackTexture!!)!!)
                .position(originX, originY)
                .velocity(dx = dx, dy = dy, speed = abs(if (dx != 0f) dx else dy))
                .boundingBox(bw.toFloat(), bh.toFloat())
                .texture(texture, attackComp.attackTexture)
                .direction(yFlip = true).gravity(collidable = false).remove().build()
    }

}