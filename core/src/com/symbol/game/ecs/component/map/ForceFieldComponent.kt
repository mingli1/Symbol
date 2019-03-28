package com.symbol.game.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ForceFieldComponent : Component, Pool.Poolable {

    var timer: Float = 0f
    var duration: Float = 0f
    var activated: Boolean = true

    override fun reset() {
        timer = 0f
        duration = 0f
        activated = true
    }

}