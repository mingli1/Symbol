package com.symbol.game.scene;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.symbol.game.Symbol;
import com.symbol.game.util.Data;
import com.symbol.game.util.Resources;

public abstract class Scene implements Disposable {

    protected final Resources res;
    protected final Data data;
    protected final Batch batch;

    protected Stage stage;
    protected Viewport viewport;
    protected Symbol context;

    public Scene(final Symbol context, Stage stage, Viewport viewport) {
        res = context.getRes();
        data = context.getData();
        batch = context.getBatch();

        this.context = context;
        this.stage = stage;
        this.viewport = viewport;
    }

    public abstract void update(float dt);

    public abstract void render(float dt);

    @Override
    public abstract void dispose();

}
