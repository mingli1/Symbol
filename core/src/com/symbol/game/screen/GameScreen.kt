package com.symbol.game.screen

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector2
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
import com.symbol.game.map.camera.CameraShake
import com.symbol.game.scene.Hud

private const val CAMERA_LERP = 2.5f
private const val PARALLAX_SCALING = 0.2f

class GameScreen(game: Symbol) : AbstractScreen(game) {

    private val engine = PooledEngine()

    private val multiplexer = InputMultiplexer()
    private val input: KeyInput
    private val androidInput: AndroidInput

    private val mm = MapManager(engine, game.res)

    private var player = Player(game.res)
    private val background = Background(game.res.getTexture("background")!!,
            cam, Vector2(PARALLAX_SCALING, PARALLAX_SCALING))

    private val hud = Hud(game, player, stage, viewport)

    init {
        engine.addEntity(player)
        initSystems()

        val keyInputSystem = KeyInputSystem(game.res)
        input = KeyInput(keyInputSystem)
        androidInput = AndroidInput(game, keyInputSystem, stage, viewport)

        engine.addSystem(keyInputSystem)
        engine.addSystem(PlayerSystem(player))

        multiplexer.addProcessor(stage)
        multiplexer.addProcessor(input)
    }

    private fun initSystems() {
        engine.addSystem(MovementSystem())
        engine.addSystem(MapCollisionSystem(game.res))
        engine.addSystem(MapEntitySystem(player, game.res))
        engine.addSystem(ProjectileSystem(player, game.res))
        engine.addSystem(HealthSystem())
        engine.addSystem(EnemyActivationSystem(player))
        engine.addSystem(EnemyAttackSystem(player, game.res))
        engine.addSystem(EnemyMovementSystem(player, game.res))
        engine.addSystem(DirectionSystem())
        engine.addSystem(GravitySystem())
        engine.addSystem(StatusEffectSystem())
        engine.addSystem(RenderSystem(game.batch))
        engine.addSystem(StatusRenderSystem(game.batch, game.res))
        engine.addSystem(RemoveSystem())
    }

    private fun resetSystems() {
        engine.getSystem(MapCollisionSystem::class.java).setMapData(mm.mapObjects,
                mm.mapWidth * TILE_SIZE, mm.mapHeight * TILE_SIZE)
        engine.getSystem(ProjectileSystem::class.java).setMapData(mm.mapObjects)
    }

    override fun show() {
        Gdx.input.inputProcessor = multiplexer
        mm.load("test_map")

        val playerPosition = Mapper.POS_MAPPER.get(player)
        playerPosition.set(mm.playerSpawnPosition.x, mm.playerSpawnPosition.y)

        resetSystems()
    }

    private fun update(dt: Float) {
        updateCamera(dt)
        background.update(dt)
        ParticleSpawner.update(dt)

        hud.update(dt)
    }

    private fun updateCamera(dt: Float) {
        val playerPos = Mapper.POS_MAPPER.get(player)

        cam.position.x += (playerPos.x + (TILE_SIZE / 2) - cam.position.x) * CAMERA_LERP * dt
        cam.position.y += (playerPos.y + (TILE_SIZE / 2) - cam.position.y) * CAMERA_LERP * dt

        if (CameraShake.time > 0 || CameraShake.toggle) {
            CameraShake.update(dt)
            cam.translate(CameraShake.position)
        }

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

        background.render(game.batch)
        mm.render(game.batch, cam)
        updateEngine(dt)
        ParticleSpawner.render(game.batch)

        game.batch.projectionMatrix = stage.camera.combined

        hud.render(dt)

        game.batch.end()

        stage.act(dt)
        stage.draw()

        game.profile("GameScreen")
    }

    override fun notifyPause() {
        super.notifyPause()
        if (!stage.actors.contains(hud.pauseDialog)) {
            hud.pauseDialog.show(stage)
        }
    }

    override fun dispose() {
        super.dispose()
        mm.dispose()

        hud.dispose()
    }

}