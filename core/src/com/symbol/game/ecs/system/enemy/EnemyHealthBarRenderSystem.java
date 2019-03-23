package com.symbol.game.ecs.system.enemy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.symbol.game.ecs.Mapper;
import com.symbol.game.ecs.component.HealthComponent;
import com.symbol.game.ecs.component.PositionComponent;
import com.symbol.game.ecs.component.TextureComponent;
import com.symbol.game.ecs.component.enemy.EnemyComponent;
import com.symbol.game.util.Resources;

import java.util.HashMap;
import java.util.Map;

public class EnemyHealthBarRenderSystem extends IteratingSystem {

    private static final float VISIBLE_DURATION = 2f;
    private static final int HEALTH_BAR_HEIGHT = 1;
    private static final int X_OFFSET = 2;
    private static final int Y_OFFSET = 3;

    private Batch batch;
    private Resources res;

    private Map<Entity, Float> timers;
    private Map<Entity, Boolean> startHealthBars;

    public EnemyHealthBarRenderSystem(Batch batch, Resources res) {
        super(Family.all(EnemyComponent.class).get());
        this.batch = batch;
        this.res = res;

        timers = new HashMap<Entity, Float>();
        startHealthBars = new HashMap<Entity, Boolean>();
    }

    public void reset() {
        timers.clear();
        startHealthBars.clear();
        for (Entity entity : getEntities()) {
            timers.put(entity, 0f);
            startHealthBars.put(entity, false);
        }
    }

    @Override
    protected void processEntity(Entity entity, float dt) {
        PositionComponent pos = Mapper.INSTANCE.getPOS_MAPPER().get(entity);
        TextureComponent texture = Mapper.INSTANCE.getTEXTURE_MAPPER().get(entity);
        float width = texture.getTexture().getRegionWidth();
        float height = texture.getTexture().getRegionHeight();

        HealthComponent health = Mapper.INSTANCE.getHEALTH_MAPPER().get(entity);
        if (health.getHpChange() && health.getHp() != 0) {
            startHealthBars.put(entity, true);
            timers.put(entity, 0f);
            health.setHpChange(false);
        }

        if (startHealthBars.get(entity)) {
            timers.put(entity, timers.get(entity) + dt);

            float maxHpBarWidth = width + (X_OFFSET * 2) - 2;
            float hpBarWidth = maxHpBarWidth * ((float) health.getHp() / health.getMaxHp());

            batch.draw(res.getTexture("black"), pos.getX() - X_OFFSET, pos.getY() + height + Y_OFFSET,
                    width + (X_OFFSET * 2), HEALTH_BAR_HEIGHT + 2);
            batch.draw(res.getTexture("hp_bar_bg_color"), pos.getX() - X_OFFSET + 1, pos.getY() + height + Y_OFFSET + 1,
                    maxHpBarWidth, HEALTH_BAR_HEIGHT);
            batch.draw(res.getTexture("hp_bar_color"), pos.getX() - X_OFFSET + 1, pos.getY() + height + Y_OFFSET + 1,
                    hpBarWidth, HEALTH_BAR_HEIGHT);

            if (timers.get(entity) >= VISIBLE_DURATION) {
                startHealthBars.put(entity, false);
                timers.put(entity, 0f);
            }
        }
    }

}
