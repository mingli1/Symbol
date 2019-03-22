package com.symbol.game.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.symbol.game.Config;
import com.symbol.game.Symbol;

public abstract class AbstractScreen implements Screen, Disposable {

    protected final Symbol game;

    protected Stage stage;
    protected Viewport viewport;
    protected OrthographicCamera cam = new OrthographicCamera();

    public AbstractScreen(final Symbol game) {
        this.game = game;

        cam.setToOrtho(false, Config.V_WIDTH, Config.V_HEIGHT);
        viewport = new StretchViewport(Config.V_WIDTH, Config.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);
    }

    @Override
    public void show() {}

    @Override
    public void render(float dt) {}

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }

}