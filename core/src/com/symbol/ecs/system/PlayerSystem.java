package com.symbol.ecs.system;

import com.badlogic.ashley.core.EntitySystem;
import com.symbol.ecs.Mapper;
import com.symbol.ecs.component.PlayerComponent;
import com.symbol.ecs.entity.Player;

public class PlayerSystem extends EntitySystem {

    private Player player;
    private float stateTime = 0f;

    public PlayerSystem(Player player) {
        this.player = player;
    }

    @Override
    public void update(float dt) {
        PlayerComponent playerComp = Mapper.PLAYER_MAPPER.get(player);

        if (!playerComp.canShoot) {
            stateTime += dt;
            if (stateTime >= Player.PLAYER_PROJECTILE_SHOOT_DELAY) {
                playerComp.canShoot = true;
                stateTime = 0f;
            }
        }
    }

}