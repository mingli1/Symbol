package com.symbol.game.ecs.component.map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class PortalComponent implements Component, Pool.Poolable {

    public int id = 0;
    public int target = 0;
    public boolean teleported = false;

    @Override
    public void reset() {
        id = 0;
        target = 0;
        teleported = false;
    }

}