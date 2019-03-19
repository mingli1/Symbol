package com.symbol.ecs.component.enemy;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class AttackComponent implements Component, Pool.Poolable {

    public int damage = 0;
    public float attackRate = 0f;
    public boolean canAttack = true;
    public String attackTexture = null;
    public float  projectileSpeed = 0f;
    public float projectileAcceleration = 0f;
    public boolean projectileDestroyable = false;
    public float attackDetonateTime = 0f;

    @Override
    public void reset() {
        damage = 0;
        attackRate = 0f;
        canAttack = true;
        attackTexture = null;
        projectileSpeed = 0f;
        projectileAcceleration = 0f;
        projectileDestroyable = false;
        attackDetonateTime = 0f;
    }

}