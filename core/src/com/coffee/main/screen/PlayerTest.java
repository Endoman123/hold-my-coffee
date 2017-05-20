package com.coffee.main.screen;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.EntityBuilder;
import com.coffee.entity.systems.CollisionSystem;
import com.coffee.entity.systems.DrawSystem;
import com.coffee.entity.systems.MovementSystem;
import com.coffee.entity.systems.PlayerSystem;
import com.coffee.main.Application;

/**
 * Test to see if the player is controllable.
 *
 * @author Jared Tulayan
 */
public class PlayerTest extends ScreenAdapter {
    private final SpriteBatch BATCH;
    private final Viewport VIEWPORT;
    private final PooledEngine ENGINE;

    public PlayerTest(Application app) {
        BATCH = app.getBatch();
        VIEWPORT = app.getViewport();
        ENGINE = app.getEngine();

        ENGINE.addSystem(new MovementSystem());
        ENGINE.addSystem(new DrawSystem(app));
        ENGINE.addSystem(new CollisionSystem(VIEWPORT));
        ENGINE.addSystem(new PlayerSystem());

        ENGINE.addEntity(EntityBuilder.createPlayer());
    }

    @Override
    public void render(float delta) {
        ENGINE.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }
}
