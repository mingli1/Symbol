package com.symbol.game.ecs.component.map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.symbol.game.ecs.entity.MapEntityType;

public class MapEntityComponent implements Component, Pool.Poolable {

    public MapEntityType mapEntityType = MapEntityType.None;
    public boolean mapCollidable = false;
    public boolean projectileCollidable = false;

    @Override
    public void reset() {
        mapEntityType = MapEntityType.None;
        mapCollidable = false;
        projectileCollidable = false;
    }
}