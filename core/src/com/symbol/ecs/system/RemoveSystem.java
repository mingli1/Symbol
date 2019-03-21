package com.symbol.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.symbol.ecs.Mapper;
import com.symbol.ecs.component.RemoveComponent;

public class RemoveSystem extends IteratingSystem {

    public RemoveSystem() {
        super(Family.all(RemoveComponent.class).get());
    }

    public void processEntity(Entity entity, float dt) {
        RemoveComponent rem = Mapper.REMOVE_MAPPER.get(entity);

        if (rem.shouldRemove) {
            getEngine().removeEntity(entity);
        }
    }

}