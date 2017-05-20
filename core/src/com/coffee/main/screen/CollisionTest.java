package com.coffee.main.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.components.ColliderComponent;
import com.coffee.entity.components.MovementComponent;
import com.coffee.entity.components.SpriteComponent;
import com.coffee.entity.components.TransformComponent;
import com.coffee.entity.systems.CollisionSystem;
import com.coffee.entity.systems.DrawSystem;
import com.coffee.entity.systems.MovementSystem;
import com.coffee.main.Application;
import com.coffee.util.CollisionHandler;
import com.coffee.util.Mapper;

/**
 * @author Phillip O'Reggio
 */
public class CollisionTest extends ScreenAdapter {
    private final Viewport VIEWPORT;
    private final PooledEngine ENGINE;

    private float curTime;
    private final float END_TIME = .2f;

    public CollisionTest(Application app) {
        VIEWPORT = app.getViewport();

        // Initialize Engine
        ENGINE = new PooledEngine();
        ENGINE.addSystem(new DrawSystem(app));
        ENGINE.addSystem(new CollisionSystem(VIEWPORT));
        ENGINE.addSystem(new MovementSystem());

        // Assemble some entities for testing
        for (int i = 0 ; i < 500; i++) {
            final Entity E = new Entity();
            final TransformComponent TRANSFORM = new TransformComponent();
            final MovementComponent MOVEMENT = new MovementComponent();
            final ColliderComponent COLLIDER;
            final SpriteComponent SPRITE = new SpriteComponent();

            TRANSFORM.SIZE.setSize(16, 16);
            TRANSFORM.POSITION.set((float) (Math.random() * 720 - 31), (float) (Math.random() * 1280 - 31));
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
            SPRITE.SPRITES.add(new Sprite(new Texture("goodlogic.png"), 16, 16));

            E.add(TRANSFORM).add(MOVEMENT).add(COLLIDER).add(SPRITE);

            ENGINE.addEntity(E);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.12F, 0.12F, 0.12F, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
}
