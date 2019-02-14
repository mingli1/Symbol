package com.symbol.util

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable

const val TOP = "_t"
const val BOTTOM = "_b"
const val TOP_RIGHT ="_tr"
const val BOTTOM_RIGHT = "_br"
const val VERTICAL = "_v"

class Resources : Disposable {

    private val assetManager: AssetManager = AssetManager()
    private val atlas: TextureAtlas
    private val textures: MutableMap<String, TextureRegion> = HashMap()

    init {
        assetManager.load("textures/textures.atlas", TextureAtlas::class.java)
        assetManager.finishLoading()

        atlas = assetManager.get("textures/textures.atlas", TextureAtlas::class.java)

        textures["background"] = atlas.findRegion("background")

        textures["player"] = atlas.findRegion("player")
        textures["e_e"] = atlas.findRegion("e_e")
        textures["e_sqrt"] = atlas.findRegion("e_sqrt")
        textures["e_exists"] = atlas.findRegion("e_exists")
        textures["e_sum"] = atlas.findRegion("e_sum")
        textures["e_big_pi"] = atlas.findRegion("e_big_pi")

        loadProjectile("p_dot")
        loadProjectile("p_angle_bracket")
        loadProjectile("p_xor")
        loadProjectile("p_arrow")
        loadProjectile("p_cup")
        loadProjectile("p_implies")
        loadProjectile("p_ldots")
        loadProjectile("p_large_triangle")
    }

    fun getTexture(key: String): TextureRegion? {
        return textures[key]
    }

    private fun loadProjectile(key: String) {
        textures[key] = atlas.findRegion(key)

        val vertical = atlas.findRegion(key + VERTICAL)
        val topRight = atlas.findRegion(key + TOP_RIGHT)
        val botRight = atlas.findRegion(key + BOTTOM_RIGHT)

        if (vertical != null) {
            textures[key + TOP] = vertical
            textures[key + BOTTOM] = vertical
        }
        else {
            val top = atlas.findRegion(key + TOP)
            val bot = atlas.findRegion(key + BOTTOM)

            if (top != null) textures[key + TOP] = top
            if (bot != null) textures[key + BOTTOM] = bot
        }
        if (topRight != null) textures[key + TOP_RIGHT] = topRight
        if (botRight != null) textures[key + BOTTOM_RIGHT] = botRight
    }

    override fun dispose() {
        assetManager.dispose()
        atlas.dispose()
    }

}