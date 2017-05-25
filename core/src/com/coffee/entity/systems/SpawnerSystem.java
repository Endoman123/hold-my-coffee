package com.coffee.entity.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.coffee.entity.components.SpawnerComponent;

import static com.coffee.util.Mapper.SPAWNER;

/**
 * {@link EntitySystem} that updates the timer of all spawner entities in
 * the {@link Engine} and calls their proper spawning method if the timer is 0.
 *
 * @author Jared Tulayan
 */
public class SpawnerSystem extends IteratingSystem {
    private final PooledEngine ENGINE;

    public SpawnerSystem(PooledEngine engine) {
        super(Family.all(SpawnerComponent.class).get());
        ENGINE = engine;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpawnerComponent spawner = SPAWNER.get(entity);

        spawner.timer -= deltaTime;

        if (spawner.timer <= 0) {
            for (Entity e : spawner.HANDLER.spawn())
                ENGINE.addEntity(e);

            spawner.timer = spawner.spawnRate;
        }
    }
}
