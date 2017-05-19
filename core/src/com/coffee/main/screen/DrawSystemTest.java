package com.coffee.main.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.components.SpriteComponent;
import com.coffee.entity.components.TransformComponent;
import com.coffee.entity.systems.DrawSystem;
import com.coffee.main.Application;
import com.coffee.util.Mapper;

/**
 * @author Phillip O'Reggio
 */
public class DrawSystemTest extends ScreenAdapter {
    Application PARENT;
    Batch batch;
    Viewport viewport;
    private Entity itemEntity, itemEntity2, itemEntity3, GUIEntity;
    private PooledEngine engine;
    private DrawSystem drawSystem;

    public DrawSystemTest(Application parent) {
        PARENT = parent;
        batch = parent.getBatch();
        viewport = parent.getViewport();
        engine = new PooledEngine();
        drawSystem = new DrawSystem(parent);
        itemEntity = new Entity();
        itemEntity2 = new Entity();
        itemEntity3 = new Entity();
        GUIEntity = new Entity();

        itemEntity.add(new SpriteComponent(new Sprite[]{new Sprite(new Texture("goodlogic.png"))}, 0));
        itemEntity.add(new TransformComponent());
        itemEntity2.add(new SpriteComponent(new Sprite[]{new Sprite(new Texture("badlogic.jpg"))}, 1));
        itemEntity2.add(new TransformComponent());
        Mapper.TRANSFORM.get(itemEntity2).POSITION.add(new Vector2(30, 30));
        itemEntity3.add(new SpriteComponent(new Sprite[]{new Sprite(new Texture("goodlogic.png"))}, 2));
        itemEntity3.add(new TransformComponent());
        Mapper.TRANSFORM.get(itemEntity3).POSITION.add(new Vector2(50, 50));

        engine.addSystem(drawSystem);
        engine.addEntity(itemEntity);
        engine.addEntity(itemEntity2);
        engine.addEntity(itemEntity3);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.12F, 0.12F, 0.12F, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawSystem.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
