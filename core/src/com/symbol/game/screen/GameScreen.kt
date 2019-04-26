package com.symbol.game.screen

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector2
import com.symbol.game.Config
import com.symbol.game.Symbol
import com.symbol.game.ecs.Mapper
import com.symbol.game.ecs.entity.Player
import com.symbol.game.ecs.system.*
import com.symbol.game.ecs.system.enemy.EnemyActivationSystem
import com.symbol.game.ecs.system.enemy.EnemyAttackSystem
import com.symbol.game.ecs.system.enemy.EnemyMovementSystem
import com.symbol.game.effects.particle.ParticleSpawner
import com.symbol.game.input.AndroidInput
import com.symbol.game.input.KeyInput
import com.symbol.game.input.KeyInputSystem
import com.symbol.game.map.MapManager
import com.symbol.game.map.TILE_SIZE
import com.symbol.game.map.camera.Background
import com.symbol.game.map.camera.CameraRotation
import com.symbol.game.map.camera.CameraShake
import com.symbol.game.scene.Hud
import com.symbol.game.scene.dialog.DeathDialog

private const val CAMERA_LERP = 2.5f
private const val PARALLAX_SCALING = 0.2f
private const val DEBUG_CAM_SPEED = 2f

class GameScreen(game: Symbol) : AbstractScreen(game) {

    private val engine = PooledEngine()

    private val multiplexer = InputMultiplexer()
    private val input: KeyInput
    private val androidInput: AndroidInput

    private val mapManager = MapManager(engine, game.res)
    private var canInvert = false
    var mapInverted = false

    private var player = Player(game.res)
    private val background = Background(game.res.getTexture("background")!!,
            cam, Vector2(PARALLAX_SCALING, PARALLAX_SCALING))

    private val hud = Hud(game, player, stage, viewport)
    val deathDialog = DeathDialog(game)

    private var debugCamera = false

    init {
        engine.addEntity(player)
        initSystems()

        val keyInputSystem = KeyInputSystem(game.res)
        input = KeyInput(keyInputSystem)
        androidInput = AndroidInput(game, keyInputSystem, stage, viewport)

        engine.addSystem(keyInputSystem)
        engine.addSystem(PlayerSystem(player, this))

        multiplexer.addProcessor(stage)
        if (!Config.onAndroid()) multiplexer.addProcessor(input)

        CameraRotation.init(cam)
    }

    private fun initSystems() {
        engine.addSystem(MovementSystem())
        engine.addSystem(MapCollisionSystem(game.res))
        engine.addSystem(MapEntitySystem(player, game.res))
        engine.addSystem(ProjectileSystem(player, game.res, this))
        engine.addSystem(HealthSystem())
        engine.addSystem(EnemyActivationSystem(player))
        engine.addSystem(EnemyAttackSystem(player, game.res))
        engine.addSystem(EnemyMovementSystem(player, game.res))
        engine.addSystem(DirectionSystem())
        engine.addSystem(GravitySystem())
        engine.addSystem(StatusEffectSystem())
        engine.addSystem(RenderSystem(game.batch, cam, game.res))
        engine.addSystem(StatusRenderSystem(game.batch, game.res, cam))
        engine.addSystem(RemoveSystem())
    }

    private fun resetSystems() {
        engine.getSystem(MapCollisionSystem::class.java).setMapData(mapManager.mapObjects,
                mapManager.mapWidth * TILE_SIZE, mapManager.mapHeight * TILE_SIZE)
        engine.getSystem(ProjectileSystem::class.java).setMapData(mapManager.mapObjects)
        engine.getSystem(EnemyAttackSystem::class.java).setMapData(mapManager.mapWidth.toFloat() * TILE_SIZE)
    }

    override fun show() {
        Gdx.input.inputProcessor = multiplexer
        gameState = GameState.Resume

        engine.removeAllEntities()
        engine.addEntity(player)
        player.reset()

        mapManager.load("test_map")
        hud.setHelpPages(mapManager.helpPages)
        canInvert = mapManager.containsInvertSwitch()
        mapInverted = false

        val playerPosition = Mapper.POS_MAPPER.get(player)
        playerPosition.set(mapManager.playerSpawnPosition.x, mapManager.playerSpawnPosition.y)

        cam.up.set(0f, 1f, 0f)
        //cam.position.set(playerPosition.x + (TILE_SIZE / 2), playerPosition.y + (TILE_SIZE / 2), 0f)
        // TODO: decide where to set initial camera position relative to player
        if (!CameraRotation.isEnded()) CameraRotation.end()

        ParticleSpawner.reset()

        resetSystems()
        notifyResume()
        hud.toggle(true);

        //if (hud.hasHelpPageNotSeen()) hud.showHelpDialog()
    }

    private fun update(dt: Float) {
        if (Config.isDebug()) debugCamera()
        updateCamera(dt)
        background.update(dt)
        ParticleSpawner.update(dt)

        hud.update(dt)
    }

    private fun updateCamera(dt: Float) {
        val playerPos = Mapper.POS_MAPPER.get(player)

        if (!debugCamera) {
            cam.position.x += (playerPos.x + (TILE_SIZE / 2) - cam.position.x) * CAMERA_LERP * dt
            cam.position.y += (playerPos.y + (TILE_SIZE / 2) - cam.position.y) * CAMERA_LERP * dt

            if (CameraShake.time > 0 || CameraShake.toggle) {
                CameraShake.update(dt)
                cam.translate(CameraShake.position)
            }
        }

        CameraRotation.update(dt)
        cam.update()
    }

    private fun updateEngine(dt: Float) {
        if (gameState == GameState.Pause) {
            engine.getSystem(RenderSystem::class.java).update(dt)
            engine.getSystem(StatusRenderSystem::class.java).update(dt)
        }
        else engine.update(dt)
    }

    override fun render(dt: Float) {
        if (gameState != GameState.Pause) update(dt)

        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = cam.combined
        game.batch.begin()
        game.batch.setColor(1f, 1f, 1f, 1f)

        if (canInvert) game.batch.shader = if (mapInverted) game.res.invertShader else null

        background.render(game.batch)
        mapManager.render(game.batch, cam)
        updateEngine(dt)
        ParticleSpawner.render(game.batch, cam)

        if (canInvert) {
            game.batch.shader = null
            engine.getSystem(StatusRenderSystem::class.java).update(dt)
        }

        game.batch.projectionMatrix = stage.camera.combined
        hud.render(dt)

        game.batch.end()

        stage.act(dt)
        stage.draw()
    }

    override fun notifyPause() {
        super.notifyPause()
        if (!Config.onAndroid()) multiplexer.removeProcessor(input)
        if (!stage.actors.contains(hud.pauseDialog) && !stage.actors.contains(hud.helpDialog)) {
            hud.pauseDialog.show(stage)
        }
    }

    override fun notifyResume() {
        super.notifyResume()
        if (!Config.onAndroid() && !multiplexer.processors.contains(input)) {
            multiplexer.addProcessor(input)
        }
    }

    fun showDeathDialog() {
        if (!Config.onAndroid()) multiplexer.removeProcessor(input)
        hud.toggle(false)
        deathDialog.show(stage)
    }

    override fun dispose() {
        super.dispose()
        mapManager.dispose()

        hud.dispose()
    }

    // DEBUG ONLY (Desktop)

    private fun debugCamera() {
        val w = Gdx.input.isKeyPressed(Input.Keys.W)
        val a = Gdx.input.isKeyPressed(Input.Keys.A)
        val s = Gdx.input.isKeyPressed(Input.Keys.S)
        val d = Gdx.input.isKeyPressed(Input.Keys.D)
        val f = Gdx.input.isKeyPressed(Input.Keys.F)

        debugCamera = w || a || s || d || f

        if (w) cam.position.y += DEBUG_CAM_SPEED
        if (a) cam.position.x -= DEBUG_CAM_SPEED
        if (s) cam.position.y -= DEBUG_CAM_SPEED
        if (d) cam.position.x += DEBUG_CAM_SPEED

        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) cam.zoom -= 0.1f
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) cam.zoom += 0.1f

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) cam.rotate(45f)
    }

}