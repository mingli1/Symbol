package com.symbol.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.symbol.ecs.entity.MapEntityType

class MapEntityComponent : Component, Pool.Poolable {

    var mapEntityType: MapEntityType = MapEntityType.None

    override fun reset() {
        mapEntityType = MapEntityType.None
    }
}