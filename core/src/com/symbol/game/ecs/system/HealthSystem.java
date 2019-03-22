package com.symbol.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.symbol.game.ecs.Mapper;
import com.symbol.game.ecs.component.HealthComponent;
import com.symbol.game.ecs.component.RemoveComponent;
import com.symbol.game.ecs.entity.Player;

public class HealthSystem extends IteratingSystem {

    public HealthSystem() {
        super(Family.all(HealthComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        HealthComponent health = Mapper.HEALTH_MAPPER.get(entity);
        RemoveComponent remove = Mapper.REMOVE_MAPPER.get(entity);

        if (health.hp > health.maxHp) health.hp = health.maxHp;

        if (health.hp <= 0) {
            if (!(entity instanceof Player)) remove.shouldRemove = true;
            else health.hp = 0;
        }
    }

}