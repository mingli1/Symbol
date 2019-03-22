package com.symbol.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

public class TextureComponent implements Component, Pool.Poolable {

    public TextureRegion texture = null;
    public String textureStr = null;

    @Override
    public void reset() {
        texture = null;
        textureStr = null;
    }
}