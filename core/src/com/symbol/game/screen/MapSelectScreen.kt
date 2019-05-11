package com.symbol.game.screen

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.symbol.game.Symbol
import com.symbol.game.data.MapData
import com.symbol.game.scene.dialog.MapDialog
import com.symbol.game.scene.page.MapPage
import com.symbol.game.scene.page.MapPage.MapPageType.*
import com.symbol.game.scene.page.PagedScrollPane

private const val HEADER_WIDTH = 116f
private const val HEADER_HEIGHT = 22f

private const val PAGE_FLING_TIME = 1f
private const val PAGE_TOP_PADDING = 50f
private const val PAGE_BOTTOM_PADDING = 34f

class MapSelectScreen(game: Symbol) : DefaultScreen(game) {

    private val res = game.res

    lateinit var pagedScrollPane: PagedScrollPane
    private lateinit var progressLabel: Label
    private lateinit var headerContainer: Container<Container<Table>>
    private lateinit var backButton: ImageButton

    private val mapDialog = MapDialog(game.res, this)

    init {
        createPagedScrollPane()
        createBackButton()
        createHeader()
    }

    private fun createPagedScrollPane() {
        val container = Container<PagedScrollPane>().apply { setFillParent(true) }

        val emptyDrawable = TextureRegionDrawable(res.getTexture("default-rect"))
        val scrollPaneStyle = ScrollPane.ScrollPaneStyle().apply {
            background = emptyDrawable
            corner = emptyDrawable
            hScroll = emptyDrawable
            hScrollKnob = emptyDrawable
            vScroll = emptyDrawable
            vScrollKnob = emptyDrawable
        }

        pagedScrollPane = PagedScrollPane(false, scrollPaneStyle, 0f).apply {
            setFlingTime(PAGE_FLING_TIME)
            setOverscroll(false, false)
            disableSnapToPage()
            disableAutoReset()

            addPadding(PAGE_TOP_PADDING)
            res.mapDatas.let {
                it.forEachIndexed { index, data ->
                    val mapPageType = when (index) {
                        0 -> Start
                        it.size - 1 -> if (it.size % 2 == 0) EndRight else EndLeft
                        else -> if (index % 2 == 0) Left else Right
                    }
                    addPage(MapPage(res, data, mapPageType, this@MapSelectScreen))
                }
            }
            addPadding(PAGE_BOTTOM_PADDING)
        }

        container.actor = pagedScrollPane
        stage.addActor(container)
    }

    private fun createBackButton() {
        val buttonStyle = res.getImageButtonStyle("back")
        backButton = ImageButton(buttonStyle).apply {
            setPosition(8f, 97f)
            addListener(object: ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    fadeToScreen(game.menuScreen)
                }
            })
        }

        stage.addActor(backButton)
    }

    private fun createHeader() {
        headerContainer = Container<Container<Table>>().apply { setFillParent(true) }

        val bgTexture = res.getNinePatch("map_select_header_bg")!!
        val bgContainer = Container<Table>().apply {
            background = NinePatchDrawable(bgTexture)
            width(HEADER_WIDTH)
            height(HEADER_HEIGHT)
        }

        val headerTable = Table().apply { setFillParent(true) }

        val labelStyle = res.getLabelStyle()
        val prompt = Label(res.getString("mapSelectHeader"), labelStyle)
        prompt.setFontScale(1.5f)
        headerTable.add(prompt).expandX().padTop(4f).row()

        val progressGroup = HorizontalGroup().apply { space(6f) }
        val progressIcon = Image(res.getTexture("map_complete_icon"))
        progressLabel = Label(res.getString("mapSelectProgress"), labelStyle)

        progressGroup.addActor(progressIcon)
        progressGroup.addActor(progressLabel)
        headerTable.add(progressGroup).padTop(2f).padRight(8f)

        bgContainer.actor = headerTable
        headerContainer.run {
            actor = bgContainer
            top()
            padTop(5f)
        }

        stage.addActor(headerContainer)
    }

    fun showMapDialog(right: Boolean, mapData: MapData) {
        with (mapDialog) {
            if (!isDisplayed) {
                setData(right, mapData)
                show(this@MapSelectScreen.stage)

                val maxZIndex = stage.actors.size + 1
                backButton.zIndex = maxZIndex
            }
        }
    }

    fun hideMapDialog() {
        if (mapDialog.isDisplayed) {
            mapDialog.hide()
            backButton.zIndex = 0
        }
    }

    fun navigateToGameScreen() = fadeToScreen(game.gameScreen)

    override fun show() {
        super.show()
        fadeIn(FADE_DURATION)
    }

    override fun render(dt: Float) {
        super.render(dt)
        game.profile("MapSelectScreen")
    }

    override fun exit() = hideMapDialog()

}