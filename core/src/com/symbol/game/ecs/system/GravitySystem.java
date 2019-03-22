package com.symbol.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.symbol.game.ecs.Mapper;
import com.symbol.game.ecs.component.GravityComponent;
import com.symbol.game.ecs.component.VelocityComponent;

public class GravitySystem extends IteratingSystem {

    public static final float GRAVITY = -750.8f;
    public static final float TERMINAL_VELOCITY = -80.8f;

    public GravitySystem() {
        super(Family.all(GravityComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        VelocityComponent vel = Mapper.VEL_MAPPER.get(entity);
        GravityComponent grav = Mapper.GRAVITY_MAPPER.get(entity);

        if (grav.reverse) {
            if (vel.dy < -grav.terminalVelocity) vel.dy -= grav.gravity * deltaTime;
            else vel.dy = -grav.terminalVelocity;
        }
        else {
            if (vel.dy > grav.terminalVelocity) vel.dy += grav.gravity * deltaTime;
            else vel.dy = grav.terminalVelocity;
        }
    }

}