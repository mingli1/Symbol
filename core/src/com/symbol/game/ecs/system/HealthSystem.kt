package com.symbol.game.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.HealthComponent
import com.symbol.game.ecs.entity.Player

class HealthSystem : IteratingSystem(Family.all(HealthComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val health = Mapper.HEALTH_MAPPER.get(entity)
        val remove = Mapper.REMOVE_MAPPER.get(entity)

        if (health.hp > health.maxHp) health.hp = health.maxHp

        if (health.hp <= 0) {
            remove?.shouldRemove = true
            if (entity is Player) Mapper.PLAYER_MAPPER.get(entity).dead = true
        }
    }

}