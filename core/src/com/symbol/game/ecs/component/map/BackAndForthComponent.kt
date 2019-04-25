package com.symbol.game.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class BackAndForthComponent : Component, Pool.Poolable {

    var dist = 0f
    var positive = true

    override fun reset() {
        dist = 0f
        positive = true
    }

}