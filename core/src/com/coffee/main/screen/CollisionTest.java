package com.coffee.main.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.components.ColliderComponent;
import com.coffee.entity.components.MovementComponent;
import com.coffee.entity.components.SpriteComponent;
import com.coffee.entity.components.TransformComponent;
import com.coffee.entity.systems.CollisionSystem;
import com.coffee.entity.systems.DebugDrawSystem;
import com.coffee.entity.systems.DrawSystem;
import com.coffee.entity.systems.MovementSystem;
import com.coffee.main.Application;
import com.coffee.util.Assets;
import com.coffee.util.CollisionHandler;
import com.coffee.util.Mapper;

/**
 * @author Phillip O'Reggio
 */
public class CollisionTest extends ScreenAdapter {
    private final Viewport VIEWPORT;
    private final PooledEngine ENGINE;
    private final SpriteBatch BATCH;
    private final ShapeRenderer SHAPE_RENDERER;

    private float curTime;
    private final float END_TIME = .2f;

    public CollisionTest() {
        Application app = (Application) Gdx.app.getApplicationListener();
        final TextureAtlas ATLAS = Assets.MANAGER.get(Assets.GameObjects.ATLAS);

        VIEWPORT = app.getViewport();
        BATCH = app.getBatch();
        ENGINE = new PooledEngine();
        SHAPE_RENDERER = app.getShapeRenderer();

        ENGINE.addSystem(new DrawSystem(BATCH, VIEWPORT));
        ENGINE.addSystem(new DebugDrawSystem(SHAPE_RENDERER, VIEWPORT));
        ENGINE.addSystem(new CollisionSystem(SHAPE_RENDERER, VIEWPORT, true));
        ENGINE.addSystem(new MovementSystem());

        // Assemble some entities for testing
        for (int i = 0 ; i < 1000; i++) {
            final Entity E = new Entity();
            final TransformComponent TRANSFORM = new TransformComponent();
            final MovementComponent MOVEMENT = new MovementComponent();
            final ColliderComponent COLLIDER;
            final SpriteComponent SPRITE = new SpriteComponent();

            TRANSFORM.SIZE.setSize(8, 8);
            TRANSFORM.POSITION.set(
                    (float) (Math.random() * VIEWPORT.getWorldWidth() - TRANSFORM.SIZE.width + 1),
                    (float) (Math.random() * VIEWPORT.getWorldHeight() - TRANSFORM.SIZE.height + 1)
            );
            MOVEMENT.moveSpeed = 2.5;
            MOVEMENT.MOVEMENT_NORMAL.set(1, 0).setToRandomDirection();

            COLLIDER = new ColliderComponent(new CollisionHandler() {
                @Override
                public void enterCollision(Entity entity) {

                }

                @Override
                public void whileCollision(Entity entity) {

                }

                @Override
                public void exitCollision(Entity entity) {
                }
            });

            COLLIDER.body.setVertices(new float[]{
                    0,0,
                    16,0,
                    16,16,
                    0,16
            });

            COLLIDER.solid = true;

            Sprite main = ATLAS.createSprite("goodlogic");
            main.setSize(16, 16);
            SPRITE.SPRITES.add(main);

            E.add(TRANSFORM).add(MOVEMENT).add(COLLIDER).add(SPRITE);

            ENGINE.addEntity(E);
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ENGINE.update(delta);

        // Keep entitites within game
        for (Entity e : ENGINE.getEntities()) {
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(e);
            MovementComponent MOVEMENT = Mapper.MOVEMENT.get(e);

            if (TRANSFORM.POSITION.x < -32 || TRANSFORM.POSITION.x > VIEWPORT.getWorldWidth() ||
                TRANSFORM.POSITION.y < -32 || TRANSFORM.POSITION.y > VIEWPORT.getWorldHeight()) {

                TRANSFORM.POSITION.set((float) (Math.random() * 720 - 31), (float) (Math.random() * 1280 - 31));
                MOVEMENT.moveSpeed = 2.5;
                MOVEMENT.MOVEMENT_NORMAL.set(1, 0).setToRandomDirection();
            }
        }
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
