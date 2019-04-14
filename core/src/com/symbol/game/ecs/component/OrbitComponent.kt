package com.symbol.game.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class OrbitComponent : Component, Pool.Poolable {

    var clockwise = true

    var angle = 0f
    var speed = 0f
    var radius = 0f

    override fun reset() {
        clockwise = true
        angle = 0f
        speed = 0f
        radius = 0f
    }

}