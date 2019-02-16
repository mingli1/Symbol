package com.symbol.screen

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector2
import com.symbol.ecs.Mapper
import com.symbol.ecs.entity.Player
import com.symbol.ecs.system.*
import com.symbol.ecs.system.enemy.EnemyActivationSystem
import com.symbol.ecs.system.enemy.EnemyAttackSystem
import com.symbol.ecs.system.enemy.EnemyMovementSystem
import com.symbol.game.Symbol
import com.symbol.input.KeyInput
import com.symbol.input.KeyInputSystem
import com.symbol.map.camera.Background
import com.symbol.map.MapManager
import com.symbol.map.camera.CameraShake

private const val CAMERA_LERP = 2.5f
private const val PARALLAX_SCALING = 0.2f

class GameScreen(game: Symbol) : AbstractScreen(game) {

    private val engine = PooledEngine()

    private val input: KeyInput
    private val mm: MapManager = MapManager(game.batch, cam, engine, game.res)

    private var player: Player = Player(game.res)
    private val background: Background = Background(game.res.getTexture("background")!!,
            cam, Vector2(PARALLAX_SCALING, PARALLAX_SCALING))

    init {
        engine.addEntity(player)
        initSystems()

        val keyInputSystem = KeyInputSystem(game.res)
        input = KeyInput(keyInputSystem)
        engine.addSystem(keyInputSystem)
        engine.addSystem(PlayerSystem(player))
    }

    private fun initSystems() {
        engine.addSystem(MovementSystem())
        engine.addSystem(MapCollisionSystem())
        engine.addSystem(ProjectileSystem(game.res))
        engine.addSystem(EnemyActivationSystem(player))
        engine.addSystem(EnemyMovementSystem(player))
        engine.addSystem(EnemyAttackSystem(player, game.res))
        engine.addSystem(DirectionSystem())
        engine.addSystem(GravitySystem())
        engine.addSystem(RenderSystem(game.batch))
        engine.addSystem(HealthSystem())
        engine.addSystem(RemoveSystem())
    }

    override fun show() {
        Gdx.input.inputProcessor = input
        mm.load("test_map")

        val playerPosition = Mapper.POS_MAPPER.get(player)
        playerPosition.set(mm.playerSpawnPosition.x, mm.playerSpawnPosition.y)

        engine.getSystem(MapCollisionSystem::class.java).setMapData(mm.mapObjects,
                mm.mapWidth * mm.tileSize, mm.mapHeight * mm.tileSize)
        engine.getSystem(ProjectileSystem::class.java).setMapData(mm.mapObjects)
        engine.getSystem(EnemyAttackSystem::class.java).reset()
    }

    private fun update(dt: Float) {
        updateCamera(dt)
        background.update(dt)
        mm.update()
    }

    private fun updateCamera(dt: Float) {
        val playerPos = Mapper.POS_MAPPER.get(player)

        cam.position.x += (playerPos.x + (mm.tileSize / 2) - cam.position.x) * CAMERA_LERP * dt
        cam.position.y += (playerPos.y + (mm.tileSize / 2) - cam.position.y) * CAMERA_LERP * dt

        if (CameraShake.time > 0 || CameraShake.toggle) {
            CameraShake.update(dt)
            cam.translate(CameraShake.position)
        }

        cam.update()
    }

    override fun render(dt: Float) {
        update(dt)

        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = cam.combined
        game.batch.begin()

        background.render(game.batch)
        mm.render()
        engine.update(dt)

        game.batch.end()
    }

    override fun dispose() {
        super.dispose()
        mm.dispose()
    }

}