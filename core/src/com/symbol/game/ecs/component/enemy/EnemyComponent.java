package com.symbol.game.ecs.component.enemy;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.symbol.game.ecs.entity.EnemyAttackType;
import com.symbol.game.ecs.entity.EnemyMovementType;

public class EnemyComponent implements Component, Pool.Poolable {

    public EnemyMovementType movementType  = EnemyMovementType.None;
    public EnemyAttackType attackType = EnemyAttackType.None;
    public Entity parent = null;

    @Override
    public void reset() {
        movementType = EnemyMovementType.None;
        attackType = EnemyAttackType.None;
        parent = null;
    }

}