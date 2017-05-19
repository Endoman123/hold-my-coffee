package com.coffee.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.components.*;
import com.coffee.main.Application;

import java.awt.*;

/**
 * Builder class that automates the creation of entities and
 * attaching the necessary {@link Component}s onto them.
 */
public class EntityBuilder {
    // Please never change these values once they have been initialized.
    // I will throw a hissy fit otherwise.
    // - Game
    private static Viewport viewport;
    private static PooledEngine engine;
    private static Batch batch;
    private static InputMultiplexer inputMultiplexer;

    private static EntityBuilder _inst;

    /**
     * Initializes the {@link EntityBuilder} instance only if it has not been already.
     *
     * @param a the {@code Application} to feed into the {@code EntityBuilder} constructor
     */
    public static void init(Application a) {
        if (_inst == null) {
            _inst = new EntityBuilder(a);
        }
    }

    /**
     * Initializes the constants in the class.
     *
     * @param app the {@code Application} to take the {@code Viewport}, {@code Engine}, {@code InputMultiplexer}, and {@code Batch} from
     */
    public EntityBuilder(Application app) {
        viewport = app.getViewport();
        engine = app.getEngine();
        batch = app.getBatch();
        inputMultiplexer = app.getInputMultiplexer();
    }

    /**
     * Creates a player that can move and shoot.
     *
     * @return a player {@code Entity} that can move, shoot, and be killed.
     */
    public static Entity createPlayer() {
        final Entity E = new Entity();
        final TransformComponent TRANSFORM = new TransformComponent();
        final MovementComponent MOVEMENT = new MovementComponent();
        final SpriteComponent SPRITE = new SpriteComponent();
        final ColliderComponent COLLIDER = new ColliderComponent();
        final InputComponent INPUT;

        // Initialize InputComponent
        InputProcessor ip = new InputAdapter() {
            @Override
            // Handle all movement here.
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.W:
                    case Input.Keys.UP:
                        MOVEMENT.MOVEMENT_NORMAL.add(0, -1);
                        break;
                    case Input.Keys.A:
                    case Input.Keys.LEFT:
                        MOVEMENT.MOVEMENT_NORMAL.add(-1, 0);
                        break;
                    case Input.Keys.S:
                    case Input.Keys.DOWN:
                        MOVEMENT.MOVEMENT_NORMAL.add(0, 1);
                        break;
                    case Input.Keys.D:
                    case Input.Keys.RIGHT:
                        MOVEMENT.MOVEMENT_NORMAL.add(1, 0);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        };

        inputMultiplexer.addProcessor(ip);

        INPUT = new InputComponent(ip);

        return E.add(TRANSFORM).add(MOVEMENT).add(COLLIDER).add(SPRITE).add(INPUT);
    }


}
