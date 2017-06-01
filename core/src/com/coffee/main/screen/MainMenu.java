package com.coffee.main.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.components.GUIComponent;
import com.coffee.entity.systems.*;
import com.coffee.main.Application;
import com.coffee.util.Assets;
import com.coffee.util.Mapper;

/**
 * @author Phillip O'Reggio
 */
public class MainMenu extends ScreenAdapter {
    private final PooledEngine ENGINE;
    private final Entity GUIEntity;

    public MainMenu() {
        final Application APP = (Application) Gdx.app.getApplicationListener();

        ENGINE = new PooledEngine();

        // region MainMenu entity
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

        final Table TABLE = new Table();
        final Image TITLE = new Image(SKIN.getDrawable("title"));
        final TextButton
                START = new TextButton("START", SKIN),
                OPTIONS = new TextButton("OPTIONS", SKIN),
                EXIT = new TextButton("EXIT", SKIN);

        TABLE.setSkin(SKIN);
        TITLE.setScaling(Scaling.fit);

        TABLE.center().pad(50).setFillParent(true);
        TABLE.add(TITLE).expand().fill().colspan(2).row();
        TABLE.add(START).fillX().pad(10, 10, 10, 5);
        TABLE.add(OPTIONS).fillX().pad(10, 5, 10, 10).row();
        TABLE.add(EXIT).expandX().fillX().colspan(2).pad(0, 10, 10, 10);

        GUI.canvas.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((Button) actor).isChecked()) {
                    if (actor == START) {
                        APP.setScreen(new AITest());
                        APP.getScreen().dispose();
                    }

                    if (actor == EXIT) {
                        Gdx.app.exit();
                    }
                }
            }
        });

        GUI.canvas.addActor(TABLE);
        GUIEntity.add(GUI);
        // endregion

        ENGINE.addEntity(GUIEntity);
        ENGINE.addEntity(EntityFactory.createParticleGenerator());
    }

    @Override
    public void render(float deltaTime) {
        ENGINE.update(deltaTime);
    }

    @Override
    public void show() {
        Application app = (Application) Gdx.app.getApplicationListener();

        app.getInputMultiplexer().addProcessor(Mapper.GUI.get(GUIEntity).canvas);
        EntityFactory.setPooledEngine(ENGINE);
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
