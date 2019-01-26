package com.symbol.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.symbol.util.Resources

class Symbol : Game() {

    lateinit var batch: Batch private set
    lateinit var res: Resources private set

    override fun create() {
        batch = SpriteBatch()
        res = Resources()
    }

    override fun render() {
        super.render()
        Gdx.graphics.setTitle(Config.TITLE + " | ${Gdx.graphics.framesPerSecond} fps")
    }

    override fun dispose() {
        batch.dispose()
        res.dispose()
    }

}
