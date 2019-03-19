package com.symbol.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class OrbitComponent implements Component, Pool.Poolable {

    public boolean clockwise = true;

    public float originX = 0f;
    public float originY = 0f;
    public float angle = 0f;
    public float speed = 0f;
    public float radius = 0f;

    public void setOrigin(float originX, float originY) {
        this.originX = originX;
        this.originY = originY;
    }

    @Override
    public void reset() {
        clockwise = true;
        originX = 0f;
        originY = 0f;
        angle = 0f;
        speed = 0f;
        radius = 0f;
    }

}