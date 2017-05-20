package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.coffee.entity.components.MovementComponent;
import com.coffee.entity.components.PlayerComponent;
import com.coffee.util.Mapper;

/**
 * System that listens for input via fields in the {@link PlayerComponent},
 * moves the player, and gets the player to shoot bullets.
 *
 * @author Jared Tulayan
 */
public class PlayerSystem extends IteratingSystem {
    public PlayerSystem() {
        super(Family.all(PlayerComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent player = Mapper.PLAYER.get(entity);
        MovementComponent move = Mapper.MOVEMENT.get(entity);

        move.MOVEMENT_NORMAL.set(player.right - player.left, player.up - player.down);

        System.out.println(move.MOVEMENT_NORMAL);
    }
}
