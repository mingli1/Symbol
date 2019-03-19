package com.symbol.ecs.component.map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

public class ClampComponent implements Component, Pool.Poolable {

    public boolean right = false;
    public Rectangle rect = new Rectangle();
    public float acceleration = 0f;
    public float backVelocity = 0f;
    public boolean clamping = true;

    @Override
    public void reset() {
        right = false;
        rect.set(0f, 0f, 0f, 0f);
        acceleration = 0f;
        backVelocity = 0f;
        clamping = true;
    }

}