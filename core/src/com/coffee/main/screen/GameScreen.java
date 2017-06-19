package com.coffee.main.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.components.GUIComponent;
import com.coffee.entity.components.PlayerComponent;
import com.coffee.entity.systems.*;
import com.coffee.main.Application;
import com.coffee.util.Assets;
import com.coffee.util.HighScore;
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

    private final InputProcessor DEBUG;

    private final Entity PLAYER;
    private final Entity BOSS_SHIP;
    private final Entity PAUSE_UI;

    private final float READY_LENGTH = 3; // decreased from 5 cuz impatient
    private float gameTimer;
    private int lowestHighScore;
    private boolean ready = false, pause = false, gameOver = false;

    public GameScreen() {
        APP = (Application) Gdx.app.getApplicationListener();

        BATCH = APP.getBatch();
        VIEWPORT = APP.getViewport();
        ENGINE = new PooledEngine();
        SHAPE_RENDERER = APP.getShapeRenderer();

        ENGINE.addSystem(new SpawnerSystem(ENGINE));
        ENGINE.addSystem(new PlayerSystem(VIEWPORT));
        ENGINE.addSystem(new AISystem(VIEWPORT));
        ENGINE.addSystem(new MovementSystem(VIEWPORT));
        ENGINE.addSystem(new DrawSystem(BATCH, VIEWPORT));
        ENGINE.addSystem(new DebugDrawSystem(SHAPE_RENDERER, VIEWPORT));
        ENGINE.addSystem(new GUISystem());
        ENGINE.addSystem(new HealthSystem());
        ENGINE.addSystem(new LifetimeSystem());
        ENGINE.addSystem(new BulletSystem(VIEWPORT));
        ENGINE.addSystem(new CollisionSystem(VIEWPORT));

        PLAYER = EntityFactory.createPlayer(VIEWPORT.getWorldWidth() / 2f, 128);
        BOSS_SHIP = EntityFactory.createBossShip(VIEWPORT.getWorldWidth() / 2, VIEWPORT.getWorldHeight() * 2 / 3 + 64);

        // region Initialize UI
        PAUSE_UI = new Entity();
        final Skin SKIN = Assets.MANAGER.get(Assets.UI.SKIN);
        final GUIComponent UI = new GUIComponent();
        final Table PAUSE_DISPLAY = new Table();

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

        PAUSE_DISPLAY.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                PAUSE_DISPLAY.setVisible(pause);

                return false;
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

        lowestHighScore = HighScore.getLowestNonZero().getScore();

        ENGINE.addEntity(PLAYER);
        ENGINE.addEntity(BOSS_SHIP);
        ENGINE.addEntity(PAUSE_UI);

        ENGINE.addEntity(EntityFactory.createParticleGenerator());

        ENGINE.getSystem(PlayerSystem.class).setProcessing(false);
        ENGINE.getSystem(AISystem.class).setProcessing(false);
        ENGINE.getSystem(DebugDrawSystem.class).setProcessing(false);

        gameTimer = READY_LENGTH;

        Gdx.input.setCursorCatched(true);
        DEBUG = new MLGHackerzDebugControlzz();
    }

    @Override
    public void render(float delta) {
        if (!ready) {
            gameTimer -= delta;

            if (gameTimer <= 0) {
                ENGINE.getSystem(PlayerSystem.class).setProcessing(true);
                ENGINE.getSystem(AISystem.class).setProcessing(true);

                ENGINE.addEntity(EntityFactory.createRandomPowerUpSpawner(200, 200, ENGINE));

                gameTimer = 3;
                ready = true;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && ready && !gameOver)
            togglePause();

        ENGINE.update(delta);

        boolean
            playerDead = Mapper.HEALTH.get(PLAYER).getHealthPercent() == 0 && Mapper.PLAYER.get(PLAYER).lives == 0,
            bossDead = !ENGINE.getEntities().contains(BOSS_SHIP, true);

        gameOver = playerDead || bossDead;

        if (gameOver) {
            gameTimer -= delta;
            stopPlayer();
            //APP.getInputMultiplexer().removeProcessor(DEBUG);

            if (playerDead)
                Mapper.HEALTH.get(BOSS_SHIP).invincibilityTimer = 999;
            else if (bossDead)
                Mapper.HEALTH.get(PLAYER).invincibilityTimer = 999;

            if (gameTimer <= 0) {
                this.dispose();
                APP.setScreen(new GameOverScreen(PLAYER));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        VIEWPORT.update(width, height, true);
    }

    public void show() {
        EntityFactory.setEngine(ENGINE);
        APP.getInputMultiplexer().addProcessor(Mapper.INPUT.get(PLAYER).PROCESSOR);
        //APP.getInputMultiplexer().addProcessor(DEBUG);

    }

    @Override
    public void hide() {
        Gdx.input.setCursorCatched(false);
        APP.getInputMultiplexer().removeProcessor(Mapper.INPUT.get(PLAYER).PROCESSOR);
        //APP.getInputMultiplexer().removeProcessor(DEBUG);
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

        final InputProcessor INPUT = Mapper.GUI.get(PAUSE_UI).canvas;

        if (pause)
            APP.getInputMultiplexer().addProcessor(INPUT);
        else
            APP.getInputMultiplexer().removeProcessor(INPUT);
    }

    /**
     * Disables the player by setting its inputs to "disabled."
     * Good for when the game ends and you just want them to stop moving/shooting
     */
    private void stopPlayer() {
        PlayerComponent player = Mapper.PLAYER.get(PLAYER);

        APP.getInputMultiplexer().removeProcessor(Mapper.INPUT.get(PLAYER).PROCESSOR);
        player.shoot = false;
        player.up = player.down = player.left = player.right = 0;
    }

    @Override
    public void dispose() {
        ENGINE.removeAllEntities();

        super.dispose();
    }

    /**
     * Class that only world class players use. Contains hotkeys for debugging purposes.
     */
    private class MLGHackerzDebugControlzz extends InputAdapter {
        @Override
        public boolean keyUp(int keycode) {
            PlayerComponent PLAY = Mapper.PLAYER.get(PLAYER);
            switch (keycode) {
                case Input.Keys.F1:
                    PLAY.lives++;
                    System.out.println("Lives +1");
                    break;
                case Input.Keys.F2:
                    PLAY.upSpeed = MathUtils.clamp(PLAY.upSpeed + 1, 0, 4);
                    System.out.println("Speed +1");
                    break;
                case Input.Keys.F3:
                    PLAY.upFireRate = MathUtils.clamp(PLAY.upFireRate + 1, 0, 4);
                    System.out.println("Fire Rate +1");
                    break;
                case Input.Keys.F4:
                    PLAY.upBulletDamage = MathUtils.clamp(PLAY.upBulletDamage + 1, 0, 4);
                    System.out.println("Bullet Damage +1");
                    break;
                case Input.Keys.F5:
                    Mapper.HEALTH.get(PLAYER).health = Mapper.HEALTH.get(PLAYER).maxHealth;
                    System.out.println("Health Restored");
                    break;
                case Input.Keys.F6: //EVERYTHING UP OOOO OOOOOO OOOOOOO TODO 000OOO000OOOO000OOooo MAXXXX XXXXXXXXXXXxxxxxxxxxx.
                    if (Mapper.HEALTH.get(PLAYER).getHealthPercent() > 0 || Mapper.PLAYER.get(PLAYER).lives > 0) {
                        PLAY.upBulletDamage = 4;
                        PLAY.upFireRate = 4;
                        PLAY.upSpeed = 4;
                        PLAY.lives = 999;
                        Mapper.HEALTH.get(PLAYER).health = Mapper.HEALTH.get(PLAYER).maxHealth;
                        System.out.println("Max Stats");
                    }
                    break;
                case Input.Keys.F7: //DeBug Draw System Toggle
                    ENGINE.getSystem(DebugDrawSystem.class).setProcessing(!ENGINE.getSystem(DebugDrawSystem.class).checkProcessing());
                    System.out.println("Debug View Toggled");
                    break;
                case Input.Keys.F8: //DeBug Draw System Toggle + QuadTree
                    ENGINE.getSystem(DebugDrawSystem.class).setProcessing(!ENGINE.getSystem(DebugDrawSystem.class).checkProcessing());
                    ENGINE.getSystem(DebugDrawSystem.class).setDrawQuadTree(true);
                    System.out.println("Debug View + QuadTree Toggled");
                    break;
                case Input.Keys.F9: //Kill player in their sleep
                    PLAY.lives = 0;
                    Mapper.HEALTH.get(PLAYER).health = 0;
                    System.out.println("Kill Player");
                    break;
                case Input.Keys.NUM_1: // (100% - 75%) 80%
                    Mapper.HEALTH.get(BOSS_SHIP).health = (int)(Mapper.HEALTH.get(BOSS_SHIP).maxHealth * 0.8);
                    break;
                case Input.Keys.NUM_2: // (75% - 50%) 60%
                    Mapper.HEALTH.get(BOSS_SHIP).health = (int)(Mapper.HEALTH.get(BOSS_SHIP).maxHealth * 0.6);
                    break;
                case Input.Keys.NUM_3: // (50% - 25%) 40%
                    Mapper.HEALTH.get(BOSS_SHIP).health = (int)(Mapper.HEALTH.get(BOSS_SHIP).maxHealth * 0.4);
                    break;
                case Input.Keys.NUM_4: // (>25%) 20%
                    Mapper.HEALTH.get(BOSS_SHIP).health = (int)(Mapper.HEALTH.get(BOSS_SHIP).maxHealth * 0.2);
                    break;
                case Input.Keys.NUM_5 : //0%
                    Mapper.HEALTH.get(BOSS_SHIP).health = 0;
                    break;
                default:
                    return false;
            }
            PLAY.score = 1000000;
            return true;
        }
    }
}
