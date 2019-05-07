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
        val texture = Mapper.TEXTURE_MAPPER[entity]
        val position = Mapper.POS_MAPPER[entity]
        val dir = Mapper.DIR_MAPPER[entity]
        val gravity = Mapper.GRAVITY_MAPPER[entity]

        if (texture.texture == null) return

        val enemy = Mapper.ENEMY_MAPPER[entity]
        if (enemy != null && !enemy.visible) return

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
        val proj = Mapper.PROJ_MAPPER[entity]
        if (proj != null && !proj.sub) {
            val texture = Mapper.TEXTURE_MAPPER[entity]
            val velocity = Mapper.VEL_MAPPER[entity]

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