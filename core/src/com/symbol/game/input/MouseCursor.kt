package com.symbol.game.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.utils.Disposable

private const val CURSOR_HIDE_TIME = 2f

class MouseCursor : Disposable {

    private lateinit var cursor: Cursor
    private lateinit var emptyCursor: Cursor

    private var timer = 0f

    fun createCursor() {
        val cursor64 = Pixmap(Gdx.files.internal("textures/cursor64.png"))
        val cursorInvisible = Pixmap(Gdx.files.internal("textures/cursor_invisible.png"))

        cursor = Gdx.graphics.newCursor(cursor64, 0, 0)
        emptyCursor = Gdx.graphics.newCursor(cursorInvisible, 0, 0)

        Gdx.graphics.setCursor(cursor)

        cursor64.dispose()
        cursorInvisible.dispose()
    }

    fun update(dt: Float) {
        if (Gdx.input.deltaX != 0 || Gdx.input.deltaY != 0) {
            timer = 0f
            Gdx.graphics.setCursor(cursor)
        }
        else {
            timer += dt
            if (timer >= CURSOR_HIDE_TIME) {
                Gdx.graphics.setCursor(emptyCursor)
                timer = CURSOR_HIDE_TIME
            }
        }
    }

    override fun dispose() {
        cursor.dispose()
        emptyCursor.dispose()
    }

}