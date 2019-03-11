package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class KnockbackComponent : Component, Pool.Poolable {

    var knockingBack: Boolean = false

    override fun reset() {
        knockingBack = false
    }

}