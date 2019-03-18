package com.symbol.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import com.symbol.ecs.Mapper
import com.symbol.ecs.component.TextureComponent

class RenderSystem(private val batch: Batch) : IteratingSystem(Family.all(TextureComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, dt: Float) {
        val texture = Mapper.TEXTURE_MAPPER.get(entity)
        val position = Mapper.POS_MAPPER.get(entity)
        val dir = Mapper.DIR_MAPPER.get(entity)
        val gravity = Mapper.GRAVITY_MAPPER.get(entity)

        if (texture.texture == null) return

        val width = texture.texture!!.regionWidth.toFloat()
        val height = texture.texture!!.regionHeight.toFloat()

        var xOffset = 0f
        var yOffset = 0f
        var fWidth = width
        var fHeight = height

        if (dir != null) {
            if (!dir.facingRight) {
                xOffset = width
                fWidth = -width
            }
            if ((dir.yFlip && !dir.facingUp) || (gravity != null && gravity.reverse)) {
                yOffset = height
                fHeight = -height
            }
        }

        batch.draw(texture.texture, position.x + xOffset, position.y + yOffset, fWidth, fHeight)
    }

}