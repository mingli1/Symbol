package com.symbol.ecs.system.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.DirectionComponent
import com.symbol.ecs.component.GravityComponent
import com.symbol.ecs.component.PositionComponent
import com.symbol.ecs.component.VelocityComponent
import com.symbol.ecs.component.enemy.EnemyComponent
import com.symbol.ecs.entity.EnemyMovementType
import com.symbol.ecs.entity.Player
import com.symbol.util.Resources

private const val MOVEMENT_FREQUENCY = 0.7f
private const val JUMP_FREQUENCY = 1.2f

class EnemyMovementSystem(private val player: Player, private val res: Resources)
    : IteratingSystem(Family.all(EnemyComponent::class.java).get()) {

    private var movementTimers: MutableMap<Entity, Float> = HashMap()
    private var jumpTimers: MutableMap<Entity, Float> = HashMap()
    private var corporealTimers: MutableMap<Entity, Float> = HashMap()

    fun reset() {
        movementTimers.clear()
        jumpTimers.clear()
        corporealTimers.clear()
        for (entity in entities) {
            movementTimers[entity] = 0f
            jumpTimers[entity] = 0f
            corporealTimers[entity] = 0f
        }
    }

    override fun processEntity(entity: Entity?, dt: Float) {
        val enemyComponent = Mapper.ENEMY_MAPPER.get(entity)
        val activation = Mapper.ACTIVATION_MAPPER.get(entity)
        val corp = Mapper.CORPOREAL_MAPPER.get(entity)
        val dirComponent = Mapper.DIR_MAPPER.get(entity)
        val position = Mapper.POS_MAPPER.get(entity)
        val velocity = Mapper.VEL_MAPPER.get(entity)
        val gravity = Mapper.GRAVITY_MAPPER.get(entity)
        val jump = Mapper.JUMP_MAPPER.get(entity)

        if (corp != null && corp.incorporealTime != 0f) {
            corporealTimers[entity!!] = corporealTimers[entity]?.plus(dt)!!
            if (corporealTimers[entity]!! >= corp.incorporealTime) {
                corp.corporeal = !corp.corporeal

                if (!corp.corporeal) {
                    val texture = Mapper.TEXTURE_MAPPER.get(entity)
                    texture.texture = res.getTexture(texture.textureStr + Resources.INCORPOREAL) ?: texture.texture
                }

                corporealTimers[entity] = 0f
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
            }
        }
    }

    private fun backAndForth(entity: Entity?, p: PositionComponent, v: VelocityComponent, dir: DirectionComponent, g: GravityComponent) {
        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)

        if (v.dx == 0f) v.dx = if (dir.facingRight) v.speed else -v.speed
        if (p.x < g.platform.x) {
            v.dx = v.speed
        }
        else if (p.x > g.platform.x + g.platform.width - bounds.rect.width) {
            v.dx = -v.speed
        }
    }

    private fun charge(p: PositionComponent, v: VelocityComponent) {
        val playerPosition = Mapper.POS_MAPPER.get(player)
        if (v.dx == 0f) v.dx = if (p.x < playerPosition.x) v.speed else -v.speed
    }

    private fun random(entity: Entity?, dt: Float, p: PositionComponent, v: VelocityComponent, g: GravityComponent) {
        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)
        if (p.x < g.platform.x) {
            v.dx = v.speed
            return
        }
        else if (p.x > g.platform.x + g.platform.width - bounds.rect.width) {
            v.dx = -v.speed
            return
        }

        movementTimers[entity!!] = movementTimers[entity]?.plus(dt)!!
        if (movementTimers[entity]!! >= MOVEMENT_FREQUENCY) {
            val action = MathUtils.random(2)
            when (action) {
                1 -> v.dx = -v.speed
                2 -> v.dx = v.speed
                else -> v.dx = 0f
            }
            movementTimers[entity] = 0f
        }
    }

    private fun randomWithJump(entity: Entity?, dt: Float, p: PositionComponent,
                               v: VelocityComponent, g: GravityComponent) {
        random(entity, dt, p, v, g)
        val jump = Mapper.JUMP_MAPPER.get(entity)
        jumpTimers[entity!!] = jumpTimers[entity]?.plus(dt)!!
        if (jumpTimers[entity]!! >= JUMP_FREQUENCY) {
            if (jump.impulse != 0f && MathUtils.randomBoolean()) {
                v.dy = jump.impulse
            }
            jumpTimers[entity] = 0f
        }
    }

    private fun orbit(entity: Entity?, enemyComponent: EnemyComponent) {
        val orbit = Mapper.ORBIT_MAPPER.get(entity)
        val remove = Mapper.REMOVE_MAPPER.get(entity)
        val parentRemove = Mapper.REMOVE_MAPPER.get(enemyComponent.parent)

        if (parentRemove != null && !parentRemove.shouldRemove) {
            val bounds = Mapper.BOUNDING_BOX_MAPPER.get(enemyComponent.parent)
            val originX = bounds.rect.x + bounds.rect.width / 2
            val originY = bounds.rect.y + bounds.rect.height / 2

            orbit?.setOrigin(originX, originY)
        }
        else {
            remove.shouldRemove = true
        }
    }

}