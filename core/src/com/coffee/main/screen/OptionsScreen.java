package com.coffee.main.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
    private final Screen LAST_SCREEN;
    private final Application APP;

    public OptionsScreen() {
        this(null);
    }

    public OptionsScreen(Screen s) {
        APP = (Application) Gdx.app.getApplicationListener();

        ENGINE = new PooledEngine();

        LAST_SCREEN = s;

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
                RES_ID = new Label("Resolution", SKIN),
                MUSIC_VOL_ID = new Label("Music Volume", SKIN),
                SFX_VOL_ID = new Label("SFX Volume", SKIN);
        final TextButton
                CANCEL = new TextButton("CANCEL", SKIN),
                SAVE = new TextButton("SAVE", SKIN);
        final SelectBox<String> RES = new SelectBox<String>(SKIN);
        final Slider
                MUSIC_VOL = new Slider(0, 1, 0.01f, false, SKIN),
                SFX_VOL = new Slider(0, 1, 0.01f, false, SKIN);

        TABLE.setSkin(SKIN);
        TITLE.setAlignment(Align.center);

        RES.setItems(
                "300x400",
                "600x800",
                "675x900",
                "1024x1280"
        );

        RES.setSelected(OptionsManager.resolution);
        MUSIC_VOL.setValue(OptionsManager.musicVolume);
        SFX_VOL.setValue(OptionsManager.sfxVolume);


        TABLE.center().pad(50).setFillParent(true);
        TABLE.add(TITLE).expandX().fillX().colspan(2).row();
        TABLE.add(OPTIONS).expand().fill().colspan(2).pad(10).row();
        TABLE.add(SAVE).expandX().fillX().padRight(5).colspan(1).uniform();
        TABLE.add(CANCEL).expandX().fillX().padLeft(5).colspan(1).uniform();

        OPTIONS.top();
        OPTIONS.add(RES_ID).colspan(1).expandX().fill().padBottom(10).align(Align.left);
        OPTIONS.add(RES).colspan(1).expandX().fill().padBottom(10).align(Align.right).row();
        OPTIONS.add(MUSIC_VOL_ID).colspan(1).expandX().fill().padBottom(10).align(Align.left);
        OPTIONS.add(MUSIC_VOL).colspan(1).expandX().fill().padBottom(10).align(Align.right).row();
        OPTIONS.add(SFX_VOL_ID).colspan(1).expandX().fill().padBottom(10).align(Align.left);
        OPTIONS.add(SFX_VOL).colspan(1).expandX().fill().padBottom(10).align(Align.right);

        CANCEL.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((Button)actor).isPressed()) {
                    APP.getTheme().setVolume(OptionsManager.musicVolume);

                    changeScreen();
                }
            }
        });

        SAVE.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((Button)actor).isPressed()) {
                    OptionsManager.resolution = RES.getSelected();
                    OptionsManager.musicVolume = MUSIC_VOL.getValue();
                    OptionsManager.sfxVolume = SFX_VOL.getValue();

                    OptionsManager.update();

                    changeScreen();
                }
            }
        });

        MUSIC_VOL.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (MUSIC_VOL.isDragging())
                    APP.getTheme().setVolume(MUSIC_VOL.getValue());
            }
        });

        SFX_VOL.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!SFX_VOL.isDragging()) {
                    Sound sound = Assets.MANAGER.get(Assets.Audio.POWERUP_SOUND);
                    sound.play(SFX_VOL.getValue());
                }
            }
        });

        GUI.canvas.addActor(TABLE);
        GUIEntity.add(GUI);
        // endregion

        ENGINE.addEntity(GUIEntity);
        // ENGINE.addEntity(EntityFactory.createParticleGenerator());
    }

    private void changeScreen() {
        APP.getScreen().dispose();
        if (LAST_SCREEN != null)
            APP.setScreen(LAST_SCREEN);
        else
            APP.setScreen(new MainMenu());
    }

    @Override
    public void render(float deltaTime) {
        ENGINE.update(deltaTime);
    }

    @Override
    public void show() {
        APP.getInputMultiplexer().addProcessor(Mapper.GUI.get(GUIEntity).canvas);
        EntityFactory.setEngine(ENGINE);
    }

    @Override
    public void hide() {
        APP.getInputMultiplexer().removeProcessor(Mapper.GUI.get(GUIEntity).canvas);
    }

    @Override
    public void dispose() {

    }

}
