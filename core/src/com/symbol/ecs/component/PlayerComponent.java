package com.symbol.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class PlayerComponent implements Component, Pool.Poolable {

    public boolean canJump = false;
    public boolean canDoubleJump = false;
    public boolean canShoot = true;
    public boolean hasJumpBoost = false;

    @Override
    public void reset() {
        canJump = false;
        canDoubleJump = false;
        canShoot = true;
        hasJumpBoost = false;
    }

}