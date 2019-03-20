package com.symbol.scene;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.symbol.game.Config;
import com.symbol.game.Symbol;

public abstract class Scene implements Disposable {

    protected Stage stage;
    protected Viewport viewport;
    protected Symbol game;

    public Scene(final Symbol game) {
        this.game = game;

        viewport = new StretchViewport(Config.V_WIDTH, Config.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);
    }

    public abstract void update(float dt);

    public abstract void render(float dt);

    public Stage getStage() {
        return stage;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
