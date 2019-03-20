package com.symbol.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.symbol.screen.GameScreen;
import com.symbol.util.Resources;

public class Symbol extends Game {

    public Batch batch;
    public Resources res;

    public GameScreen gameScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        res = new Resources();

        gameScreen = new GameScreen(this);

        this.setScreen(gameScreen);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        res.dispose();
    }

}
