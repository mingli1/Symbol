package com.symbol.ecs

import com.badlogic.ashley.core.PooledEngine
import com.symbol.util.Resources

class ECS(res: Resources) {

    val engine: PooledEngine = PooledEngine()

    init {

    }

    fun update(dt: Float) {
        engine.update(dt)
    }

}