package com.symbol.screen

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.symbol.ecs.EntityFactory
import com.symbol.ecs.Mapper
import com.symbol.ecs.system.GravitySystem
import com.symbol.ecs.system.MapCollisionSystem
import com.symbol.ecs.system.MovementSystem
import com.symbol.ecs.system.RenderSystem
import com.symbol.game.Symbol
import com.symbol.input.KeyInput
import com.symbol.input.KeyInputSystem
import com.symbol.map.TileMapManager

class GameScreen(game: Symbol) : AbstractScreen(game) {

    private val engine = PooledEngine()

    private val input: KeyInput
    private val tmm: TileMapManager = TileMapManager(game.batch, cam)

    private var player: Entity

    init {
        initSystems()

        player = EntityFactory.createPlayer(engine, game.res)

        val keyInputSystem = KeyInputSystem()
        input = KeyInput(keyInputSystem)
        engine.addSystem(keyInputSystem)
    }

    private fun initSystems() {
        engine.addSystem(MovementSystem())
        engine.addSystem(MapCollisionSystem())
        engine.addSystem(GravitySystem())
        engine.addSystem(RenderSystem(game.batch))
    }

    override fun show() {
        Gdx.input.inputProcessor = input
        tmm.load("test_map")

        val playerPosition = Mapper.POS_MAPPER.get(player)
        playerPosition.x = tmm.playerSpawnPosition.x
        playerPosition.y = tmm.playerSpawnPosition.y

        engine.getSystem(MapCollisionSystem::class.java).setMapData(tmm.mapObjects)
    }

    private fun update(dt: Float) {
        tmm.update()
    }

    override fun render(dt: Float) {
        update(dt)

        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = cam.combined
        game.batch.begin()

        tmm.render()
        engine.update(dt)

        game.batch.end()
    }

    override fun dispose() {
        super.dispose()
        tmm.dispose()
    }

}