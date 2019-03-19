package com.symbol.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ToggleTileComponent : Component, Pool.Poolable {

    var id: Int = 0
    var toggle: Boolean = true

    override fun reset() {
        id = 0
        toggle = true
    }

}
