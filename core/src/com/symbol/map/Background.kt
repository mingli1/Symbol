package com.symbol.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.symbol.game.Config

class Background(private val bgTexture: TextureRegion, private val cam: OrthographicCamera, private val scale: Vector2) {

    private var position: Vector2 = Vector2()
    private var velocity: Vector2 = Vector2()

    private var numDrawX: Int = 0
    private var numDrawY: Int = 0

    init {
        numDrawX = Config.V_WIDTH / bgTexture.regionWidth + 1
        numDrawY = Config.V_HEIGHT / bgTexture.regionHeight + 1
        fixBleeding(bgTexture)
    }

    fun update(dt: Float) {
        position.x += (velocity.x * scale.x) * dt
        position.y += (velocity.y * scale.y) * dt
    }

    fun render(batch: Batch) {
        val x = ((position.x + cam.viewportWidth / 2 - cam.position.x) * scale.x) % bgTexture.regionWidth
        val y = ((position.y + cam.viewportHeight / 2 - cam.position.y) * scale.y) % bgTexture.regionHeight

        val colOffset = if (x > 0) -1 else 0
        val rowOffset = if (y > 0) -1 else 0

        for (row in 0..numDrawY) {
            for (col in 0..numDrawX) {
                batch.draw(bgTexture, x + (col + colOffset) * bgTexture.regionWidth,
                        y + (row + rowOffset) * bgTexture.regionHeight)
            }
        }
    }

    private fun fixBleeding(texture: TextureRegion) {
        val fix = 0.01f
        val invWidth = 1f / texture.texture.width
        val invHeight = 1f / texture.texture.height
        texture.setRegion((texture.regionX + fix) * invWidth,
                (texture.regionY + fix) * invHeight,
                (texture.regionX + texture.regionWidth - fix) * invWidth,
                (texture.regionY + texture.regionHeight - fix) * invHeight)
    }

}