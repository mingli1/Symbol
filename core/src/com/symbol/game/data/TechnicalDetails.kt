package com.symbol.game.data

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array

data class TechnicalDetails(
        val id: String? = null,
        val title: String? = null,
        val imageSize: Int = 0,
        val images: Array<ImageWrapper> = Array(),
        val texts: Array<String> = Array()
)

data class ImageWrapper(
        val image: TextureRegion? = null,
        val alignment: ImageAlign = ImageAlign.Left
)

enum class ImageAlign {
    Top, Left, Right
}