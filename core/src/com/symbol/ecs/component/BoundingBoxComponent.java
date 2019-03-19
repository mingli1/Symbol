package com.symbol.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

public class BoundingBoxComponent implements Component, Pool.Poolable {

    public Rectangle rect = new Rectangle(0f, 0f, 0f, 0f);

    @Override
    public void reset() {
        rect.set(0f, 0f, 0f, 0f);
    }
}