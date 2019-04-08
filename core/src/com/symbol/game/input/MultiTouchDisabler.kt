package com.symbol.game.input

import com.badlogic.gdx.InputProcessor

class MultiTouchDisabler : InputProcessor {

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = pointer > 0

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = pointer > 0

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = pointer > 0

    override fun keyDown(keycode: Int): Boolean = false

    override fun keyUp(keycode: Int): Boolean = false

    override fun keyTyped(character: Char): Boolean = true

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = false

    override fun scrolled(amount: Int): Boolean = false

}