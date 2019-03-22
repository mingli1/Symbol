package com.symbol.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.symbol.game.ecs.entity.ProjectileMovementType;
import com.symbol.game.util.Direction;

public class ProjectileComponent implements Component, Pool.Poolable {

    public float lifeTime = 0f;

    public boolean parentFacingRight = false;

    public String textureStr = null;
    public boolean collidesWithTerrain = true;
    public boolean collidesWithProjectiles = false;
    public boolean enemy = false;
    public int damage = 0;
    public float knockback = 0f;

    public float detonateTime = 0f;
    public float acceleration = 0f;

    public ProjectileMovementType movementType = ProjectileMovementType.Normal;

    public boolean arcHalf = false;
    public Direction waveDir = Direction.Left;

    @Override
    public void reset() {
        lifeTime = 0f;
        parentFacingRight = false;
        textureStr = null;
        collidesWithTerrain = true;
        collidesWithProjectiles = false;
        enemy = false;
        damage = 0;
        knockback = 0f;
        detonateTime = 0f;
        acceleration = 0f;
        movementType = ProjectileMovementType.Normal;

        arcHalf = false;
        waveDir = Direction.Left;
    }
}