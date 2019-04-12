package com.symbol.game.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class AffectAllComponent : Component, Pool.Poolable {
    override fun reset() {}
}