package com.symbol.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class JumpComponent implements Component, Pool.Poolable {

    public float impulse = 0f;

    @Override
    public void reset() {
        impulse = 0f;
    }

}