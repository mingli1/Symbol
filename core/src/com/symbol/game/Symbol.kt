package com.symbol.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.symbol.game.screen.AbstractScreen
import com.symbol.game.screen.GameScreen
import com.symbol.game.util.Resources

class Symbol : Game() {

    lateinit var batch: Batch private set
    lateinit var res: Resources private set

    private var currentScreen: AbstractScreen? = null

    lateinit var gameScreen: GameScreen

    override fun create() {
        batch = SpriteBatch()
        res = Resources()

        gameScreen = GameScreen(this)

        this.setScreen(gameScreen)
    }

    private fun setScreen(screen: AbstractScreen?) {
        super.setScreen(screen)
        currentScreen = screen
    }

    override fun pause() {
        currentScreen?.notifyGameState(AbstractScreen.GameState.Pause)
    }

    override fun resume() {
        currentScreen?.notifyGameState(AbstractScreen.GameState.Resume)
    }

    override fun render() {
        screen?.render(Math.min(Config.DELTA_TIME_BOUND, Gdx.graphics.deltaTime))
    }

    override fun dispose() {
        batch.dispose()
        res.dispose()
        gameScreen.dispose()
    }

}
