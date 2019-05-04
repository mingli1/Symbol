package com.symbol.game.screen

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.symbol.game.Symbol

private const val HEADER_WIDTH = 116f
private const val HEADER_HEIGHT = 22f

class MapSelectScreen(game: Symbol) : DefaultScreen(game) {

    private val res = game.res

    private lateinit var progressLabel: Label

    init {
        createBackButton()
        createHeader()
    }

    private fun createBackButton() {
        val buttonStyle = res.getImageButtonStyle("back")
        val backButton = ImageButton(buttonStyle)
        backButton.setPosition(8f, 97f)

        backButton.addListener(object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                fadeToScreen(game.menuScreen)
            }
        })

        stage.addActor(backButton)
    }

    private fun createHeader() {
        val root = Container<Container<Table>>()
        root.setFillParent(true)

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
        root.actor = bgContainer
        root.top()
        root.padTop(5f)

        stage.addActor(root)
    }

    override fun show() {
        super.show()
        fadeIn(FADE_DURATION)
    }

    override fun render(dt: Float) {
        super.render(dt)
        game.profile("MapSelectScreen")
    }

}