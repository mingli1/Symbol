package com.symbol.screen

import com.badlogic.gdx.Gdx
import com.symbol.ecs.ECS
import com.symbol.game.Symbol
import com.symbol.input.KeyInput
import com.symbol.input.KeyInputSystem

class GameScreen(game: Symbol) : AbstractScreen(game) {

    private val ecs = ECS(game.res)
    private val input: KeyInput

    init {
        val keyInputSystem = KeyInputSystem()
        input = KeyInput(keyInputSystem)
        ecs.engine.addSystem(keyInputSystem)
    }

    override fun show() {
        Gdx.input.inputProcessor = input
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