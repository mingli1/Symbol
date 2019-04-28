package com.symbol.game.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.symbol.game.util.Orientation

class MirrorComponent : Component, Pool.Poolable {

    var orientation = Orientation.Vertical

    override fun reset() {
        orientation = Orientation.Vertical
    }

}