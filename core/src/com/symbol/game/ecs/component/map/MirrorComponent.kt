package com.symbol.game.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class MirrorComponent : Component, Pool.Poolable {

    var orientation = Orientation.Vertical

    enum class Orientation(val typeStr: String) {
        Horizontal("h"),
        Vertical("v"),
        LeftDiagonal("ld"),
        RightDiagonal("rd");

        companion object {
            fun getType(typeStr: String) : Orientation? = Orientation.values().find { it.typeStr == typeStr }
        }

    }

    override fun reset() {
        orientation = Orientation.Vertical
    }

}