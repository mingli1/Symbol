package com.symbol.util

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable

class Resources : Disposable {

    private val assetManager: AssetManager = AssetManager()

    private val atlas: TextureAtlas

    private val single: Map<String, TextureRegion> = HashMap()
    private val multiple: Map<String, Array<TextureRegion>> = HashMap()
    private val sheet: Map<String, Array<Array<TextureRegion>>> = HashMap()

    init {
        assetManager.load("textures/textures.atlas", TextureAtlas::class.java)
        assetManager.finishLoading()

        atlas = assetManager.get("textures/textures.atlas", TextureAtlas::class.java)
    }

    fun getSingleTexture(key: String): TextureRegion? {
        return single[key]
    }

    fun getMultipleTextures(key: String): Array<TextureRegion>? {
        return multiple[key]
    }

    fun getTextureSheet(key: String): Array<Array<TextureRegion>>? {
        return sheet[key]
    }

    override fun dispose() {
        assetManager.dispose()
        atlas.dispose()
    }

}