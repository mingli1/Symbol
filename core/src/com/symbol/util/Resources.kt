package com.symbol.util

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable

class Resources : Disposable {

    private val assetManager: AssetManager = AssetManager()
    private val atlas: TextureAtlas
    private val textures: Map<String, TextureRegion>

    init {
        textures = HashMap()

        assetManager.load("textures/textures.atlas", TextureAtlas::class.java)
        assetManager.finishLoading()

        atlas = assetManager.get("textures/textures.atlas", TextureAtlas::class.java)

        textures["player"] = atlas.findRegion("player")
        textures["e_e"] = atlas.findRegion("e_e")
        textures["e_sqrt"] = atlas.findRegion("e_sqrt")
        textures["e_exists"] = atlas.findRegion("e_exists")
        textures["e_sum"] = atlas.findRegion("e_sum")

        textures["p_dot"] = atlas.findRegion("p_dot")
        textures["p_angle_bracket_t"] = atlas.findRegion("p_angle_bracket_t")
        textures["p_angle_bracket_b"] = atlas.findRegion("p_angle_bracket_b")
        textures["p_angle_bracket"] = atlas.findRegion("p_angle_bracket")
        textures["p_angle_bracket_tr"] = atlas.findRegion("p_angle_bracket_tr")
        textures["p_angle_bracket_br"] = atlas.findRegion("p_angle_bracket_br")
    }

    fun getTexture(key: String): TextureRegion? {
        return textures[key]
    }

    override fun dispose() {
        assetManager.dispose()
        atlas.dispose()
    }

}