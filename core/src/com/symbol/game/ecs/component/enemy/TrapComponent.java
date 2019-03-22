package com.symbol.game.ecs.component.enemy;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class TrapComponent implements Component, Pool.Poolable {

    public boolean countdown = false;
    public int hits = 0;
    public float timer = 0f;

    @Override
    public void reset() {
        countdown = false;
        hits = 0;
        timer = 0f;
    }

}