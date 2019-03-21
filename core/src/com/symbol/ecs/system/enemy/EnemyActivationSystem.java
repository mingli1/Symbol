package com.symbol.ecs.system.enemy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.symbol.ecs.Mapper;
import com.symbol.ecs.component.BoundingBoxComponent;
import com.symbol.ecs.component.enemy.ActivationComponent;
import com.symbol.ecs.component.enemy.EnemyComponent;
import com.symbol.ecs.entity.Player;

public class EnemyActivationSystem extends IteratingSystem {

    private Player player;

    public EnemyActivationSystem(Player player) {
        super(Family.all(EnemyComponent.class).get());
        this.player = player;
    }

    @Override
    public void processEntity(Entity entity, float dt) {
        ActivationComponent activation = Mapper.ACTIVATION_MAPPER.get(entity);

        if (activation.activationRange != -1f) {
            BoundingBoxComponent playerBounds = Mapper.BOUNDING_BOX_MAPPER.get(player);
            BoundingBoxComponent enemyBounds = Mapper.BOUNDING_BOX_MAPPER.get(entity);
            float x1 = playerBounds.rect.x + playerBounds.rect.width / 2;
            float x2 = enemyBounds.rect.x + enemyBounds.rect.width / 2;
            float y1 = playerBounds.rect.y + playerBounds.rect.height / 2;
            float y2 = enemyBounds.rect.y + enemyBounds.rect.height / 2;
            float sqdist = ((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2));

            if (sqdist <= activation.activationRange * activation.activationRange) {
                activation.active = true;
            }
        } else activation.active = true;
    }

}