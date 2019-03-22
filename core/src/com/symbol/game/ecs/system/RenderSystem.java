package com.symbol.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.symbol.game.ecs.Mapper;
import com.symbol.game.ecs.component.DirectionComponent;
import com.symbol.game.ecs.component.GravityComponent;
import com.symbol.game.ecs.component.PositionComponent;
import com.symbol.game.ecs.component.TextureComponent;

public class RenderSystem extends IteratingSystem {

    private Batch batch;

    public RenderSystem(Batch batch) {
        super(Family.all(TextureComponent.class).get());
        this.batch = batch;
    }

    @Override
    public void processEntity(Entity entity, float dt) {
        TextureComponent texture = Mapper.TEXTURE_MAPPER.get(entity);
        PositionComponent position = Mapper.POS_MAPPER.get(entity);
        DirectionComponent dir = Mapper.DIR_MAPPER.get(entity);
        GravityComponent gravity = Mapper.GRAVITY_MAPPER.get(entity);

        if (texture.texture == null) return;

        float width = texture.texture.getRegionWidth();
        float height = texture.texture.getRegionHeight();

        float xOffset = 0f;
        float yOffset = 0f;
        float fWidth = width;
        float fHeight = height;

        if (dir != null) {
            if (!dir.facingRight) {
                xOffset = width;
                fWidth = -width;
            }
            if ((dir.yFlip && !dir.facingUp) || (gravity != null && gravity.reverse)) {
                yOffset = height;
                fHeight = -height;
            }
        }

        batch.draw(texture.texture, position.x + xOffset, position.y + yOffset, fWidth, fHeight);
    }

}