package com.symbol.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class KeyInput implements InputProcessor {

    private KeyInputHandler handler;

    public KeyInput(KeyInputHandler handler) {
        this.handler = handler;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.RIGHT: handler.move(true); break;
            case Input.Keys.LEFT: handler.move(false); break;
            case Input.Keys.Z: handler.jump(); break;
            case Input.Keys.X: handler.shoot(); break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.RIGHT: handler.stop(true); break;
            case Input.Keys.LEFT: handler.stop(false); break;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) { return false; }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override
    public boolean scrolled(int amount) { return false; }

}