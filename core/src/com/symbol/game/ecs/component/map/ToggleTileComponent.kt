package com.symbol.game.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Pool

class ToggleTileComponent : Component, Pool.Poolable {

    var id = 0
    var toggle = true
    var lethalRect = Rectangle()

    override fun reset() {
        id = 0
        toggle = true
        lethalRect.set(0f, 0f, 0f, 0f)
    }

}
