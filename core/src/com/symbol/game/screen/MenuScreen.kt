package com.symbol.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.symbol.game.Symbol
import com.symbol.game.input.MultiTouchDisabler
import com.symbol.game.map.TILE_SIZE
import com.symbol.game.map.camera.Background
import com.symbol.game.scene.MovingImage

private const val BACKGROUND_VELOCITY = -40f
private const val BACKGROUND_SCALE = 0.4f

private const val NUM_BUTTONS = 3
private const val BUTTON_WIDTH = 100f
private const val PLAY_TAG = "play"
private const val HELP_TAG = "help"
private const val SETTINGS_TAG = "settings"

class MenuScreen(game: Symbol) : AbstractScreen(game) {

    private val multiplexer = InputMultiplexer()

    private val blockTexture = game.res.getTexture("tileset")!!.split(TILE_SIZE, TILE_SIZE)[0][3]
    private val background = Background(game.res.getTexture("background")!!,
            cam, Vector2(BACKGROUND_SCALE, 0f), Vector2(BACKGROUND_VELOCITY, 0f))

    private val buttonTable = Table()
    private lateinit var playerImage: Image

    private val logoChars = "symbol".toCharArray()
    private val letters = Array(6) {
        MovingImage(game.res.getTexture("logo_${logoChars[it]}")!!, Vector2(), Vector2(), 150f)
    }
    private val letterPositions = arrayOf(Vector2(40f, 91f), Vector2(63f, 83f), Vector2(86f, 91f),
            Vector2(110f, 91f), Vector2(133f, 91f), Vector2(156f, 91f))
    private val letterOrigins = arrayOf(Vector2(-15f, 91f), Vector2(220f, 83f), Vector2(216f, 91f),
            Vector2(222f, 91f), Vector2(215f, 91f), Vector2(222f, 91f))

    init {
        buttonTable.setFillParent(true)
        buttonTable.bottom().padRight(TILE_SIZE * 2f)

        stage.addActor(buttonTable)

        createButtons()
        createTitle()

        multiplexer.addProcessor(MultiTouchDisabler())
        multiplexer.addProcessor(stage)
    }

    private fun createButtons() {
        val blockImages = Array(NUM_BUTTONS) { Image(blockTexture) }
        val buttonTexts = arrayOf(PLAY_TAG, HELP_TAG, SETTINGS_TAG)

        playerImage = Image(game.res.getTexture("player"))
        playerImage.setPosition(34f, 64f)
        stage.addActor(playerImage)

        for (i in 0 until NUM_BUTTONS) {
            buttonTable.add(blockImages[i]).padRight(TILE_SIZE.toFloat())
            val button = TextButton(buttonTexts[i], game.res.getTextButtonStyle("menu"))
            buttonTable.add(button).padBottom(TILE_SIZE.toFloat()).width(BUTTON_WIDTH).height(TILE_SIZE * 2f).row()

            button.addListener(object: InputListener() {
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    playerImage.setPosition(34f, 64f - i * 24f)
                }
            })
        }
    }

    private fun createTitle() {
        for (letter in letters) {
            stage.addActor(letter)
        }
    }

    private fun resetTitleAnimation() {
        for (i in 0..5) {
            val letter = letters[i]
            val origin = letterOrigins[i]
            val target = letterPositions[i]

            letter.setOrigin(origin.x, origin.y)
            letter.target.set(target)
            if (i < 5) letter.link(letters[i + 1])
            if (i > 0) letter.speed = 300f
        }
        letters[0].start()
    }

    override fun show() {
        Gdx.input.inputProcessor = multiplexer
        resetTitleAnimation()

        playerImage.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)))
        buttonTable.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)))
    }

    private fun update(dt: Float) {
        background.update(dt)

        for (letter in letters) {
            letter.update(dt)
        }
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

        game.profile("MenuScreen")
    }

}