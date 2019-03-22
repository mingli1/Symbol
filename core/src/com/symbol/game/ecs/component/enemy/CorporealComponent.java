package com.symbol.game.ecs.component.enemy;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class CorporealComponent implements Component, Pool.Poolable {

    public boolean corporeal = true;
    public float incorporealTime = 0f;

    @Override
    public void reset() {
        corporeal = true;
        incorporealTime = 0f;
    }

}