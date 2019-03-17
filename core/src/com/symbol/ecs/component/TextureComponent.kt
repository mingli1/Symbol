package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool

class TextureComponent : Component, Pool.Poolable {

    var texture: TextureRegion? = null
    var textureStr: String? = null

    override fun reset() {
        texture = null
        textureStr = null
    }
}