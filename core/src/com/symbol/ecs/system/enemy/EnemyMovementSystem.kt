package com.symbol.ecs.system.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.enemy.EnemyComponent
import com.symbol.ecs.entity.EnemyMovementType

class EnemyMovementSystem : IteratingSystem(Family.all(EnemyComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val enemyComponent = Mapper.ENEMY_MAPPER.get(entity)
        val dirComponent = Mapper.DIR_MAPPER.get(entity)
        val position = Mapper.POS_MAPPER.get(entity)
        val velocity = Mapper.VEL_MAPPER.get(entity)
        val speed = Mapper.SPEED_MAPPER.get(entity)

        when (enemyComponent.movementType) {
            EnemyMovementType.BackAndForth -> {
                val gravity = Mapper.GRAVITY_MAPPER.get(entity)
                val bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity)

                if (gravity.onGround) {
                    if (velocity.dx == 0f) velocity.dx = if (dirComponent.facingRight) speed.speed else -speed.speed
                    if (position.x < gravity.platform.x) {
                        position.x = gravity.platform.x
                        velocity.dx = speed.speed
                    }
                    else if (position.x > gravity.platform.x + gravity.platform.width - bounds.rect.width) {
                        position.x = gravity.platform.x + gravity.platform.width - bounds.rect.width
                        velocity.dx = -speed.speed
                    }
                }
            }
        }
    }

}