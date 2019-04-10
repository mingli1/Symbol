package com.symbol.game.map.camera

import com.badlogic.gdx.graphics.OrthographicCamera
import com.symbol.game.map.TILE_SIZE

object CameraUtil {

    private const val X_HALF = 13
    private const val Y_HALF = 9

    fun withinCamera(x: Float, y: Float, cam: OrthographicCamera) : Boolean {
        val xOffset = TILE_SIZE * X_HALF
        val yOffset = TILE_SIZE * Y_HALF
        return x >= cam.position.x - xOffset - TILE_SIZE &&
                x <= cam.position.x + xOffset &&
                y >= cam.position.y - yOffset &&
                y <= cam.position.y + yOffset
    }

}