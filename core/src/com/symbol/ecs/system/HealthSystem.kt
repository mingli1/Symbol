package com.symbol.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.HealthComponent
import com.symbol.ecs.entity.Player

class HealthSystem : IteratingSystem(Family.all(HealthComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val health = Mapper.HEALTH_MAPPER.get(entity)
        val remove = Mapper.REMOVE_MAPPER.get(entity)

        if (health.hp <= 0) {
            if (entity !is Player) remove?.shouldRemove = true
        }
    }

}