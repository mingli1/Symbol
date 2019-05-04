package com.symbol.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.symbol.game.Symbol
import com.symbol.game.ecs.entity.PLAYER_JUMP_IMPULSE
import com.symbol.game.ecs.system.GRAVITY
import com.symbol.game.map.TILE_SIZE
import com.symbol.game.scene.DynamicImage
import com.symbol.game.scene.dialog.AboutDialog

internal const val FADE_DURATION = 1f

private const val NUM_BUTTONS = 3
private const val BUTTON_WIDTH = 100f

class MenuScreen(game: Symbol) : DefaultScreen(game) {

    private val blockTexture = game.res.getTexture("tileset")!!.split(TILE_SIZE, TILE_SIZE)[0][3]

    private val aboutDialog = AboutDialog(game)

    private val buttonTable = Table()
    private lateinit var playerImage: DynamicImage
    private lateinit var aboutButton: ImageButton

    private val logoChars = game.res.getString("title")!!.toCharArray()
    private val letters = Array(6) {
        DynamicImage(game.res.getTexture("logo_${logoChars[it]}")!!)
    }
    private val letterPositions = arrayOf(Vector2(37f, 89f), Vector2(60f, 79f), Vector2(83f, 89f),
            Vector2(111f, 89f), Vector2(134f, 89f), Vector2(157f, 89f))
    private val letterOrigins = arrayOf(Vector2(-15f, 89f), Vector2(220f, 79f), Vector2(216f, 89f),
            Vector2(222f, 89f), Vector2(215f, 89f), Vector2(222f, 89f))

    private var transition = false
    private var nextScreen: AbstractScreen? = null

    init {
        buttonTable.setFillParent(true)
        buttonTable.bottom().padRight(TILE_SIZE * 2f)

        stage.addActor(buttonTable)

        createButtons()
        createTitle()
        createAboutButton()
    }

    private fun createButtons() {
        val blockImages = Array(NUM_BUTTONS) { Image(blockTexture) }
        val buttonTexts = arrayOf(game.res.getString("playButton"),
                game.res.getString("helpButton"),
                game.res.getString("settingsButton"))

        playerImage = DynamicImage(game.res.getTexture("player")!!)
        playerImage.setPosition(34f, 64f)
        stage.addActor(playerImage)

        for (i in 0 until NUM_BUTTONS) {
            buttonTable.add(blockImages[i]).padRight(TILE_SIZE.toFloat())
            val button = TextButton(buttonTexts[i], game.res.getTextButtonStyle("menu"))
            buttonTable.add(button).padBottom(TILE_SIZE.toFloat()).width(BUTTON_WIDTH).height(TILE_SIZE * 2f).row()

            button.addListener(object: InputListener() {
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    if (!playerImage.moving()) playerImage.setPosition(34f, 64f - i * 24f)
                }
            })

            button.addListener(object: ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    playerImage.applyJump(-GRAVITY, PLAYER_JUMP_IMPULSE - 45f)
                    transition = true
                    nextScreen = when (i) {
                        0 -> game.mapSelectScreen
                        else -> null
                    }
                    Gdx.input.inputProcessor = null
                }
            })
        }
    }

    private fun createTitle() {
        for (letter in letters) {
            letter.addListener(object: InputListener() {
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    letter.applyJump(-GRAVITY, 60f)
                }
            })
            stage.addActor(letter)
        }
    }

    private fun resetTitleAnimation() {
        for (i in 0..5) {
            val letter = letters[i]
            val origin = letterOrigins[i]
            val target = letterPositions[i]

            letter.applyLinearMovement(origin, target, if (i > 0) 450f else 150f)
            if (i < 5) letter.link(letters[i + 1])
        }
        letters[0].startLinearMovement()
    }

    private fun createAboutButton() {
        aboutButton = ImageButton(game.res.getImageButtonStyle("about"))
        aboutButton.setPosition(176f, 8f)
        stage.addActor(aboutButton)

        aboutButton.addListener(object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                aboutDialog.show(stage)
            }
        })
    }

    override fun show() {
        super.show()
        transition = false
        nextScreen = null

        resetTitleAnimation()
        fadeIn(FADE_DURATION)
    }

    override fun update(dt: Float) {
        super.update(dt)
        playerImage.update(dt)

        for (letter in letters) {
            letter.update(dt)
        }

        if (transition && !playerImage.moving()) {
            transition = false
            if (nextScreen != null) fadeToScreen(nextScreen!!)
        }
    }

}