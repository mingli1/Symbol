package com.symbol.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class DirectionComponent implements Component, Pool.Poolable {

    public boolean facingRight = true;
    public boolean facingUp = true;
    public boolean yFlip = false;

    @Override
    public void reset() {
        facingRight = true;
        facingUp = true;
        yFlip = false;
    }

}