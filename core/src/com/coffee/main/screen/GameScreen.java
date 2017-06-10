package com.coffee.main.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.components.GUIComponent;
import com.coffee.entity.systems.*;
import com.coffee.main.Application;
import com.coffee.util.Assets;
import com.coffee.util.Mapper;

/**
 * Screen where all the action and game takes place.
 */
public class GameScreen extends ScreenAdapter {
    private final SpriteBatch BATCH;
    private final Viewport VIEWPORT;
    private final ShapeRenderer SHAPE_RENDERER;
    private final PooledEngine ENGINE;
    private final Application APP;

    private final Entity PLAYER;
    private final Entity BOSS_SHIP;
    private final Entity PAUSE_UI;

    private final float READY_LENGTH = 3; // decreased from 5 cuz impatient
    private float gameTimer;
    private boolean ready = false, pause = false, gameOver = false;

    public GameScreen() {
        APP = (Application) Gdx.app.getApplicationListener();

        BATCH = APP.getBatch();
        VIEWPORT = APP.getViewport();
        ENGINE = new PooledEngine();
        SHAPE_RENDERER = APP.getShapeRenderer();

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

        PLAYER = EntityFactory.createPlayer(VIEWPORT.getWorldWidth() / 2f, 128);
        BOSS_SHIP = EntityFactory.createBossShip(VIEWPORT.getWorldWidth() / 2, VIEWPORT.getWorldHeight() * 2 / 3 + 64);

        // region Initialize UI
        PAUSE_UI = new Entity();
        final Skin SKIN = Assets.MANAGER.get(Assets.UI.SKIN);
        final GUIComponent UI = new GUIComponent();
        final Table
                GAME_OVER_DISPLAY = new Table(),
                PAUSE_DISPLAY = new Table();

        final Label PAUSE_ID = new Label("PAUSED", SKIN, "title");
        final Button
                RESUME = new TextButton("RESUME", SKIN),
                QUIT = new TextButton("QUIT", SKIN);

        UI.canvas = new Stage(VIEWPORT, BATCH);

        PAUSE_ID.setAlignment(Align.center);

        PAUSE_DISPLAY.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (RESUME.isPressed())
                    togglePause();

                if (QUIT.isPressed()) {
                    APP.getScreen().dispose();
                    APP.setScreen(new MainMenu());
                }
            }
        });

        PAUSE_DISPLAY.center().pad(200).setFillParent(true);
        PAUSE_DISPLAY.setSkin(SKIN);
        PAUSE_DISPLAY.add(PAUSE_ID).expandX().fillX().row();
        PAUSE_DISPLAY.add(RESUME).expandX().fillX().padTop(10).row();
        PAUSE_DISPLAY.add(QUIT).expandX().fillX().padTop(10).row();

        UI.canvas.addActor(PAUSE_DISPLAY);

        PAUSE_UI.add(UI);
        // endregion

        ENGINE.addEntity(PLAYER);
        ENGINE.addEntity(BOSS_SHIP);

        ENGINE.addEntity(EntityFactory.createParticleGenerator());

        ENGINE.getSystem(PlayerSystem.class).setProcessing(false);
        ENGINE.getSystem(AISystem.class).setProcessing(false);

        gameTimer = READY_LENGTH;
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void render(float delta) {
        if (!ready) {
            gameTimer -= delta;

            if (gameTimer <= 0) {
                ENGINE.getSystem(PlayerSystem.class).setProcessing(true);
                ENGINE.getSystem(AISystem.class).setProcessing(true);

                ENGINE.addEntity(EntityFactory.createRandomPowerUpSpawner(200, 200, ENGINE));

                gameTimer = 0;
                ready = true;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && ready)
            togglePause();

        if (Gdx.input.isKeyJustPressed(Input.Keys.K) && !gameOver) {
            Mapper.PLAYER.get(PLAYER).lives = 0;
            Mapper.HEALTH.get(PLAYER).health = 0;
        }

        gameOver = Mapper.HEALTH.get(PLAYER).getHealthPercent() == 0 && Mapper.PLAYER.get(PLAYER).lives == 0 || Mapper.HEALTH.get(BOSS_SHIP).getHealthPercent() == 0;

        if (gameOver) {
            if (gameTimer == 0) {
                System.out.println("Game Over!");
                gameTimer = 3;
            } else {
                gameTimer -= delta;
                if (gameTimer <= 0) {
                    APP.getScreen().dispose();
                    APP.setScreen(new MainMenu());
                }
            }
        }

        ENGINE.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        VIEWPORT.update(width, height, true);
    }

    public void show() {
        EntityFactory.setEngine(ENGINE);
        APP.getInputMultiplexer().addProcessor(Mapper.INPUT.get(PLAYER).PROCESSOR);
    }

    @Override
    public void hide() {
        Gdx.input.setCursorCatched(false);
        APP.getInputMultiplexer().removeProcessor(Mapper.INPUT.get(PLAYER).PROCESSOR);
    }

    /**
     * Toggles the state of pause in the game.
     * This only turns off enough of the entity systems to consider the game "paused."
     */
    private void togglePause() {
        pause = !pause;

        Gdx.input.setCursorCatched(!pause);
        ENGINE.getSystem(AISystem.class).setProcessing(!pause);
        ENGINE.getSystem(BulletSystem.class).setProcessing(!pause);
        ENGINE.getSystem(CollisionSystem.class).setProcessing(!pause);
        ENGINE.getSystem(HealthSystem.class).setProcessing(!pause);
        ENGINE.getSystem(LifetimeSystem.class).setProcessing(!pause);
        ENGINE.getSystem(MovementSystem.class).setProcessing(!pause);
        ENGINE.getSystem(PlayerSystem.class).setProcessing(!pause);
        ENGINE.getSystem(SpawnerSystem.class).setProcessing(!pause);

        if (pause) {
            final Stage INPUT = Mapper.GUI.get(PAUSE_UI).canvas;
            APP.getInputMultiplexer().addProcessor(INPUT);
            ENGINE.addEntity(PAUSE_UI);
        } else {
            final InputProcessor INPUT = Mapper.GUI.get(PAUSE_UI).canvas;
            APP.getInputMultiplexer().removeProcessor(INPUT);
            ENGINE.removeEntity(PAUSE_UI);
        }
    }

    @Override
    public void dispose() {
        ENGINE.removeAllEntities();

        super.dispose();
    }
}
