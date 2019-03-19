package com.symbol.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class PositionComponent implements Component, Pool.Poolable {

    public float x = 0f;
    public float y = 0f;

    public float prevX = 0f;
    public float prevY = 0f;

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setPrev(float prevX, float prevY) {
        this.prevX = prevX;
        this.prevY = prevY;
    }

    @Override
    public void reset() {
        x = 0f;
        y = 0f;
        prevX = 0f;
        prevY = 0f;
    }
}