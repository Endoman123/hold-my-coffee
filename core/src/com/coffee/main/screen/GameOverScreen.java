package com.coffee.main.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.components.GUIComponent;
import com.coffee.entity.components.HealthComponent;
import com.coffee.entity.components.PlayerComponent;
import com.coffee.entity.systems.*;
import com.coffee.main.Application;
import com.coffee.util.Assets;
import com.coffee.util.HighScore;
import com.coffee.util.HighScoreEntry;
import com.coffee.util.Mapper;

/**
 * @author Phillip O'Reggio
 */
public class GameOverScreen extends ScreenAdapter {
    private final PooledEngine ENGINE;
    private final Entity GUI_ENTITY, PLAYER_ENTITY;
    private final Application APP;

    public GameOverScreen(Entity e) {
        APP = (Application) Gdx.app.getApplicationListener();

        ENGINE = new PooledEngine();

        ENGINE.addSystem(new DrawSystem(APP.getBatch(), APP.getViewport()));
        ENGINE.addSystem(new GUISystem());
        ENGINE.addSystem(new MovementSystem());
        ENGINE.addSystem(new LifetimeSystem());
        ENGINE.addSystem(new SpawnerSystem(ENGINE));

        GUI_ENTITY = new Entity();
        PLAYER_ENTITY = e;
        generateUI();

        ENGINE.addEntity(GUI_ENTITY);
        ENGINE.addEntity(EntityFactory.createParticleGenerator());
    }

    private void generateUI() {
        final Skin SKIN = Assets.MANAGER.get(Assets.UI.SKIN);
        final TextureAtlas UI_ATLAS = Assets.MANAGER.get(Assets.UI.ATLAS);
        final GUIComponent GUI = new GUIComponent();
        final Table TABLE = new Table();
        final BitmapFont FNT_GAME_OVER, FNT_SCORE;
        final FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fff.ttf"));
        final FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        final PlayerComponent PLAYER = Mapper.PLAYER.get(PLAYER_ENTITY);
        final HealthComponent HEALTH = Mapper.HEALTH.get(PLAYER_ENTITY);

        GUI.canvas = new Stage(APP.getViewport(), APP.getBatch());

        param.size = 25;
        param.shadowOffsetX = -4;
        param.shadowOffsetY = -4;
        param.shadowColor = Color.GRAY;

        FNT_GAME_OVER = fontGenerator.generateFont(param);

        param.size = 20;
        param.shadowOffsetX = 0;
        param.shadowOffsetY = 0;
        param.color = Color.CYAN;

        FNT_SCORE = fontGenerator.generateFont(param);

        final Label
            TITLE = new Label("GAME OVER", new Label.LabelStyle(FNT_GAME_OVER, Color.WHITE)),
            LBL_SCORE_ID = new Label("SCORE: ", SKIN),
            SCORE = new Label("" + PLAYER.score, SKIN);

        final TextButton CONTINUE = new TextButton("CONTINUE", SKIN);
        final int FINAL_SCORE;
        int score = PLAYER.score;

        LBL_SCORE_ID.setAlignment(Align.left);
        SCORE.setAlignment(Align.right);

        TABLE.setSkin(SKIN);
        TABLE.center().pad(100).setFillParent(true);
        TABLE.add(TITLE).padBottom(20).colspan(2).row();
        TABLE.add(LBL_SCORE_ID).padBottom(10).expandX().fillX().align(Align.left);
        TABLE.add(SCORE).padBottom(10).uniform().expandX().fillX().align(Align.right).row();

        if (PLAYER.lives > 0 || HEALTH.getHealthPercent() > 0) {
            final Label
                LBL_HEALTH_BONUS_ID = new Label("HEALTH BONUS: ", SKIN),
                LBL_HEALTH_BONUS = new Label("", SKIN),
                LBL_ACCURACY_BONUS_ID = new Label("ACCURACY BONUS: ", SKIN),
                LBL_ACCURACY_BONUS = new Label("", SKIN),
                LBL_TIME_BONUS_ID = new Label("TIME BONUS: ", SKIN),
                LBL_TIME_BONUS = new Label("", SKIN),
                LBL_FINAL_SCORE_ID = new Label("FINAL SCORE: ", SKIN),
                LBL_FINAL_SCORE = new Label("", SKIN);

            final int HEALTH_BONUS_SCORE = PLAYER.lives * 100 + HEALTH.health;
            final float ACCURACY_BONUS_FACTOR = 1 + PLAYER.getAccuracy();
            final int TIME_BONUS;

            if (PLAYER.timeAlive <= 240)
                TIME_BONUS = 4000;
            else if (PLAYER.timeAlive <= 360)
                TIME_BONUS = 3000;
            else if (PLAYER.timeAlive <= 480)
                TIME_BONUS = 2000;
            else if (PLAYER.timeAlive <= 540)
                TIME_BONUS = 1000;
            else
                TIME_BONUS = 0;

            LBL_HEALTH_BONUS.setText("+" + HEALTH_BONUS_SCORE);
            LBL_ACCURACY_BONUS.setText(String.format("%.2f", PLAYER.getAccuracy() * 100f) + "% | x" + String.format("%.2f", ACCURACY_BONUS_FACTOR));
            LBL_TIME_BONUS.setText(String.format("%02d", PLAYER.timeAlive / 60) + ":" + String.format("%02d", PLAYER.timeAlive % 60) + " | +" + TIME_BONUS);

            LBL_HEALTH_BONUS_ID.setAlignment(Align.left);
            LBL_ACCURACY_BONUS_ID.setAlignment(Align.left);
            LBL_TIME_BONUS_ID.setAlignment(Align.left);
            LBL_FINAL_SCORE_ID.setAlignment(Align.left);
            LBL_HEALTH_BONUS.setAlignment(Align.right);
            LBL_ACCURACY_BONUS.setAlignment(Align.right);
            LBL_TIME_BONUS.setAlignment(Align.right);
            LBL_FINAL_SCORE.setAlignment(Align.right);

            score += HEALTH_BONUS_SCORE;
            score *= ACCURACY_BONUS_FACTOR;

            if (score > HighScore.get(HighScore.SIZE - 1).getScore())
                LBL_FINAL_SCORE_ID.setText("NEW HIGH SCORE: ");

            LBL_FINAL_SCORE.setText("" + score);

            TABLE.add(LBL_HEALTH_BONUS_ID).expandX().fillX().align(Align.left);
            TABLE.add(LBL_HEALTH_BONUS).expandX().fillX().align(Align.right).row();
            TABLE.add(LBL_ACCURACY_BONUS_ID).expandX().fillX().align(Align.left);
            TABLE.add(LBL_ACCURACY_BONUS).expandX().fillX().align(Align.right).row();
            TABLE.add(LBL_TIME_BONUS_ID).expandX().fillX().align(Align.left);
            TABLE.add(LBL_TIME_BONUS).expandX().fillX().align(Align.right).row();
            TABLE.add(LBL_FINAL_SCORE_ID).padTop(10).expandX().fillX().align(Align.left);
            TABLE.add(LBL_FINAL_SCORE).padTop(10).expandX().fillX().align(Align.right).row();
        } else {
            if (score > HighScore.get(HighScore.SIZE - 1).getScore())
                LBL_SCORE_ID.setText("NEW HIGH SCORE: ");
        }

        FINAL_SCORE = score;

        if (FINAL_SCORE > HighScore.getLowestNonZero().getScore()) {
            final Label LBL_NAME_ID = new Label("ENTER NAME: ", new Label.LabelStyle(FNT_SCORE, Color.WHITE));
            final TextField NAME = new TextField("", SKIN);

            NAME.setMaxLength(12);

            NAME.setColor(Color.RED);
            CONTINUE.setDisabled(true);

            TABLE.add(LBL_NAME_ID).pad(20, 10, 10, 10).expandX().fillX();
            TABLE.add(NAME).pad(20, 10, 10, 10).expandX().fillX().uniform().row();

            NAME.setTextFieldListener((TextField textField, char c) -> {
                String test = textField.getText().trim();
                if (test.length() == 0) {
                    textField.setColor(Color.RED);
                    CONTINUE.setDisabled(true);
                } else {
                    for (int j = 0; j < HighScore.SIZE; j++) {
                        String name = HighScore.get(j).getName();
                        if (name.equals(test.trim())) {
                            textField.setColor(Color.RED);
                            CONTINUE.setDisabled(true);
                            return;
                        }
                    }

                    textField.setColor(Color.WHITE);
                    CONTINUE.setDisabled(false);
                }
            });

            CONTINUE.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (CONTINUE.isPressed()) {
                        // Put score in
                        String playerName = NAME.getText().trim();
                        HighScore.insert(new HighScoreEntry(FINAL_SCORE, playerName));
                        HighScore.save();

                        // Change to high score screen
                        APP.getScreen().dispose();
                        APP.setScreen(new HighScoreScreen());
                    }
                }
            });
        } else {
            CONTINUE.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (CONTINUE.isPressed()) {
                        // Change back to main menu
                        APP.getScreen().dispose();
                        APP.setScreen(new MainMenu());
                    }
                }
            });
        }

        TABLE.add(CONTINUE).colspan(2).fillX().pad(20, 10, 10, 10).uniform().row();

        GUI.canvas.addActor(TABLE);
        GUI_ENTITY.add(GUI);
       // TABLE.setDebug(true);
        // endregion

        fontGenerator.dispose();
    }

    @Override
    public void render(float deltaTime) {
        ENGINE.update(deltaTime);
    }

    @Override
    public void show() {
        Application app = (Application) Gdx.app.getApplicationListener();

        app.getInputMultiplexer().addProcessor(Mapper.GUI.get(GUI_ENTITY).canvas);
        EntityFactory.setEngine(ENGINE);
    }

    @Override
    public void hide() {
        Application app = (Application) Gdx.app.getApplicationListener();

        app.getInputMultiplexer().removeProcessor(Mapper.GUI.get(GUI_ENTITY).canvas);
    }

    @Override
    public void dispose() {

    }
}
