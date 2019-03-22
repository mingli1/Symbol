package com.symbol.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class ColorComponent implements Component, Pool.Poolable {

    public String hex = null;

    @Override
    public void reset() {
        hex = null;
    }

}