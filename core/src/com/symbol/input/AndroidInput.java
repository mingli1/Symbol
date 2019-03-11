package com.symbol.input;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.symbol.game.Symbol;
import com.symbol.scene.Scene;

public class AndroidInput extends Scene {

    private static final float DIRECTIONAL_BUTTON_SIZE = 30f;
    private static final float ACTION_BUTTON_SIZE = 30f;

    private static final Vector2 LEFT_BUTTON_POSITION = new Vector2(5, 10);
    private static final Vector2 RIGHT_BUTTON_POSITION = new Vector2(35, 10);
    private static final Vector2 JUMP_BUTTON_POSITION = new Vector2(134, 3);
    private static final Vector2 SHOOT_BUTTON_POSITION = new Vector2(164, 25);

    private KeyInputHandler keyInputHandler;

    public AndroidInput(final Symbol game, KeyInputHandler keyInputHandler) {
        super(game);
        this.keyInputHandler = keyInputHandler;

        createDirectionalButtons();
        createActionButtons();
    }

    private void createDirectionalButtons() {
        ImageButton leftButton = new ImageButton(game.getRes().getButtonStyle("left"));
        leftButton.setPosition(LEFT_BUTTON_POSITION.x, LEFT_BUTTON_POSITION.y);
        leftButton.setSize(DIRECTIONAL_BUTTON_SIZE, DIRECTIONAL_BUTTON_SIZE);

        leftButton.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                keyInputHandler.move(false);
                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                keyInputHandler.stop(false);
            }
        });

        ImageButton rightButton = new ImageButton(game.getRes().getButtonStyle("right"));
        rightButton.setPosition(RIGHT_BUTTON_POSITION.x, RIGHT_BUTTON_POSITION.y);
        rightButton.setSize(DIRECTIONAL_BUTTON_SIZE, DIRECTIONAL_BUTTON_SIZE);

        rightButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                keyInputHandler.move(true);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                keyInputHandler.stop(true);
            }
        });

        stage.addActor(leftButton);
        stage.addActor(rightButton);
    }

    private void createActionButtons() {
        ImageButton jumpButton = new ImageButton(game.getRes().getButtonStyle("jump"));
        jumpButton.setPosition(JUMP_BUTTON_POSITION.x, JUMP_BUTTON_POSITION.y);
        jumpButton.setSize(ACTION_BUTTON_SIZE, ACTION_BUTTON_SIZE);

        jumpButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                keyInputHandler.jump();
                return true;
            }
        });

        ImageButton shootButton = new ImageButton(game.getRes().getButtonStyle("shoot"));
        shootButton.setPosition(SHOOT_BUTTON_POSITION.x, SHOOT_BUTTON_POSITION.y);
        shootButton.setSize(ACTION_BUTTON_SIZE, ACTION_BUTTON_SIZE);

        shootButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                keyInputHandler.shoot();
                return true;
            }
        });

        stage.addActor(jumpButton);
        stage.addActor(shootButton);
    }

    @Override
    public void update(float dt) {}

    @Override
    public void render(float dt) {
        stage.act(dt);
        stage.draw();
    }

}
