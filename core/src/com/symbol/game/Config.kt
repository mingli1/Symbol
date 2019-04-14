package com.symbol.game

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx

object Config {

    private const val DEBUG = true

    const val V_WIDTH = 200
    const val V_HEIGHT = V_WIDTH * 3 / 5
    private const val SCALE = 4

    const val S_WIDTH = V_WIDTH * SCALE
    const val S_HEIGHT = V_HEIGHT * SCALE

    const val TITLE = "Symbol"

    const val BG_FPS = 10
    const val FG_FPS = 60
    const val DELTA_TIME_BOUND = 1 / 30f

    const val V_SYNC = false

    const val RESIZABLE = false

    fun isDebug() : Boolean = DEBUG

    fun onAndroid() : Boolean = Gdx.app.type == Application.ApplicationType.Android

}