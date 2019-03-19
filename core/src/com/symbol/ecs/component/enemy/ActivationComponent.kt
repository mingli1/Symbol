package com.symbol.ecs.component.enemy

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ActivationComponent : Component, Pool.Poolable {

    var activationRange = -1f
    var active: Boolean = false

    override fun reset() {
        activationRange = -1f
        active = false
    }

}