package com.coffee.main.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.components.GUIComponent;
import com.coffee.entity.systems.DrawSystem;
import com.coffee.entity.systems.LifetimeSystem;
import com.coffee.entity.systems.SpawnerSystem;
import com.coffee.main.Application;
import com.coffee.util.Mapper;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;

/**
 * @author Phillip O'Reggio
 */
public class MainMenu extends ScreenAdapter {
    private final PooledEngine ENGINE;
    private final Entity GUIEntity;
    private final Label TITLE;

    public MainMenu() {
        Application app = (Application) Gdx.app.getApplicationListener();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 40;
        param.color = Color.WHITE;
        param.shadowOffsetX = 10;
        param.shadowOffsetY = 10;
        param.shadowColor = Color.GRAY;

        TITLE = new Label("hold my cobbee", new Label.LabelStyle(generator.generateFont(param), Color.WHITE));
        ENGINE = new PooledEngine();
        VisUI.load();

        ENGINE.addSystem(new DrawSystem(app.getBatch(), app.getViewport()));
        ENGINE.addSystem(new LifetimeSystem());
        ENGINE.addSystem(new SpawnerSystem(ENGINE));

        GUIEntity = ENGINE.createEntity();
        GUIComponent GUAcomel = ENGINE.createComponent(GUIComponent.class);
        GUAcomel.CANVAS.addActor(TITLE);
        GUAcomel.CANVAS.addActor(new VisTextButton("Begin your pass-times y'all"));

        GUIEntity.add(GUAcomel);
        ENGINE.addEntity(GUIEntity);
    }

    @Override
    public void show() {
        Application app = (Application) Gdx.app.getApplicationListener();

        app.getInputMultiplexer().addProcessor(Mapper.GUI.get(GUIEntity).CANVAS);
        EntityFactory.setPooledEngine(ENGINE);
    }

    @Override
    public void hide() {
        Application app = (Application) Gdx.app.getApplicationListener();
        app.getInputMultiplexer().removeProcessor(Mapper.GUI.get(GUIEntity).CANVAS);
    }

    @Override
    public void render(float deltaTime) {
        ENGINE.update(deltaTime);
    }

    @Override
    public void dispose() {

    }

}
