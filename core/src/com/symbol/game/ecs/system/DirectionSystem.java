package com.symbol.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.symbol.game.ecs.Mapper;
import com.symbol.game.ecs.component.DirectionComponent;
import com.symbol.game.ecs.component.KnockbackComponent;
import com.symbol.game.ecs.component.VelocityComponent;

public class DirectionSystem extends IteratingSystem {

    public DirectionSystem() {
        super(Family.all(DirectionComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        VelocityComponent vel = Mapper.VEL_MAPPER.get(entity);
        DirectionComponent dir = Mapper.DIR_MAPPER.get(entity);

        KnockbackComponent knockback = Mapper.KNOCKBACK_MAPPER.get(entity);
        boolean knockingBack = knockback != null && knockback.knockingBack;

        if (vel.dx > 0 && !knockingBack) dir.facingRight = true;
        else if (vel.dx < 0 && !knockingBack) dir.facingRight = false;

        if (vel.dy > 0) dir.facingUp = true;
        else if (vel.dy < 0) dir.facingUp = false;
    }

}