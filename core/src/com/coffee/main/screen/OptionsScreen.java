package com.coffee.main.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.components.GUIComponent;
import com.coffee.entity.systems.*;
import com.coffee.main.Application;
import com.coffee.util.Assets;
import com.coffee.util.Mapper;
import com.coffee.util.OptionsManager;

/**
 * @author Jared Tulayan
 */
public class OptionsScreen extends ScreenAdapter {
    private final PooledEngine ENGINE;
    private final Entity GUIEntity;

    public OptionsScreen() {
        final Application APP = (Application) Gdx.app.getApplicationListener();

        ENGINE = new PooledEngine();

        // region Menu entity
        final Skin SKIN = Assets.MANAGER.get(Assets.UI.SKIN);
        final TextureAtlas UI_ATLAS = Assets.MANAGER.get(Assets.UI.ATLAS);

        ENGINE.addSystem(new DrawSystem(APP.getBatch(), APP.getViewport()));
        ENGINE.addSystem(new GUISystem());
        ENGINE.addSystem(new MovementSystem());
        ENGINE.addSystem(new LifetimeSystem());
        ENGINE.addSystem(new SpawnerSystem(ENGINE));

        GUIEntity = new Entity();
        final GUIComponent GUI = new GUIComponent();
        GUI.canvas = new Stage(APP.getViewport(), APP.getBatch());

        final Table
                TABLE = new Table(),
                OPTIONS = new Table();
        final Label
                TITLE = new Label("OPTIONS", SKIN, "title"),
                RES_ID = new Label("Resolution", SKIN);
        final TextButton
                CANCEL = new TextButton("CANCEL", SKIN),
                SAVE = new TextButton("SAVE", SKIN);
        final SelectBox<String> RES = new SelectBox<String>(SKIN);

        TABLE.setSkin(SKIN);
        TITLE.setAlignment(Align.center);

        RES.setItems(
                "1920x1080",
                "1280x720"
        );

        RES.setSelected(OptionsManager.resolution);

        TABLE.center().pad(50).setFillParent(true);
        TABLE.add(TITLE).expandX().fillX().colspan(2).row();
        TABLE.add(OPTIONS).expand().fill().colspan(2).pad(10).row();
        TABLE.add(SAVE).expandX().fillX().padRight(5).colspan(1).uniform();
        TABLE.add(CANCEL).expandX().fillX().padLeft(5).colspan(1).uniform();

        OPTIONS.top();
        OPTIONS.add(RES_ID).colspan(1).expandX().fill().align(Align.left);
        OPTIONS.add(RES).colspan(1).expandX().fill().align(Align.right);

        CANCEL.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((Button)actor).isPressed()) {
                    APP.setScreen(new MainMenu());
                    APP.getScreen().dispose();
                }
            }
        });

        SAVE.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((Button)actor).isPressed()) {
                    OptionsManager.resolution = RES.getSelected();

                    OptionsManager.update();

                    APP.setScreen(new MainMenu());
                    APP.getScreen().dispose();
                }
            }
        });


        GUI.canvas.addActor(TABLE);
        GUIEntity.add(GUI);
        TABLE.setDebug(true, true);
        // endregion

        ENGINE.addEntity(GUIEntity);
        // ENGINE.addEntity(EntityFactory.createParticleGenerator());
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
