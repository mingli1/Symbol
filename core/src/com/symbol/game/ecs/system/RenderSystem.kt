package com.symbol.game.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.component.TextureComponent
import com.symbol.game.map.camera.CameraUtil
import com.symbol.game.util.Resources
import com.symbol.game.util.TOP
import com.symbol.game.util.TOP_RIGHT

class RenderSystem(private val batch: Batch, private val cam: OrthographicCamera, private val res: Resources) :
        IteratingSystem(Family.all(TextureComponent::class.java).get()) {

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

        applyProjectileFlip(entity)

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

        if (CameraUtil.withinCamera(position.x + xOffset, position.y + yOffset, cam)) {
            batch.draw(texture.texture, position.x + xOffset, position.y + yOffset, fWidth, fHeight)
        }
    }

    private fun applyProjectileFlip(entity: Entity?) {
        if (Mapper.PROJ_MAPPER.get(entity) != null) {
            val texture = Mapper.TEXTURE_MAPPER.get(entity)
            val velocity = Mapper.VEL_MAPPER.get(entity)

            val defaultTexture = res.getTexture(texture.textureStr!!)

            if (velocity.dx != 0f && velocity.dy == 0f)
                texture.texture = defaultTexture
            else if (velocity.dx == 0f && velocity.dy != 0f)
                texture.texture = res.getTexture(texture.textureStr + TOP) ?: defaultTexture
            else if (velocity.dx != 0f && velocity.dy != 0f)
                texture.texture = res.getTexture(texture.textureStr + TOP_RIGHT) ?: defaultTexture
        }
    }

}