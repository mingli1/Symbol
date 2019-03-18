package com.symbol.ecs.component.enemy

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class TeleportComponent : Component, Pool.Poolable {
    override fun reset() {}
}