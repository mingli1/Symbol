package com.symbol.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.symbol.game.ecs.Mapper;
import com.symbol.game.ecs.component.BoundingBoxComponent;
import com.symbol.game.ecs.component.GravityComponent;
import com.symbol.game.ecs.component.OrbitComponent;
import com.symbol.game.ecs.component.PositionComponent;
import com.symbol.game.ecs.component.TextureComponent;
import com.symbol.game.ecs.component.VelocityComponent;

public class MovementSystem extends IteratingSystem {

    public MovementSystem() {
        super(Family.all(PositionComponent.class, VelocityComponent.class).exclude(GravityComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float dt) {
        PositionComponent position = Mapper.POS_MAPPER.get(entity);
        VelocityComponent velocity = Mapper.VEL_MAPPER.get(entity);
        OrbitComponent orbit = Mapper.ORBIT_MAPPER.get(entity);
        BoundingBoxComponent bounds = Mapper.BOUNDING_BOX_MAPPER.get(entity);
        TextureComponent texture = Mapper.TEXTURE_MAPPER.get(entity);

        if (texture.texture == null) return;

        float width = texture.texture.getRegionWidth();
        float height = texture.texture.getRegionHeight();

        bounds.rect.setPosition(position.x + (width - bounds.rect.width) / 2, position.y + (height - bounds.rect.height) / 2);

        if (orbit != null) {
            orbit.angle += !orbit.clockwise ? orbit.speed * dt : -orbit.speed * dt;
            if (orbit.angle >= MathUtils.PI2) orbit.angle -= MathUtils.PI2;

            position.x = orbit.originX + MathUtils.cos(orbit.angle) * orbit.radius - bounds.rect.width / 2;
            position.y = orbit.originY + MathUtils.sin(orbit.angle) * orbit.radius - bounds.rect.height / 2;
        }
        else {
            position.x += velocity.dx * dt;
            position.y += velocity.dy * dt;
        }
    }

}