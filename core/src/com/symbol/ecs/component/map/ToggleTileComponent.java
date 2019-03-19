package com.symbol.ecs.component.map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class ToggleTileComponent implements Component, Pool.Poolable {

    public int id = 0;
    public boolean toggle = true;

    @Override
    public void reset() {
        id = 0;
        toggle = true;
    }

}
