package com.symbol.game.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor

class KeyInput(private val handler: KeyInputHandler) : InputProcessor {

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.RIGHT -> handler.move(true)
            Input.Keys.LEFT -> handler.move(false)
            Input.Keys.Z -> handler.jump()
            Input.Keys.X -> handler.shoot()
            Input.Keys.C -> handler.release()
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.RIGHT -> handler.stop(true)
            Input.Keys.LEFT -> handler.stop(false)
        }
        return true
    }

    override fun keyTyped(character: Char): Boolean = true

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = false

    override fun scrolled(amount: Int): Boolean = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

}