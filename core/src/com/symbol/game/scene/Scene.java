package com.symbol.game.scene;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.symbol.game.Symbol;

public abstract class Scene implements Disposable {

    protected Stage stage;
    protected Viewport viewport;
    protected Symbol game;

    public Scene(final Symbol game, Stage stage, Viewport viewport) {
        this.game = game;
        this.stage = stage;
        this.viewport = viewport;
    }

    public abstract void update(float dt);

    public abstract void render(float dt);

    @Override
    public abstract void dispose();

}
