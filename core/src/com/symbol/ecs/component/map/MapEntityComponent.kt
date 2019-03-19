package com.symbol.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.symbol.ecs.entity.MapEntityType

class MapEntityComponent : Component, Pool.Poolable {

    var mapEntityType = MapEntityType.None
    var mapCollidable = false
    var projectileCollidable = false

    override fun reset() {
        mapEntityType = MapEntityType.None
        mapCollidable = false
        projectileCollidable = false
    }
}