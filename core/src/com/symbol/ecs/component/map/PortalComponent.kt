package com.symbol.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PortalComponent : Component, Pool.Poolable {

    var id = 0
    var target = 0
    var teleported = false

    override fun reset() {
        id = 0
        target = 0
        teleported = false
    }

}