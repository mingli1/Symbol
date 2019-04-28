package com.symbol.game.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class AccelerationGateComponent : Component, Pool.Poolable {

    var boost = 0f

    override fun reset() {
        boost = 0f
    }
}