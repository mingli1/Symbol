package com.symbol.game.scene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.symbol.game.util.Resources

class Page(drawable: Drawable, res: Resources, index: Int) : Table() {

    init {
        background = drawable
        add(Label("$index",
                Label.LabelStyle(res.font, Color.WHITE)))
    }

}