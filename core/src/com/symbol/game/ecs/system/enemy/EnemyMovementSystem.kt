package com.symbol.game.ecs.system.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.DirectionComponent
import com.symbol.game.ecs.component.GravityComponent
import com.symbol.game.ecs.component.PositionComponent
import com.symbol.game.ecs.component.VelocityComponent
import com.symbol.game.ecs.component.enemy.EnemyComponent
import com.symbol.game.ecs.entity.EnemyMovementType
import com.symbol.game.ecs.entity.Player
import com.symbol.game.util.INCORPOREAL
import com.symbol.game.util.Resources

private const val MOVEMENT_FREQUENCY = 0.7f
private const val JUMP_FREQUENCY = 1.2f

class EnemyMovementSystem(private val player: Player, private val res: Resources)
    : IteratingSystem(Family.all(EnemyComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, dt: Float) {
        val enemyComponent = Mapper.ENEMY_MAPPER[entity]
        val activation = Mapper.ACTIVATION_MAPPER[entity]
        val corp = Mapper.CORPOREAL_MAPPER[entity]
        val dirComponent = Mapper.DIR_MAPPER[entity]
        val position = Mapper.POS_MAPPER[entity]
        val velocity = Mapper.VEL_MAPPER[entity]
        val gravity = Mapper.GRAVITY_MAPPER[entity]
        val jump = Mapper.JUMP_MAPPER[entity]

        if (corp != null && corp.incorporealTime != 0f) {
            corp.timer += dt
            if (corp.timer >= corp.incorporealTime) {
                corp.corporeal = !corp.corporeal

                if (!corp.corporeal) {
                    val texture = Mapper.TEXTURE_MAPPER[entity]
                    texture.texture = res.getTexture(texture.textureStr + INCORPOREAL) ?: texture.texture
                }

                corp.timer = 0f
            }
        }

        if (activation.active) {
            if (gravity != null) {
                if (gravity.onGround && jump != null
                        && enemyComponent.movementType != EnemyMovementType.RandomWithJump) {
                    velocity.dy = jump.impulse
                }
            }
            when (enemyComponent.movementType) {
                EnemyMovementType.None -> return
                EnemyMovementType.BackAndForth -> backAndForth(entity, position, velocity, dirComponent, gravity)
                EnemyMovementType.Charge -> charge(position, velocity)
                EnemyMovementType.Random -> random(entity, dt, position, velocity, gravity)
                EnemyMovementType.RandomWithJump -> randomWithJump(entity, dt, position, velocity, gravity)
                EnemyMovementType.Orbit -> orbit(entity, enemyComponent)
                EnemyMovementType.TeleportTriangle -> teleportTriangle(entity, dt, position)
                EnemyMovementType.TeleportSquare -> teleportSquare(entity, dt, position)
            }
        }
    }

    private fun backAndForth(entity: Entity?, p: PositionComponent, v: VelocityComponent, dir: DirectionComponent, g: GravityComponent) {
        val bounds = Mapper.BOUNDING_BOX_MAPPER[entity]

        if (v.dx == 0f) v.dx = if (dir.facingRight) v.speed else -v.speed
        if (p.x < g.platform.x) {
            v.dx = v.speed
        }
        else if (p.x > g.platform.x + g.platform.width - bounds.rect.width) {
            v.dx = -v.speed
        }
    }

    private fun charge(p: PositionComponent, v: VelocityComponent) {
        val playerPosition = Mapper.POS_MAPPER[player]
        if (v.dx == 0f) v.dx = if (p.x < playerPosition.x) v.speed else -v.speed
    }

    private fun random(entity: Entity?, dt: Float, p: PositionComponent, v: VelocityComponent, g: GravityComponent) {
        val enemy = Mapper.ENEMY_MAPPER[entity]
        val bounds = Mapper.BOUNDING_BOX_MAPPER[entity]
        if (p.x < g.platform.x) {
            v.dx = v.speed
            return
        }
        else if (p.x > g.platform.x + g.platform.width - bounds.rect.width) {
            v.dx = -v.speed
            return
        }

        enemy.movementTimer += dt
        if (enemy.movementTimer >= MOVEMENT_FREQUENCY) {
            val action = MathUtils.random(2)
            when (action) {
                1 -> v.dx = -v.speed
                2 -> v.dx = v.speed
                else -> v.dx = 0f
            }
            enemy.movementTimer = 0f
        }
    }

    private fun randomWithJump(entity: Entity?, dt: Float, p: PositionComponent,
                               v: VelocityComponent, g: GravityComponent) {
        random(entity, dt, p, v, g)
        val jump = Mapper.JUMP_MAPPER[entity]
        jump.timer += dt
        if (jump.timer >= JUMP_FREQUENCY) {
            if (jump.impulse != 0f && MathUtils.randomBoolean()) {
                v.dy = jump.impulse
            }
            jump.timer = 0f
        }
    }

    private fun orbit(entity: Entity?, enemyComponent: EnemyComponent) {
        val remove = Mapper.REMOVE_MAPPER[entity]
        val parentRemove = Mapper.REMOVE_MAPPER[enemyComponent.parent]

        if (parentRemove != null && !parentRemove.shouldRemove) {
            val position = Mapper.POS_MAPPER[entity]
            val bounds = Mapper.BOUNDING_BOX_MAPPER[enemyComponent.parent]
            val originX = bounds.rect.x + bounds.rect.width / 2
            val originY = bounds.rect.y + bounds.rect.height / 2

            position.originX = originX
            position.originY = originY
        }
        else {
            remove.shouldRemove = true
        }
    }

    private fun teleportTriangle(entity: Entity?, dt: Float, position: PositionComponent) {
        val enemy = Mapper.ENEMY_MAPPER[entity]
        val teleport = Mapper.TELEPORT_MAPPER[entity]

        enemy.movementTimer += dt
        if (enemy.movementTimer >= teleport.freq) {
            enemy.movementTimer = 0f
            teleport.pos++
            if (teleport.pos > 2) teleport.pos = 0
        }

        when (teleport.pos) {
            0 -> position.set(position.originX, position.originY)
            1 -> position.set(position.originX + teleport.range / 2f,
                    position.originY + MathUtils.sin(MathUtils.PI / 3f) * teleport.range)
            2 -> position.set(position.originX + teleport.range, position.originY)
        }
    }

    private fun teleportSquare(entity: Entity?, dt: Float, position: PositionComponent) {
        val enemy = Mapper.ENEMY_MAPPER[entity]
        val teleport = Mapper.TELEPORT_MAPPER[entity]

        enemy.movementTimer += dt
        if (enemy.movementTimer >= teleport.freq) {
            enemy.movementTimer = 0f
            teleport.pos++
            if (teleport.pos > 3) teleport.pos = 0
        }

        when (teleport.pos) {
            0 -> position.set(position.originX, position.originY)
            1 -> position.set(position.originX, position.originY + teleport.range)
            2 -> position.set(position.originX + teleport.range, position.originY + teleport.range)
            3 -> position.set(position.originX + teleport.range, position.originY)
        }
    }

}