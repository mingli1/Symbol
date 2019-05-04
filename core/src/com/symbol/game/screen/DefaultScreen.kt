package com.symbol.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.symbol.game.Symbol
import com.symbol.game.input.MultiTouchDisabler

const val BACKGROUND_VELOCITY = -40f
const val BACKGROUND_SCALE = 0.4f

open class DefaultScreen(game: Symbol) : AbstractScreen(game) {

    private val multiplexer = InputMultiplexer()

    init {
        multiplexer.addProcessor(MultiTouchDisabler())
        multiplexer.addProcessor(stage)
    }

    override fun show() {
        Gdx.input.inputProcessor = multiplexer
    }

    open fun update(dt: Float) {
        game.background.update(dt)
    }

    override fun render(dt: Float) {
        update(dt)

        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = cam.combined
        game.batch.begin()
        game.batch.setColor(1f, 1f, 1f, 1f)

        game.background.render(game.batch)

        game.batch.end()

        stage.act()
        stage.draw()
    }

}