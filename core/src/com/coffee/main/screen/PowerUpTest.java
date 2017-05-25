package com.coffee.main.screen;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.systems.*;
import com.coffee.main.Application;

/**
 * @author Phillip O'Reggio
 */
public class PowerUpTest extends ScreenAdapter {
    private final SpriteBatch BATCH;
    private final Viewport VIEWPORT;
    private final ShapeRenderer SHAPE_RENDERER;
    private final PooledEngine ENGINE;

    public PowerUpTest() {
        Application app = (Application) Gdx.app.getApplicationListener();

        BATCH = app.getBatch();
        VIEWPORT = app.getViewport();
        ENGINE = new PooledEngine();
        SHAPE_RENDERER = app.getShapeRenderer();

        ENGINE.addSystem(new DrawSystem(BATCH, VIEWPORT));
        ENGINE.addSystem(new BulletSystem(VIEWPORT));
        ENGINE.addSystem(new MovementSystem(VIEWPORT));
        ENGINE.addSystem(new CollisionSystem(app.getShapeRenderer(), VIEWPORT, true));
        ENGINE.addSystem(new SpawnerSystem(ENGINE));
        ENGINE.addSystem(new PlayerSystem(VIEWPORT));

        for (EntitySystem e : ENGINE.getSystems()) {
            e.setProcessing(true);
        }

        ENGINE.addEntity(EntityFactory.createRandomPowerUpSpawner(200, 200));
        ENGINE.addEntity(EntityFactory.createRandomPowerUpSpawner(300, 600));
        ENGINE.addEntity(EntityFactory.createPlayer());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.12F, 0.12F, 0.12F, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        ENGINE.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        VIEWPORT.update(width, height, true);
    }

    @Override
    public void dispose() {
        ENGINE.removeAllEntities();

        for (EntitySystem s : ENGINE.getSystems()) {
            ENGINE.removeSystem(s);
        }
    }
}
