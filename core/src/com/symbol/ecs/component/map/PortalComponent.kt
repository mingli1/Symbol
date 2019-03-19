package com.symbol.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PortalComponent : Component, Pool.Poolable {

    var id: Int = 0
    var target: Int = 0
    var teleported: Boolean = false

    override fun reset() {
        id = 0
        target = 0
        teleported = false
    }

}