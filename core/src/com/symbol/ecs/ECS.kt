package com.symbol.ecs

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.utils.Disposable
import com.symbol.util.Resources

class ECS(res: Resources) : Disposable {

    private val engine: Engine = PooledEngine()

    init {

    }

    fun update(dt: Float) {
        engine.update(dt)
    }

    fun addSystem(system: EntitySystem) {
        engine.addSystem(system)
    }

    fun <T : EntitySystem> getSystem(system: Class<T>): T {
        return engine.getSystem(system)
    }

    fun <T : EntitySystem> toggleProcessing(system: Class<T>, toggle: Boolean) {
        engine.getSystem(system).setProcessing(toggle)
    }

    override fun dispose() {

    }

}