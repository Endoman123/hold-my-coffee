package com.coffee.main.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.components.GUIComponent;
import com.coffee.entity.systems.*;
import com.coffee.main.Application;
import com.coffee.util.Assets;
import com.coffee.util.HighScoreEntry;
import com.coffee.util.Mapper;

/**
 * @author Phillip O'Reggio
 */
public class ScoreEntryScreen extends ScreenAdapter {
    private final PooledEngine ENGINE;
    private final Entity GUIEntity;

    public ScoreEntryScreen(int playerPoints) {
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

        GUIEntity = new Entity();

        GUI.canvas = new Stage(APP.getViewport(), APP.getBatch());

        param.size = 25;
        param.shadowOffsetX = -4;
        param.shadowOffsetY = -4;
        param.shadowColor = Color.GRAY;

        final Label TITLE = new Label("NEW HIGH SCORE", new Label.LabelStyle(fontGenerator.generateFont(param), Color.WHITE));
        final TextField INPUT = new TextField("", SKIN);
        INPUT.setTextFieldListener((TextField textField, char c) -> {
            if (textField.getText().length() >= 12)
                textField.setColor(Color.RED);
            else
                textField.setColor(Color.WHITE);
        });
        final TextButton OK = new TextButton("OK", SKIN);

        TABLE.setSkin(SKIN);
        TABLE.center().pad(50).setFillParent(true);
        TABLE.add(TITLE).padBottom(20).row();
        TABLE.add(INPUT).colspan(2).fillX().pad(10, 10, 10, 10).uniform().row();
        TABLE.add(OK).colspan(2).fillX().pad(10, 10, 10, 10).uniform().row();

        GUI.canvas.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (actor instanceof Button && ((Button) actor).isPressed()) {
                    if (actor == OK) {
                        if (actor == OK) {
                            Json JSON = new Json();
                            Array<HighScoreEntry> scores;
                            final FileHandle SCORE_FILE = Gdx.files.local("hold_my_coffee_highscores.json");

                            //Get scores
                            scores = (SCORE_FILE.exists()) ? JSON.fromJson(Array.class, SCORE_FILE.readString()) : new Array();
                            if (scores == null) scores = new Array();

                            //Check if the name inputted is already there
                            for (HighScoreEntry score : scores)
                                if (score.getName().equals(INPUT.getText()))
                                    return; //TODO tell user name is taken Boi

                            //Put score in
                            String playerName = (INPUT.getText().length() >= 12)? INPUT.getText().substring(0, 12) : INPUT.getText();
                            scores.add(new HighScoreEntry(playerPoints, playerName));
                            scores.sort();
                            scores.reverse();
                            scores.truncate(10);


                            //scores.truncate(10);

                            //Write changes to high score to file
                            SCORE_FILE.writeString(JSON.prettyPrint(scores), false);

                            APP.getScreen().dispose();

                            APP.setScreen(new HighScoreScreen());
                        }
                    }
                }
            }
        });

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
