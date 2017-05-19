package com.coffee.main.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.components.ColliderComponent;
import com.coffee.entity.components.MovementComponent;
import com.coffee.entity.components.SpriteComponent;
import com.coffee.entity.components.TransformComponent;
import com.coffee.entity.systems.CollisionSystem;
import com.coffee.entity.systems.DrawSystem;
import com.coffee.entity.systems.MovementSystem;
import com.coffee.main.Application;
import com.coffee.util.Mapper;
import com.coffee.util.QuadTree;

/**
 * @author Phillip O'Reggio
 */
public class CollisionTest extends ScreenAdapter {
    Application PARENT;
    Batch batch;
    private ShapeRenderer DEBUG;
    Viewport viewport;

    private QuadTree quadTree;
    private Array<Entity> entities;
    private PooledEngine engine;
    private DrawSystem drawSystem;
    private CollisionSystem collisionSystem;
    private MovementSystem movementSystem;

    private float curTime;
    private final float END_TIME = .2f;

    public CollisionTest(Application parent) {
        PARENT = parent;
        viewport = parent.getViewport();

        batch = parent.getBatch();
        DEBUG = new ShapeRenderer();
        DEBUG.setAutoShapeType(true);

        viewport = parent.getViewport();

        engine = new PooledEngine();
        drawSystem = new DrawSystem(parent);
        collisionSystem = new CollisionSystem();
        movementSystem = new MovementSystem();

        quadTree = new QuadTree(0, new Rectangle(10, 10, 720, 1280));
        entities = new Array<Entity>(new Entity[50]);

        for (int i = 0 ; i < entities.size; i++) {
            entities.set(i, new Entity());
            entities.get(i).add(new TransformComponent());
            Mapper.TRANSFORM.get(entities.get(i)).POSITION.add((float) (Math.random() * 300), (float) (Math.random() * 300));
            entities.get(i).add(new MovementComponent());
            Mapper.MOVEMENT.get(entities.get(i)).moveSpeed = 100;
            entities.get(i).add(new ColliderComponent());
            entities.get(i).add(new SpriteComponent(new Sprite[] {new Sprite(new Texture("goodlogic.png"), 32, 32)}, 0));

            engine.addEntity(entities.get(i));
        }

        for (int i = 0 ; i < entities.size; i++)
            quadTree.insert(Mapper.COLLIDER.get(entities.get(i)).body);

        engine.addSystem(drawSystem);
        engine.addSystem(collisionSystem);
        engine.addSystem(movementSystem);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.12F, 0.12F, 0.12F, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        curTime += delta;
        if (curTime >= END_TIME) {
            curTime = 0;
            for (Entity e : entities) {
                Mapper.TRANSFORM.get(e).POSITION.add((float) (-20 + (Math.random() * 40)), (float) (-20 + (Math.random() * 40)));
            }
        }

        quadTree.clear();
        for (Entity e : entities)
            quadTree.insert(Mapper.COLLIDER.get(e).body);

        engine.update(delta);
        quadTree.draw(DEBUG);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
