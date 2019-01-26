package com.symbol.screen

import com.symbol.ecs.ECS
import com.symbol.game.Symbol

class GameScreen(game: Symbol) : AbstractScreen(game) {

    private val ecs = ECS(game.res)

    override fun show() {

    }

    fun update(dt: Float) {

    }

    override fun render(dt: Float) {
        update(dt)
    }

    override fun dispose() {
        super.dispose()
    }

}