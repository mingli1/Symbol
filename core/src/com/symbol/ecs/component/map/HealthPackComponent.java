package com.symbol.ecs.component.map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class HealthPackComponent implements Component, Pool.Poolable {

    public int regen = 0;

    @Override
    public void reset() {
        regen = 0;
    }

}