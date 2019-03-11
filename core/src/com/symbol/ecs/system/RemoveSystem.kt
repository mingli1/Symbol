package com.symbol.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.RemoveComponent

class RemoveSystem : IteratingSystem(Family.all(RemoveComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val rem = Mapper.REMOVE_MAPPER.get(entity)

        if (rem.shouldRemove) {
            engine.removeEntity(entity)
        }
    }

}