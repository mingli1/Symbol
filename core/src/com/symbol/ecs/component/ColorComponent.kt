package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ColorComponent : Component, Pool.Poolable {

    var hex: String? = null

    override fun reset() {
        hex = null
    }

}