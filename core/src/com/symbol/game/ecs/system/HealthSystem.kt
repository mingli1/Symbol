package com.symbol.game.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.HealthComponent
import com.symbol.game.ecs.entity.Player

class HealthSystem : IteratingSystem(Family.all(HealthComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        Mapper.HEALTH_MAPPER[entity].run {
            if (hp > maxHp) hp = maxHp
            if (hp <= 0) {
                Mapper.REMOVE_MAPPER[entity]?.shouldRemove = true
                if (entity is Player) Mapper.PLAYER_MAPPER[entity].dead = true
            }
        }
    }

}