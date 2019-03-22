package com.symbol.game.map.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.symbol.game.Config;

public class Background {

    private TextureRegion bgTexture;
    private OrthographicCamera cam;
    private Vector2 scale;

    private Vector2 position = new Vector2();
    private Vector2 velocity = new Vector2();

    private int numDrawX;
    private int numDrawY;

    public Background(TextureRegion bgTexture, OrthographicCamera cam, Vector2 scale) {
        this.bgTexture = bgTexture;
        this.cam = cam;
        this.scale = scale;

        numDrawX = (Config.V_WIDTH * 2) / bgTexture.getRegionWidth() + 1;
        numDrawY = (Config.V_HEIGHT * 2) / bgTexture.getRegionHeight() + 1;
        fixBleeding(bgTexture);
    }

    public void update(float dt) {
        position.x += (velocity.x * scale.x) * dt;
        position.y += (velocity.y * scale.y) * dt;
    }

    public void render(Batch batch) {
        float x = ((position.x + cam.viewportWidth / 2 - cam.position.x) * scale.x) % bgTexture.getRegionWidth();
        float y = ((position.y + cam.viewportHeight / 2 - cam.position.y) * scale.y) % bgTexture.getRegionHeight();

        int colOffset = x > 0 ? -1 : 0;
        int rowOffset = y > 0 ? -1 : 0;

        for (int row = 0; row < numDrawY; row++) {
            for (int col = 0; col < numDrawX; col++) {
                batch.draw(bgTexture,
                        x + (col + colOffset) * bgTexture.getRegionWidth(),
                        y + (row + rowOffset) * bgTexture.getRegionHeight());
            }
        }
    }

    private void fixBleeding(TextureRegion texture) {
        float fix = 0.01f;
        float invWidth = 1f / texture.getTexture().getWidth();
        float invHeight = 1f / texture.getTexture().getHeight();
        texture.setRegion((texture.getRegionX() + fix) * invWidth,
                (texture.getRegionY() + fix) * invHeight,
                (texture.getRegionX() + texture.getRegionWidth() - fix) * invWidth,
                (texture.getRegionY() + texture.getRegionHeight() - fix) * invHeight);
    }

}