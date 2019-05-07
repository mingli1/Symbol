package com.symbol.game.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.RemoveComponent

class RemoveSystem : IteratingSystem(Family.all(RemoveComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        Mapper.REMOVE_MAPPER[entity].run {
            if (shouldRemove) engine.removeEntity(entity)
        }
    }

}