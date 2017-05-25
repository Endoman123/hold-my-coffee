package com.coffee.main.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.systems.*;
import com.coffee.main.Application;
import com.coffee.util.Mapper;

/**
 * @author Phillip O'Reggio
 */
public class PowerUpTest extends ScreenAdapter {
    private final SpriteBatch BATCH;
    private final Viewport VIEWPORT;
    private final ShapeRenderer SHAPE_RENDERER;
    private final PooledEngine ENGINE;
    private Entity player;

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
        //ENGINE.addSystem(new DebugDrawSystem(SHAPE_RENDERER, VIEWPORT));

        player = EntityFactory.createPlayer();

        ENGINE.addEntity(player);
        ENGINE.addEntity(EntityFactory.createRandomPowerUpSpawner(200, 200, ENGINE));
    }

    @Override
    public void render(float delta) {
        ENGINE.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        VIEWPORT.update(width, height, true);
    }

    public void show() {
        Application app = (Application) Gdx.app.getApplicationListener();

        app.getInputMultiplexer().addProcessor(Mapper.INPUT.get(player).PROCESSOR);
    }

    @Override
    public void hide() {
        Application app = (Application) Gdx.app.getApplicationListener();

        app.getInputMultiplexer().removeProcessor(Mapper.INPUT.get(player).PROCESSOR);
    }
}
