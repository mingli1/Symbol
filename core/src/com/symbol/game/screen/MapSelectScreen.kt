package com.symbol.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.symbol.game.Symbol
import com.symbol.game.input.MultiTouchDisabler
import com.symbol.game.map.camera.Background

private const val HEADER_WIDTH = 116f
private const val HEADER_HEIGHT = 20f

class MapSelectScreen(game: Symbol) : AbstractScreen(game) {

    private val res = game.res

    private val multiplexer = InputMultiplexer()
    private val background = Background(game.res.getTexture("background")!!,
            cam, Vector2(BACKGROUND_SCALE, 0f), Vector2(BACKGROUND_VELOCITY, 0f))

    init {
        createBackButton()
        createHeader()

        multiplexer.addProcessor(MultiTouchDisabler())
        multiplexer.addProcessor(stage)
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

        bgContainer.actor = headerTable

        root.actor = bgContainer
        root.top()
        root.padTop(5f)

        stage.addActor(root)
    }

    override fun show() {
        Gdx.input.inputProcessor = multiplexer
        fadeIn(FADE_DURATION)
    }

    private fun update(dt: Float) {
        background.update(dt)
    }

    override fun render(dt: Float) {
        update(dt)

        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = cam.combined
        game.batch.begin()
        game.batch.setColor(1f, 1f, 1f, 1f)

        background.render(game.batch)

        game.batch.end()

        stage.act(dt)
        stage.draw()

        game.profile("MapSelectScreen")
    }

}