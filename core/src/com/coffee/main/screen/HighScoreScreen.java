package com.coffee.main.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.components.GUIComponent;
import com.coffee.entity.systems.*;
import com.coffee.main.Application;
import com.coffee.util.Assets;
import com.coffee.util.Mapper;

/**
 * @author Phillip O'Reggio
 */
public class HighScoreScreen extends ScreenAdapter {
    private final PooledEngine ENGINE;
    private final Entity GUIEntity;

    public HighScoreScreen() {
        final FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fff.ttf"));
        final FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();

        final Application APP = (Application) Gdx.app.getApplicationListener();

        ENGINE = new PooledEngine();

        // region MainMenu entity
        final com.badlogic.gdx.scenes.scene2d.ui.Skin SKIN = Assets.MANAGER.get(Assets.UI.SKIN);
        final TextureAtlas UI_ATLAS = Assets.MANAGER.get(Assets.UI.ATLAS);

            ENGINE.addSystem(new DrawSystem(APP.getBatch(), APP.getViewport()));
            ENGINE.addSystem(new GUISystem());
            ENGINE.addSystem(new MovementSystem());
            ENGINE.addSystem(new LifetimeSystem());
            ENGINE.addSystem(new SpawnerSystem(ENGINE));

            GUIEntity = new Entity();
            final GUIComponent GUI = new GUIComponent();
            GUI.canvas = new Stage(APP.getViewport(), APP.getBatch());

            final Table TABLE = new Table();
            param.size = 25;
            param.shadowOffsetX = -4;
            param.shadowOffsetY = -4;
            param.shadowColor = Color.GRAY;
            final Label TITLE = new Label("HIGH SCORES", new Label.LabelStyle(fontGenerator.generateFont(param), Color.WHITE));
            final TextButton CANCEL = new TextButton("CANCEL", SKIN);

            TABLE.setSkin(SKIN);
            TABLE.center().pad(50).setFillParent(true);
            TABLE.add().width(APP.getViewport().getScreenWidth() / 4f);
            TABLE.add().width(APP.getViewport().getScreenWidth() / 4f);
            TABLE.row();

            TABLE.add(TITLE).padBottom(20).colspan(2).row();
            for (int i = 0; i < 10; i++) {
                TABLE.add(new Label("d e f a u l t B ", SKIN)).padBottom(20).center().uniform().fill(); //max of 16
                TABLE.add(new Label("" + 88888888, SKIN)).padBottom(20).uniform().row();
            }
            TABLE.add(CANCEL).colspan(2).fillX().pad(10, 10, 10, 10).uniform().row();
            //TABLE.debug();

            GUI.canvas.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    if (((Button) actor).isPressed()) {
                        APP.getScreen().dispose();
                        if (actor == CANCEL) {
                            APP.setScreen(new MainMenu());
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
