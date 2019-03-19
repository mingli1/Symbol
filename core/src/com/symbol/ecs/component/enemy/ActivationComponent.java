package com.symbol.ecs.component.enemy;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class ActivationComponent implements Component, Pool.Poolable {

    public float activationRange = -1f;
    public boolean active = false;

    @Override
    public void reset() {
        activationRange = -1f;
        active = false;
    }

}
