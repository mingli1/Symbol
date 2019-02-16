package com.symbol.util

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable

const val TOP = "_t"
const val TOP_RIGHT ="_tr"

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
        textures["e_in"] = atlas.findRegion("e_in")
        textures["e_theta"] = atlas.findRegion("e_theta")
        textures["e_big_omega"] = atlas.findRegion("e_big_omega")

        loadProjectile("p_dot")
        loadProjectile("p_dot_xor")
        loadProjectile("p_angle_bracket")
        loadProjectile("p_xor")
        loadProjectile("p_arrow")
        loadProjectile("p_cup")
        loadProjectile("p_implies")
        loadProjectile("p_ldots")
        loadProjectile("p_large_triangle")
        loadProjectile("p_big_ll")
    }

    fun getTexture(key: String): TextureRegion? {
        return textures[key]
    }

    fun getSubProjectileTextureFor(key: String) : TextureRegion? {
        if (key == "p_xor") return getTexture("p_dot_xor")
        return null
    }

    private fun loadProjectile(key: String) {
        textures[key] = atlas.findRegion(key)

        val top = atlas.findRegion(key + TOP)
        val topRight = atlas.findRegion(key + TOP_RIGHT)

        if (top != null) textures[key + TOP] = top
        if (topRight != null) textures[key + TOP_RIGHT] = topRight
    }

    override fun dispose() {
        assetManager.dispose()
        atlas.dispose()
    }

}