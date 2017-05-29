package com.coffee.main.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.components.GUIComponent;
import com.coffee.entity.systems.GUISystem;
import com.coffee.entity.systems.LifetimeSystem;
import com.coffee.entity.systems.SpawnerSystem;
import com.coffee.main.Application;
import com.coffee.util.Assets;
import com.coffee.util.Mapper;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

/**
 * @author Phillip O'Reggio
 */
public class MainMenu extends ScreenAdapter {
    private final PooledEngine ENGINE;
    private final Entity GUIEntity;

    public MainMenu() {
        Application app = (Application) Gdx.app.getApplicationListener();

        ENGINE = new PooledEngine();
        final TextureAtlas UI_ATLAS = Assets.MANAGER.get(Assets.UI.ATLAS);

        VisUI.load();

        ENGINE.addSystem(new GUISystem());
        ENGINE.addSystem(new LifetimeSystem());
        ENGINE.addSystem(new SpawnerSystem(ENGINE));

        GUIEntity = new Entity();
        final GUIComponent GUI = new GUIComponent();
        GUI.canvas = new Stage(app.getViewport(), app.getBatch());

        final VisTable TABLE = new VisTable();
        final VisImage TITLE = new VisImage(UI_ATLAS.findRegion("title"));
        final VisTextButton PLAY = new VisTextButton("START");

        TITLE.setScaling(Scaling.fit);

        TABLE.center().pad(50).setFillParent(true);
        TABLE.add(TITLE).expand().fill().row();
        TABLE.add(PLAY);

        GUI.canvas.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((VisTextButton) actor).isChecked()) {
                    if (actor == PLAY)
                        System.out.println("Go!");
                }
            }
        });

        GUI.canvas.addActor(TABLE);
        GUIEntity.add(GUI);
        ENGINE.addEntity(GUIEntity);
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
    public void render(float deltaTime) {
        ENGINE.update(deltaTime);
    }

    @Override
    public void dispose() {

    }

}
