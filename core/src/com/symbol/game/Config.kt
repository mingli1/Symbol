package com.symbol.game

object Config {

    const val RATIO = 5 / 3
    const val ASPECT_RATIO = 1 / RATIO

    const val V_WIDTH = 200
    const val V_HEIGHT = V_WIDTH * ASPECT_RATIO
    const val SCALE = 6

    const val S_WIDTH = V_WIDTH * SCALE
    const val S_HEIGHT = V_HEIGHT * SCALE

    const val TITLE = "Symbol"

    const val BG_FPS = 10
    const val FG_FPS = 60

    const val V_SYNC = false

    const val RESIZABLE = false

}