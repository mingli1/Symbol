package com.symbol.scene;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.symbol.ecs.Mapper;
import com.symbol.ecs.component.HealthComponent;
import com.symbol.game.Symbol;

public class Hud extends Scene {

    private static final String HP_PROMPT = "HP";
    private static final Vector2 HP_PROMPT_POSITION = new Vector2(5, 108);

    private static final Vector2 HP_BAR_POSITION = new Vector2(16, 108.3f);
    private static final float HP_BAR_WIDTH = 44f;
    private static final float HP_BAR_HEIGHT = 4;

    private Entity player;

    private float hpBarWidth;

    public Hud(final Symbol game, Entity player) {
        super(game);
        this.player = player;

        createHealthBar();
    }

    private void createHealthBar() {
        Label.LabelStyle labelStyle = new Label.LabelStyle(game.getRes().getFont(), Color.BLACK);
        Label hpPromptLabel = new Label(HP_PROMPT, labelStyle);
        hpPromptLabel.setPosition(HP_PROMPT_POSITION.x, HP_PROMPT_POSITION.y);

        stage.addActor(hpPromptLabel);
    }

    @Override
    public void update(float dt) {
        HealthComponent health = Mapper.INSTANCE.getHEALTH_MAPPER().get(player);
        hpBarWidth = HP_BAR_WIDTH * ((float) health.getHp() / health.getMaxHp());
    }

    @Override
    public void render(float dt) {
        game.getBatch().setProjectionMatrix(stage.getCamera().combined);
        game.getBatch().begin();

        game.getBatch().draw(game.getRes().getTexture("black"), HP_BAR_POSITION.x, HP_BAR_POSITION.y,
                HP_BAR_WIDTH + 2, HP_BAR_HEIGHT + 2);
        game.getBatch().draw(game.getRes().getTexture("hp_bar_bg_color"), HP_BAR_POSITION.x + 1, HP_BAR_POSITION.y + 1,
                HP_BAR_WIDTH, HP_BAR_HEIGHT);
        game.getBatch().draw(game.getRes().getTexture("hp_bar_green"), HP_BAR_POSITION.x + 1, HP_BAR_POSITION.y + 1,
                hpBarWidth, HP_BAR_HEIGHT);

        game.getBatch().end();

        stage.act(dt);
        stage.draw();
    }

}
