package com.symbol.ecs.component.map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class MovingPlatformComponent implements Component, Pool.Poolable {

    public float originX = 0f;
    public float originY = 0f;
    public float distance = 0f;
    public boolean positive = true;

    @Override
    public void reset() {
        originX = 0f;
        originY = 0f;
        distance = 0f;
        positive = true;
    }

}