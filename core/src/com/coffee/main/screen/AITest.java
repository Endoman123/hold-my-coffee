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
public class AITest extends ScreenAdapter {
    private final SpriteBatch BATCH;
    private final Viewport VIEWPORT;
    private final ShapeRenderer SHAPE_RENDERER;
    private final PooledEngine ENGINE;

    private Entity player;
    private Entity bossShip;

    private float readyTimer = 5;
    private boolean ready = false;

    public AITest() {
        Application app = (Application) Gdx.app.getApplicationListener();

        BATCH = app.getBatch();
        VIEWPORT = app.getViewport();
        ENGINE = new PooledEngine();
        SHAPE_RENDERER = app.getShapeRenderer();

        ENGINE.addSystem(new DrawSystem(BATCH, VIEWPORT));
        ENGINE.addSystem(new GUISystem());
        ENGINE.addSystem(new BulletSystem(VIEWPORT));
        ENGINE.addSystem(new MovementSystem(VIEWPORT));
        ENGINE.addSystem(new CollisionSystem(VIEWPORT));
        // ENGINE.addSystem(new DebugDrawSystem(SHAPE_RENDERER, VIEWPORT));
        ENGINE.addSystem(new LifetimeSystem());
        ENGINE.addSystem(new HealthSystem());
        ENGINE.addSystem(new SpawnerSystem(ENGINE));
        ENGINE.addSystem(new PlayerSystem(VIEWPORT));
        ENGINE.addSystem(new AISystem(VIEWPORT));

        player = EntityFactory.createPlayer(VIEWPORT.getWorldWidth() / 2f, 128);
        bossShip = EntityFactory.createBossShip(VIEWPORT.getWorldWidth() / 2, VIEWPORT.getWorldHeight() * 2 / 3 + 64);

        ENGINE.addEntity(player);
        ENGINE.addEntity(bossShip);

        ENGINE.addEntity(EntityFactory.createParticleGenerator());

        ENGINE.getSystem(PlayerSystem.class).setProcessing(false);
        ENGINE.getSystem(AISystem.class).setProcessing(false);
    }

    @Override
    public void render(float delta) {
        if (readyTimer <= 0 && !ready) {
            ENGINE.getSystem(PlayerSystem.class).setProcessing(true);
            ENGINE.getSystem(AISystem.class).setProcessing(true);

            ENGINE.addEntity(EntityFactory.createRandomPowerUpSpawner(200, 200, ENGINE));
            ready = true;
        } else {
            readyTimer -= delta;
        }

        ENGINE.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        VIEWPORT.update(width, height, true);
    }

    public void show() {
        Application app = (Application) Gdx.app.getApplicationListener();

        EntityFactory.setPooledEngine(ENGINE);
        app.getInputMultiplexer().addProcessor(Mapper.INPUT.get(player).PROCESSOR);
    }

    @Override
    public void hide() {
        Application app = (Application) Gdx.app.getApplicationListener();

        app.getInputMultiplexer().removeProcessor(Mapper.INPUT.get(player).PROCESSOR);
    }
}
