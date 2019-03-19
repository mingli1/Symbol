package com.symbol.game.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.symbol.game.Config
import com.symbol.game.Symbol

object DesktopLauncher {

    @JvmStatic
    fun main(args: Array<String>) {
        val config = LwjglApplicationConfiguration()

        config.title = Config.TITLE
        config.width = Config.S_WIDTH
        config.height = Config.S_HEIGHT
        config.backgroundFPS = Config.BG_FPS
        config.foregroundFPS = Config.FG_FPS
        config.vSyncEnabled = Config.V_SYNC
        config.resizable = Config.RESIZABLE

        LwjglApplication(Symbol(), config)
    }

}
