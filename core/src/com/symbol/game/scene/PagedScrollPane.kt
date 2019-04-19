package com.symbol.game.scene

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table

class PagedScrollPane(skin: Skin, pageSpace: Float) : ScrollPane(null, skin) {

    private var panDragOrFling = false
    private val container = Table()

    init {
        container.defaults().space(pageSpace)
        actor = container
    }

    fun addPages(vararg pages: Actor) {
        for (page in pages) {
            container.add(page).expandY().fillY()
        }
    }

    fun addPage(page: Actor) : Cell<Actor> {
        return container.add(page).expandY().fillY()
    }

    override fun act(dt: Float) {
        super.act(dt)

        if (panDragOrFling && !isPanning && !isDragging && !isFlinging) {
            panDragOrFling = false
            scrollToPage()
        }
        else panDragOrFling = isPanning || isDragging || isFlinging
    }

    private fun scrollToPage() {
        val pages = container.children

        if (scrollX >= maxX || scrollX <= 0f) return

        var pageX = 0f
        var pageWidth = 0f
        if (pages.size > 0) {
            for (page in pages) {
                pageX = page.x
                pageWidth = page.width
                if (scrollX < pageX + pageWidth / 2f) break
            }
            scrollX = MathUtils.clamp(pageX - (width - pageWidth) / 2f, 0f, maxX)
        }
    }

}