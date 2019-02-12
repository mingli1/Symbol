package com.symbol.ecs.system.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.*
import com.symbol.ecs.entity.EnemyMovementType
import com.symbol.ecs.entity.Player

class EnemyMovementSystem(private val player: Player) : IteratingSystem(Family.all(EnemyComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, dt: Float) {
        val enemyComponent = Mapper.ENEMY_MAPPER.get(entity)
        val dirComponent = Mapper.DIR_MAPPER.get(entity)
        val position = Mapper.POS_MAPPER.get(entity)
        val velocity = Mapper.VEL_MAPPER.get(entity)
        val gravity = Mapper.GRAVITY_MAPPER.get(entity)

        if (enemyComponent.active) {
            if (gravity.onGround && enemyComponent.jumpImpulse != 0f) {
                velocity.dy = enemyComponent.jumpImpulse
            }
            when (enemyComponent.movementType) {
                EnemyMovementType.None -> return
                EnemyMovementType.BackAndForth -> backAndForth(entity, position, velocity, dirComponent, gravity)
                EnemyMovementType.Charge -> charge(position, velocity)
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

}