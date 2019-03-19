package com.symbol.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class KnockbackComponent implements Component, Pool.Poolable {

    public boolean knockingBack = false;

    @Override
    public void reset() {
        knockingBack = false;
    }

}