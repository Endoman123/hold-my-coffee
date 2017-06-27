package com.coffee.main.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.components.SpriteComponent;
import com.coffee.entity.components.TransformComponent;
import com.coffee.entity.systems.DrawSystem;
import com.coffee.main.Application;
import com.coffee.util.Assets;

/**
 * @author Phillip O'Reggio
 */
public class DrawSystemTest extends ScreenAdapter {
    private final SpriteBatch BATCH;
    private final Viewport VIEWPORT;
    private Entity GUIEntity;
    private final PooledEngine ENGINE;

    public DrawSystemTest() {
        Application app = (Application) Gdx.app.getApplicationListener();
        final TextureAtlas ATLAS = Assets.MANAGER.get(Assets.GameObjects.ATLAS);

        BATCH = app.getBatch();
        VIEWPORT = app.getViewport();
        ENGINE = new PooledEngine();

        final Entity
                E1 = new Entity(),
                E2 = new Entity(),
                E3 = new Entity();

        GUIEntity = new Entity();

        ENGINE.addSystem(new DrawSystem(BATCH, VIEWPORT));

        final TransformComponent
                T1 = new TransformComponent(),
                T2 = new TransformComponent(),
                T3 = new TransformComponent();

        final SpriteComponent
                SC1 = new SpriteComponent(),
                SC2 = new SpriteComponent(),
                SC3 = new SpriteComponent();

        final Sprite
                GOOD = ATLAS.createSprite("goodlogic"),
                BAD = ATLAS.createSprite("badlogic");

        T2.POSITION.add(30, 30);
        T3.POSITION.add(50, 50);

        SC1.SPRITES.add(GOOD);
        SC2.SPRITES.add(BAD);
        SC3.SPRITES.add(GOOD);

        SC2.zIndex = 1;
        SC3.zIndex = 2;

        ENGINE.addEntity(E1.add(T1).add(SC1));
        ENGINE.addEntity(E2.add(T2).add(SC2));
        ENGINE.addEntity(E3.add(T3).add(SC3));
    }

    public void show() {

    }

    @Override
    public void render(float delta) {
        ENGINE.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        VIEWPORT.update(width, height, true);
    }

    @Override
    public void dispose() {
        ENGINE.removeAllEntities();

        for (EntitySystem s : ENGINE.getSystems()) {
            ENGINE.removeSystem(s);
        }
    }
}
