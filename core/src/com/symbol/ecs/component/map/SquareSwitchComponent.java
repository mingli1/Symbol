package com.symbol.ecs.component.map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class SquareSwitchComponent implements Component, Pool.Poolable {

    public int targetId = 0;
    public boolean toggle = true;

    @Override
    public void reset() {
        targetId = 0;
        toggle = true;
    }

}