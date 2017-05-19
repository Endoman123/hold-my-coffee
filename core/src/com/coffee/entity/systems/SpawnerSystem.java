package com.coffee.entity.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.coffee.entity.components.SpawnerComponent;
import com.coffee.util.Mapper;

/**
 * {@link EntitySystem} that updates the timer of all spawner entities in
 * the {@link Engine} and calls their proper spawning method if the timer is 0.
 *
 * @author Jared Tulayan
 */
public class SpawnerSystem extends IteratingSystem {
    private final ComponentMapper<SpawnerComponent> SPAWN_MAP = Mapper.SPAWNER;

    public SpawnerSystem() {
        super(Family.all(SpawnerComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpawnerComponent spawner = SPAWN_MAP.get(entity);

        spawner.timer -= deltaTime;

        if (spawner.timer <= 0) {
            spawner.HANDLER.spawn();

            spawner.timer = spawner.spawnRate;
        }
    }
}
