package com.coffee.main.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.systems.*;
import com.coffee.main.Application;
import com.coffee.util.Mapper;

/**
 * Screen where all the action and game takes place.
 */
public class GameScreen extends ScreenAdapter {
    private final SpriteBatch BATCH;
    private final Viewport VIEWPORT;
    private final ShapeRenderer SHAPE_RENDERER;
    private final PooledEngine ENGINE;

    private Entity player;
    private Entity bossShip;

    private final float READY_LENGTH = 3;
    private float readyTimer = 0; // decreased from 5 cuz impatient
    private boolean ready = false, pause = false;

    public GameScreen() {
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

        readyTimer = READY_LENGTH;
    }

    @Override
    public void render(float delta) {
        if (!ready) {
            readyTimer -= delta;

            if (readyTimer <= 0) {
                ENGINE.getSystem(PlayerSystem.class).setProcessing(true);
                ENGINE.getSystem(AISystem.class).setProcessing(true);

                ENGINE.addEntity(EntityFactory.createRandomPowerUpSpawner(200, 200, ENGINE));

                readyTimer = 0;
                ready = true;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && ready)
            togglePause();

        ENGINE.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        VIEWPORT.update(width, height, true);
    }

    public void show() {
        Application app = (Application) Gdx.app.getApplicationListener();

        EntityFactory.setEngine(ENGINE);
        app.getInputMultiplexer().addProcessor(Mapper.INPUT.get(player).PROCESSOR);
    }

    @Override
    public void hide() {
        Application app = (Application) Gdx.app.getApplicationListener();

        app.getInputMultiplexer().removeProcessor(Mapper.INPUT.get(player).PROCESSOR);
    }

    /**
     * Toggles the state of pause in the game.
     * This only turns off enough of the entity systems to consider the game "paused."
     */
    private void togglePause() {
        pause = !pause;

        ENGINE.getSystem(AISystem.class).setProcessing(pause);
        ENGINE.getSystem(BulletSystem.class).setProcessing(pause);
        ENGINE.getSystem(CollisionSystem.class).setProcessing(pause);
        ENGINE.getSystem(HealthSystem.class).setProcessing(pause);
        ENGINE.getSystem(LifetimeSystem.class).setProcessing(pause);
        ENGINE.getSystem(MovementSystem.class).setProcessing(pause);
        ENGINE.getSystem(PlayerSystem.class).setProcessing(pause);
        ENGINE.getSystem(SpawnerSystem.class).setProcessing(pause);
    }
}
