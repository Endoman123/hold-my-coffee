package com.coffee.main.screen;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.systems.DrawSystem;
import com.coffee.entity.systems.MovementSystem;
import com.coffee.entity.systems.SpawnerSystem;
import com.coffee.main.Application;

/**
 * Test to see if stars spawn properly.
 *
 * @author Jared Tulayan
 */
public class StarsTest extends ScreenAdapter {
    private final SpriteBatch BATCH;
    private final Viewport VIEWPORT;
    private final ShapeRenderer SHAPE_RENDERER;
    private final PooledEngine ENGINE;

    public StarsTest() {
        Application app = (Application) Gdx.app.getApplicationListener();

        BATCH = app.getBatch();
        VIEWPORT = app.getViewport();
        ENGINE = new PooledEngine();
        SHAPE_RENDERER = app.getShapeRenderer();

        ENGINE.addSystem(new SpawnerSystem(ENGINE));
        ENGINE.addSystem(new DrawSystem(BATCH, VIEWPORT));
        ENGINE.addSystem(new MovementSystem(VIEWPORT));

        ENGINE.addEntity(EntityFactory.createParticleGenerator());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(10 / 255f, 5 / 255f, 9 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        ENGINE.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        VIEWPORT.update(width, height, true);
    }

    public void show() {

    }

    @Override
    public void hide() {

    }
}
