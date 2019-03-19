package com.symbol.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class HealthComponent implements Component, Pool.Poolable {

    public int hp = 0;
    public int maxHp = 0;
    public int hpDelta = 0;
    public boolean hpChange = false;

    public void hit(int damage) {
        hp -= damage;
        hpDelta = damage > maxHp ? maxHp : damage;
        hpChange = true;
    }

    @Override
    public void reset() {
        hp = 0;
        maxHp = 0;
        hpDelta = 0;
        hpChange = false;
    }

}