package com.coffee.main.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.main.Application;

/**
 * Quick test to see if VIEWPORT works.
 * @author Jared Tulayan
 */
public class ViewportTest extends ScreenAdapter {
    private final SpriteBatch BATCH;
    private final Viewport VIEWPORT;
    private final Texture TEX;

    public ViewportTest() {
        Application app = (Application) Gdx.app.getApplicationListener();

        BATCH = app.getBatch();
        VIEWPORT = app.getViewport();
        TEX = new Texture("badlogic.jpg");
    }

    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        BATCH.setProjectionMatrix(VIEWPORT.getCamera().combined);
        BATCH.begin();
        BATCH.draw(TEX, 0, 0);
        BATCH.end();
    }

    @Override
    public void resize(int width, int height) {
        VIEWPORT.update(width, height, true);
    }

    @Override
    public void dispose() {
        TEX.dispose();
    }
}
