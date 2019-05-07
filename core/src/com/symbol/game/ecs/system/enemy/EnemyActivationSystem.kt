package com.symbol.game.ecs.system.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.enemy.EnemyComponent
import com.symbol.game.ecs.entity.Player

class EnemyActivationSystem(private val player: Player) : IteratingSystem(Family.all(EnemyComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val activation = Mapper.ACTIVATION_MAPPER[entity]

        if (activation.activationRange != -1f) {
            val playerBounds = Mapper.BOUNDING_BOX_MAPPER[player]
            val enemyBounds = Mapper.BOUNDING_BOX_MAPPER[entity]
            val x1 = playerBounds.rect.x + playerBounds.rect.width / 2
            val x2 = enemyBounds.rect.x + enemyBounds.rect.width / 2
            val y1 = playerBounds.rect.y + playerBounds.rect.height / 2
            val y2 = enemyBounds.rect.y + enemyBounds.rect.height / 2
            val sqdist = ((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2))

            if (sqdist <= activation.activationRange * activation.activationRange) {
                activation.active = true
            }
        } else activation.active = true
    }

}