package com.symbol.game.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ForceFieldComponent : Component, Pool.Poolable {

    var timer = 0f
    var duration = 0f
    var activated = true

    override fun reset() {
        timer = 0f
        duration = 0f
        activated = true
    }

}