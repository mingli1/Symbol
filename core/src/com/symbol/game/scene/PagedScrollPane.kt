package com.symbol.game.scene

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table

class PagedScrollPane(style: ScrollPaneStyle, pageSpace: Float) : ScrollPane(null, style) {

    private var panDragOrFling = false
    private val container = Table()

    init {
        container.defaults().space(pageSpace)
        actor = container
        setFadeScrollBars(true)
        setupFadeScrollBars(0f, 0f)
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
        else if (isPanning || isDragging || isFlinging) {
            panDragOrFling = true
            resetCurrentPage()
        }
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

    fun scrollToLeft() {
        val currIndex = getCurrentIndex()
        if (currIndex == 0) return

        resetCurrentPage()
        scrollX = container.children[currIndex - 1].x
    }

    fun scrollToRight() {
        val currIndex = getCurrentIndex()
        if (currIndex == container.children.size - 1) return

        resetCurrentPage()
        scrollX = container.children[currIndex + 1].x
    }

    fun resetCurrentPage() {
        val currPage = (getCurrentPage() as Page)
        currPage.reset()
        if (!currPage.hasSeen()) currPage.notifySeen()
    }

    fun hasAllSeen() : Boolean {
        for (page in container.children) {
            if (!(page as Page).hasSeen()) return false
        }
        return true
    }

    fun isCurrentPageSeen() : Boolean {
        return (getCurrentPage() as Page).hasSeen()
    }

    private fun getCurrentPage() : Actor? {
        for (page in container.children) {
            if (scrollX <= page.x + page.width) {
                return page
            }
        }
        return null
    }

    private fun getCurrentIndex() : Int {
        for ((index, page) in container.children.withIndex()) {
            if (scrollX <= page.x + page.width) return index
        }
        return -1
    }

}