package com.symbol.screen;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.symbol.ecs.Mapper;
import com.symbol.ecs.component.PositionComponent;
import com.symbol.ecs.entity.Player;
import com.symbol.ecs.system.*;
import com.symbol.ecs.system.enemy.EnemyActivationSystem;
import com.symbol.ecs.system.enemy.EnemyAttackSystem;
import com.symbol.ecs.system.enemy.EnemyHealthBarRenderSystem;
import com.symbol.ecs.system.enemy.EnemyMovementSystem;
import com.symbol.effects.particle.ParticleSpawner;
import com.symbol.game.Config;
import com.symbol.game.Symbol;
import com.symbol.input.AndroidInput;
import com.symbol.input.KeyInput;
import com.symbol.input.KeyInputSystem;
import com.symbol.map.MapManager;
import com.symbol.map.camera.Background;
import com.symbol.map.camera.CameraShake;
import com.symbol.scene.Hud;

public class GameScreen extends AbstractScreen {

    private static final float CAMERA_LERP = 2.5f;
    private static final float PARALLAX_SCALING = 0.2f;

    private PooledEngine engine = new PooledEngine();

    private InputMultiplexer multiplexer = new InputMultiplexer();
    private KeyInput input;
    private AndroidInput androidInput;

    private MapManager mm = new MapManager(game.batch, cam, engine, game.res);

    private Player player = new Player(game.res);
    private Background background = new Background(game.res.getTexture("background"),
            cam, new Vector2(PARALLAX_SCALING, PARALLAX_SCALING));

    private Hud hud = new Hud(game, player);

    public GameScreen(final Symbol game) {
        super(game);

        engine.addEntity(player);
        initSystems();

        KeyInputSystem keyInputSystem = new KeyInputSystem(game.res);
        input = new KeyInput(keyInputSystem);
        androidInput = new AndroidInput(game, keyInputSystem);

        engine.addSystem(keyInputSystem);
        engine.addSystem(new PlayerSystem(player));

        multiplexer.addProcessor(input);
        multiplexer.addProcessor(hud.getStage());
        if (Config.onAndroid()) multiplexer.addProcessor(androidInput.getStage());
    }

    private void initSystems() {
        engine.addSystem(new MovementSystem());
        engine.addSystem(new MapCollisionSystem(game.res));
        engine.addSystem(new MapEntitySystem(player, game.res));
        engine.addSystem(new ProjectileSystem(player, game.res));
        engine.addSystem(new HealthSystem());
        engine.addSystem(new EnemyActivationSystem(player));
        engine.addSystem(new EnemyAttackSystem(player, game.res));
        engine.addSystem(new EnemyMovementSystem(player, game.res));
        engine.addSystem(new DirectionSystem());
        engine.addSystem(new GravitySystem());
        engine.addSystem(new RenderSystem(game.batch));
        engine.addSystem(new EnemyHealthBarRenderSystem(game.batch, game.res));
        engine.addSystem(new RemoveSystem());
    }

    private void resetSystems() {
        engine.getSystem(MapCollisionSystem.class).setMapData(mm.getMapObjects(),
                mm.getMapWidth() * mm.getTileSize(), mm.getMapHeight() * mm.getTileSize());
        engine.getSystem(ProjectileSystem.class).setMapData(mm.getMapObjects());
        engine.getSystem(EnemyAttackSystem.class).reset();
        engine.getSystem(EnemyMovementSystem.class).reset();
        engine.getSystem(EnemyHealthBarRenderSystem.class).reset();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiplexer);
        mm.load("test_map");

        PositionComponent playerPosition = Mapper.INSTANCE.getPOS_MAPPER().get(player);
        playerPosition.set(mm.getPlayerSpawnPosition().x, mm.getPlayerSpawnPosition().y);

        resetSystems();
    }

    private void update(float dt) {
        updateCamera(dt);
        background.update(dt);
        mm.update();
        ParticleSpawner.INSTANCE.update(dt);

        hud.update(dt);
        if (Config.onAndroid()) androidInput.update(dt);
    }

    private void updateCamera(float dt) {
        PositionComponent playerPos = Mapper.INSTANCE.getPOS_MAPPER().get(player);

        cam.position.x += (playerPos.x + (mm.getTileSize() / 2) - cam.position.x) * CAMERA_LERP * dt;
        cam.position.y += (playerPos.y + (mm.getTileSize() / 2) - cam.position.y) * CAMERA_LERP * dt;

        if (CameraShake.time > 0 || CameraShake.toggle) {
            CameraShake.update(dt);
            cam.translate(CameraShake.position);
        }

        cam.update();
    }

    @Override
    public void render(float dt) {
        update(dt);

        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();

        background.render(game.batch);
        mm.render();
        engine.update(dt);
        ParticleSpawner.INSTANCE.render(game.batch);

        game.batch.end();

        hud.render(dt);
        if (Config.onAndroid()) androidInput.render(dt);
    }

    @Override
    public void dispose() {
        super.dispose();
        mm.dispose();

        hud.dispose();
    }

}