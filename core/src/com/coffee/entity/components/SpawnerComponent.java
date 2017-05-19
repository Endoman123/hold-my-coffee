package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.coffee.util.SpawnerHandler;

/**
 * {@link Component} that contains data for spawning objects on a timer.
 *
 * @author Jared Tulayan
 */
public class SpawnerComponent implements Component {
    public double spawnRate, timer;
    public final SpawnerHandler HANDLER;

    /**
     * Initializes the {@link SpawnerComponent} with the specified {@link SpawnerHandler}.
     * This also sets the spawnrate to a default of 10 seconds.
     *
     * @param handler the {@code SpawnerHandler} that will handle how this {@code Entity} spawns in other objects.
     */
    public SpawnerComponent(SpawnerHandler handler) {
        HANDLER = handler;
        timer = 0;
        spawnRate = 10;
    }

}
