package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool

data class TextureComponent(var texture: TextureRegion?) : Component, Pool.Poolable {
    override fun reset() {
        texture = null
    }
}