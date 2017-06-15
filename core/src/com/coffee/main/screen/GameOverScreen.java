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
import com.coffee.entity.EntityFactory;
import com.coffee.entity.components.GUIComponent;
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
    private final Entity GUIEntity;

    public GameOverScreen(PlayerComponent player) {
        final FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fff.ttf"));
        final FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        final Application APP = (Application) Gdx.app.getApplicationListener();

        ENGINE = new PooledEngine();

        ENGINE.addSystem(new DrawSystem(APP.getBatch(), APP.getViewport()));
        ENGINE.addSystem(new GUISystem());
        ENGINE.addSystem(new MovementSystem());
        ENGINE.addSystem(new LifetimeSystem());
        ENGINE.addSystem(new SpawnerSystem(ENGINE));

        // region MainMenu entity
        final Skin SKIN = Assets.MANAGER.get(Assets.UI.SKIN);
        final TextureAtlas UI_ATLAS = Assets.MANAGER.get(Assets.UI.ATLAS);
        final GUIComponent GUI = new GUIComponent();
        final Table TABLE = new Table();
        final BitmapFont FNT_GAME_OVER, FNT_SCORE;

        GUIEntity = new Entity();

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
            SCORE_ID = new Label("SCORE: ", new Label.LabelStyle(FNT_SCORE, Color.WHITE)),
            SCORE = new Label("" + player.score, SKIN);

        final TextButton CONTINUE = new TextButton("CONTINUE", SKIN);

        int finalScore = player.score;

        TABLE.setSkin(SKIN);
        TABLE.pad(50, 100, 50, 100).setFillParent(true);
        TABLE.center().pad(50).setFillParent(true);
        TABLE.add(TITLE).padBottom(20).row();
        TABLE.add(SCORE).pad(10, 10, 10, 10).row();

        if (finalScore > HighScore.getLowest().getScore()) {
            final TextField INPUT = new TextField("", SKIN);
            INPUT.setMaxLength(12);

            INPUT.setColor(Color.RED);
            CONTINUE.setDisabled(true);

            TABLE.add(INPUT).colspan(2).fillX().pad(10, 10, 10, 10).uniform().row();

            INPUT.setTextFieldListener((TextField textField, char c) -> {
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
                        String playerName = INPUT.getText().trim();
                        HighScore.insert(new HighScoreEntry(finalScore, playerName));
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

        TABLE.add(CONTINUE).colspan(2).fillX().pad(10, 10, 10, 10).uniform().row();

        GUI.canvas.addActor(TABLE);
        GUIEntity.add(GUI);
        // endregion

        ENGINE.addEntity(GUIEntity);
        ENGINE.addEntity(EntityFactory.createParticleGenerator());

        fontGenerator.dispose();
    }

    @Override
    public void render(float deltaTime) {
        ENGINE.update(deltaTime);
    }

    @Override
    public void show() {
        Application app = (Application) Gdx.app.getApplicationListener();

        app.getInputMultiplexer().addProcessor(Mapper.GUI.get(GUIEntity).canvas);
        EntityFactory.setEngine(ENGINE);
    }

    @Override
    public void hide() {
        Application app = (Application) Gdx.app.getApplicationListener();

        app.getInputMultiplexer().removeProcessor(Mapper.GUI.get(GUIEntity).canvas);
    }

    @Override
    public void dispose() {

    }
}
