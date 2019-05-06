package com.symbol.game.screen

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.symbol.game.Symbol
import com.symbol.game.scene.dialog.MapDialog
import com.symbol.game.scene.page.MapPage
import com.symbol.game.scene.page.PagedScrollPane

private const val HEADER_WIDTH = 116f
private const val HEADER_HEIGHT = 22f

private const val PAGE_FLING_TIME = 0.1f
private const val PAGE_TOP_PADDING = 50f
private const val PAGE_BOTTOM_PADDING = 34f * 2

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
        val container = Container<PagedScrollPane>()
        container.setFillParent(true)

        val emptyDrawable = TextureRegionDrawable(res.getTexture("default-rect"))
        val scrollPaneStyle = ScrollPane.ScrollPaneStyle()
        scrollPaneStyle.background = emptyDrawable
        scrollPaneStyle.corner = emptyDrawable
        scrollPaneStyle.hScroll = emptyDrawable
        scrollPaneStyle.hScrollKnob = emptyDrawable
        scrollPaneStyle.vScroll = emptyDrawable
        scrollPaneStyle.vScrollKnob = emptyDrawable

        pagedScrollPane = PagedScrollPane(false, scrollPaneStyle, 0f)
        pagedScrollPane.setFlingTime(PAGE_FLING_TIME)
        pagedScrollPane.disableSnapToPage()
        pagedScrollPane.disableAutoReset()

        pagedScrollPane.addPadding(PAGE_TOP_PADDING)
        pagedScrollPane.addPage(MapPage(res, MapPage.MapPageType.Start, this))
        pagedScrollPane.addPage(MapPage(res, MapPage.MapPageType.Right, this))
        pagedScrollPane.addPage(MapPage(res, MapPage.MapPageType.Left, this))
        pagedScrollPane.addPage(MapPage(res, MapPage.MapPageType.Right, this))
        pagedScrollPane.addPage(MapPage(res, MapPage.MapPageType.Left, this))
        pagedScrollPane.addPage(MapPage(res, MapPage.MapPageType.Right, this))
        pagedScrollPane.addPage(MapPage(res, MapPage.MapPageType.Left, this))
        pagedScrollPane.addPage(MapPage(res, MapPage.MapPageType.EndRight, this))
        pagedScrollPane.addPadding(PAGE_BOTTOM_PADDING)

        container.actor = pagedScrollPane

        stage.addActor(container)
    }

    private fun createBackButton() {
        val buttonStyle = res.getImageButtonStyle("back")
        backButton = ImageButton(buttonStyle)
        backButton.setPosition(8f, 97f)

        backButton.addListener(object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                fadeToScreen(game.menuScreen)
            }
        })

        stage.addActor(backButton)
    }

    private fun createHeader() {
        headerContainer = Container()
        headerContainer.setFillParent(true)

        val bgTexture = res.getNinePatch("map_select_header_bg")!!
        val bgContainer = Container<Table>()
        bgContainer.background = NinePatchDrawable(bgTexture)
        bgContainer.width(HEADER_WIDTH).height(HEADER_HEIGHT)

        val headerTable = Table()
        headerTable.setFillParent(true)

        val labelStyle = res.getLabelStyle()
        val prompt = Label(res.getString("mapSelectHeader"), labelStyle)
        prompt.setFontScale(1.5f)
        headerTable.add(prompt).expandX().padTop(4f).row()

        val progressGroup = HorizontalGroup()
        progressGroup.space(6f)
        val progressIcon = Image(res.getTexture("map_complete_icon"))
        progressLabel = Label(res.getString("mapSelectProgress"), labelStyle)

        progressGroup.addActor(progressIcon)
        progressGroup.addActor(progressLabel)
        headerTable.add(progressGroup).padTop(2f).padRight(8f)

        bgContainer.actor = headerTable
        headerContainer.actor = bgContainer
        headerContainer.top()
        headerContainer.padTop(5f)

        stage.addActor(headerContainer)
    }

    fun showMapDialog(right: Boolean) {
        if (!mapDialog.isDisplayed) {
            mapDialog.setOrientation(right)
            mapDialog.show(stage)

            val maxZIndex = stage.actors.size + 1
            backButton.zIndex = maxZIndex
        }
    }

    fun hideMapDialog() {
        if (mapDialog.isDisplayed) {
            mapDialog.hide()
            backButton.zIndex = 0
        }
    }

    fun navigateToGameScreen() {
        fadeToScreen(game.gameScreen)
    }

    override fun show() {
        super.show()
        fadeIn(FADE_DURATION)
    }

    override fun render(dt: Float) {
        super.render(dt)
        game.profile("MapSelectScreen")
    }

    override fun exit() {
        hideMapDialog()
    }

}