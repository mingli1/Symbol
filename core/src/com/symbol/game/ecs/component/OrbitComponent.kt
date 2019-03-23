package com.symbol.game.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class OrbitComponent : Component, Pool.Poolable {

    var clockwise = true

    var originX = 0f
    var originY = 0f
    var angle = 0f
    var speed = 0f
    var radius = 0f

    fun setOrigin(originX: Float, originY: Float) {
        this.originX = originX
        this.originY = originY
    }

    override fun reset() {
        clockwise = true
        originX = 0f
        originY = 0f
        angle = 0f
        speed = 0f
        radius = 0f
    }

}