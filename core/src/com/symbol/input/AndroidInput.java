package com.symbol.input;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.symbol.game.Symbol;
import com.symbol.scene.Scene;

public class AndroidInput extends Scene {

    private static final Vector2 LEFT_BUTTON_POSITION = new Vector2(10, 20);
    private static final Vector2 RIGHT_BUTTON_POSITION = new Vector2(40, 20);
    private static final Vector2 JUMP_BUTTON_POSITION = new Vector2(144, 8);
    private static final Vector2 SHOOT_BUTTON_POSITION = new Vector2(174, 30);

    private KeyInputHandler keyInputHandler;

    public AndroidInput(final Symbol game, KeyInputHandler keyInputHandler) {
        super(game);
        this.keyInputHandler = keyInputHandler;

        createDirectionalButtons();
        createJumpAndShootButtons();
    }

    private void createDirectionalButtons() {
        ImageButton leftButton = new ImageButton(game.getRes().getButtonStyle("left"));
        leftButton.setPosition(LEFT_BUTTON_POSITION.x, LEFT_BUTTON_POSITION.y);

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

        rightButton.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                keyInputHandler.move(true);
                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                keyInputHandler.stop(true);
            }
        });

        stage.addActor(leftButton);
        stage.addActor(rightButton);
    }

    private void createJumpAndShootButtons() {
        ImageButton jumpButton = new ImageButton(game.getRes().getButtonStyle("jump"));
        jumpButton.setPosition(JUMP_BUTTON_POSITION.x, JUMP_BUTTON_POSITION.y);

        jumpButton.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                keyInputHandler.jump();
            }
        });

        ImageButton shootButton = new ImageButton(game.getRes().getButtonStyle("shoot"));
        shootButton.setPosition(SHOOT_BUTTON_POSITION.x, SHOOT_BUTTON_POSITION.y);

        shootButton.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                keyInputHandler.shoot();
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
