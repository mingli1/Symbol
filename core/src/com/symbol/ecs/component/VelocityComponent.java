package com.symbol.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class VelocityComponent implements Component, Pool.Poolable {

    public float dx = 0f;
    public float dy = 0f;

    public float speed = 0f;

    public float platformDx = 0f;

    public void set(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void reset() {
        dx = 0f;
        dy = 0f;
        speed = 0f;
        platformDx = 0f;
    }

    public void move(boolean right) {
        if (right) dx = speed;
        else dx = -speed;
    }

}