package com.coffee.main.screen;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.EntityBuilder;
import com.coffee.entity.systems.*;
import com.coffee.main.Application;

/**
 * Test to see if the player is controllable.
 *
 * @author Jared Tulayan
 */
public class PlayerTest extends ScreenAdapter {
    private final SpriteBatch BATCH;
    private final Viewport VIEWPORT;
    private final PooledEngine ENGINE;

    public PlayerTest() {
        Application app = (Application) Gdx.app.getApplicationListener();

        BATCH = app.getBatch();
        VIEWPORT = app.getViewport();
        ENGINE = new PooledEngine();

        ENGINE.addSystem(new DebugDrawSystem(app.getShapeRenderer(), VIEWPORT));
        ENGINE.addSystem(new MovementSystem(VIEWPORT));
        ENGINE.addSystem(new DrawSystem(BATCH, VIEWPORT));
        ENGINE.addSystem(new CollisionSystem(app.getShapeRenderer(), VIEWPORT, true));
        ENGINE.addSystem(new PlayerSystem(VIEWPORT));

        for (EntitySystem e : ENGINE.getSystems()) {
            e.setProcessing(true);
        }

        ENGINE.addEntity(EntityBuilder.createPlayer());
    }

    @Override
    public void render(float delta) {
        VIEWPORT.apply(true);

        ENGINE.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        ENGINE.removeAllEntities();

        for (EntitySystem s : ENGINE.getSystems()) {
            ENGINE.removeSystem(s);
        }
    }
}
