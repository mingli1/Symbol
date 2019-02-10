package com.symbol.ecs.system.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.DirectionComponent
import com.symbol.ecs.component.EnemyComponent
import com.symbol.ecs.component.PositionComponent
import com.symbol.ecs.component.VelocityComponent
import com.symbol.ecs.entity.EnemyMovementType
import com.symbol.ecs.entity.Player

class EnemyMovementSystem(private val player: Player) : IteratingSystem(Family.all(EnemyComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, dt: Float) {
        val enemyComponent = Mapper.ENEMY_MAPPER.get(entity)
        val dirComponent = Mapper.DIR_MAPPER.get(entity)
        val position = Mapper.POS_MAPPER.get(entity)
        val velocity = Mapper.VEL_MAPPER.get(entity)

        if (enemyComponent.active) {
            when (enemyComponent.movementType) {
                EnemyMovementType.BackAndForth -> backAndForth(entity, position, velocity, dirComponent)
            }
        }
    }

    private fun backAndForth(entity: Entity?, p: PositionComponent, v: VelocityComponent, dir: DirectionComponent) {
        val gravity = Mapper.GRAVITY_MAPPER.get(entity)
        val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)

        if (gravity.onGround) {
            if (v.dx == 0f) v.dx = if (dir.facingRight) v.speed else -v.speed
            if (p.x < gravity.platform.x) {
                p.x = gravity.platform.x
                v.dx = v.speed
            }
            else if (p.x > gravity.platform.x + gravity.platform.width - bounds.rect.width) {
                p.x = gravity.platform.x + gravity.platform.width - bounds.rect.width
                v.dx = -v.speed
            }
        }
    }

}