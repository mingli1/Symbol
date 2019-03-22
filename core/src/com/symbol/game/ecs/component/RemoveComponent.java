package com.symbol.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class RemoveComponent implements Component, Pool.Poolable {

    public boolean shouldRemove = false;

    @Override
    public void reset() {
        shouldRemove = false;
    }

}