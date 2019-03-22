package com.symbol.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.symbol.game.ecs.system.GravitySystem;

public class GravityComponent implements Component, Pool.Poolable {

    public boolean onGround = false;
    public boolean onMovingPlatform = false;
    public Rectangle platform = new Rectangle();
    public boolean collidable = true;

    public float gravity = GravitySystem.GRAVITY;
    public float terminalVelocity = GravitySystem.TERMINAL_VELOCITY;

    public boolean reverse = false;

    @Override
    public void reset() {
        onGround = false;
        onMovingPlatform = false;
        collidable = true;
        platform.set(0f, 0f, 0f, 0f);
        gravity = GravitySystem.GRAVITY;
        terminalVelocity = GravitySystem.TERMINAL_VELOCITY;
        reverse = false;
    }
}