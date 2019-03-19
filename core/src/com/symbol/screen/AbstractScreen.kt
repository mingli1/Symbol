package com.symbol.screen

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.symbol.game.Config
import com.symbol.game.Symbol

abstract class AbstractScreen(protected val game: Symbol) : Screen, Disposable {

    protected var stage: Stage private set
    protected var viewport: Viewport private set
    protected var cam = OrthographicCamera()

    init {
        cam.setToOrtho(false, Config.V_WIDTH.toFloat(), Config.V_HEIGHT.toFloat())
        viewport = StretchViewport(Config.V_WIDTH.toFloat(), Config.V_HEIGHT.toFloat(), OrthographicCamera())
        stage = Stage(viewport, game.batch)
    }

    override fun show() {}

    override fun render(dt: Float) {}

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {
        stage.dispose()
    }

}