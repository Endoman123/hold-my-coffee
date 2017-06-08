package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.components.*;
import com.coffee.util.Mapper;

import java.awt.*;

/**
 * System that listens for input via fields in the {@link PlayerComponent},
 * moves the player, and gets the player to shoot bullets.
 *
 * @author Jared Tulayan
 */
public class PlayerSystem extends IteratingSystem {
    private final Dimension GAME_SIZE;

    public PlayerSystem(Viewport viewport) {
        super(Family.all(PlayerComponent.class).get());

        GAME_SIZE = new Dimension((int)viewport.getWorldWidth(), (int)viewport.getWorldHeight());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent player = Mapper.PLAYER.get(entity);
        SpriteComponent sprite = Mapper.SPRITE.get(entity);
        MovementComponent move = Mapper.MOVEMENT.get(entity);
        TransformComponent transform = Mapper.TRANSFORM.get(entity);
        HealthComponent health = Mapper.HEALTH.get(entity);

        if (health.getHealthPercent() > 0) {
            // Update the shoot timer
            // Since we can, we need to clamp the value of the timer between 0 and the value of the bulletsPerSecond
            // to avoid any overflow exceptions.
            float bps = (float) (player.bulletsPerSecond + player.upFireRate);
            player.shootTimer = MathUtils.clamp(player.shootTimer - bps * deltaTime, 0, 1);

            // Any invalid moves the player tries to take, we should combat ASAP.
            if (player.up == 1 && transform.POSITION.y + move.moveSpeed * deltaTime > GAME_SIZE.height * 2 / 3)
                player.up = 0;
            if (player.left == 1 && transform.POSITION.x + move.moveSpeed * deltaTime < 0)
                player.left = 0;
            if (player.down == 1 && transform.POSITION.y - move.moveSpeed * deltaTime < 64)
                player.down = 0;
            if (player.right == 1 && transform.POSITION.x + move.moveSpeed * deltaTime > GAME_SIZE.width - transform.SIZE.width)
                player.right = 0;

            // Shoot if we can
            if (player.shoot && player.shootTimer == 0) {
                switch (player.upBulletDamage) {
                    case 1:
                        getEngine().addEntity(EntityFactory.createPlayerBullet(
                                transform.POSITION.x + transform.ORIGIN.x + 8,
                                transform.POSITION.y + transform.SIZE.height + 10
                        ));

                        getEngine().addEntity(EntityFactory.createPlayerBullet(
                                transform.POSITION.x + transform.ORIGIN.x - 8,
                                transform.POSITION.y + transform.SIZE.height + 10
                        ));
                        break;

                    case 2:
                        getEngine().addEntity(EntityFactory.createPlayerBullet(
                                transform.POSITION.x + transform.ORIGIN.x,
                                transform.POSITION.y + transform.SIZE.height + 10
                        ));

                        getEngine().addEntity(EntityFactory.createPlayerBullet(
                                transform.POSITION.x + transform.ORIGIN.x + 16,
                                transform.POSITION.y + transform.SIZE.height + 10
                        ));

                        getEngine().addEntity(EntityFactory.createPlayerBullet(
                                transform.POSITION.x + transform.ORIGIN.x - 16,
                                transform.POSITION.y + transform.SIZE.height + 10
                        ));
                        break;

                    case 3:
                        getEngine().addEntity(EntityFactory.createPlayerBullet(
                                transform.POSITION.x + transform.ORIGIN.x + 8,
                                transform.POSITION.y + transform.SIZE.height + 10
                        ));

                        getEngine().addEntity(EntityFactory.createPlayerBullet(
                                transform.POSITION.x + transform.ORIGIN.x + 24,
                                transform.POSITION.y + transform.SIZE.height - 10
                        ));

                        getEngine().addEntity(EntityFactory.createPlayerBullet(
                                transform.POSITION.x + transform.ORIGIN.x - 8,
                                transform.POSITION.y + transform.SIZE.height + 10
                        ));

                        getEngine().addEntity(EntityFactory.createPlayerBullet(
                                transform.POSITION.x + transform.ORIGIN.x - 24,
                                transform.POSITION.y + transform.SIZE.height - 10
                        ));
                        break;

                    case 4:
                        getEngine().addEntity(EntityFactory.createPlayerBullet(
                                transform.POSITION.x + transform.ORIGIN.x,
                                transform.POSITION.y + transform.SIZE.height + 10
                        ));

                        getEngine().addEntity(EntityFactory.createPlayerBullet(
                                transform.POSITION.x + transform.ORIGIN.x + 16,
                                transform.POSITION.y + transform.SIZE.height + 10
                        ));

                        getEngine().addEntity(EntityFactory.createPlayerBullet(
                                transform.POSITION.x + transform.ORIGIN.x + 32,
                                transform.POSITION.y + transform.SIZE.height - 10
                        ));

                        getEngine().addEntity(EntityFactory.createPlayerBullet(
                                transform.POSITION.x + transform.ORIGIN.x - 16,
                                transform.POSITION.y + transform.SIZE.height + 10
                        ));

                        getEngine().addEntity(EntityFactory.createPlayerBullet(
                                transform.POSITION.x + transform.ORIGIN.x - 32,
                                transform.POSITION.y + transform.SIZE.height - 10
                        ));
                        break;

                    default:
                        getEngine().addEntity(EntityFactory.createPlayerBullet(
                                transform.POSITION.x + transform.ORIGIN.x,
                                transform.POSITION.y + transform.SIZE.height + 10
                        ));
                        break;
                }
                player.shootTimer = 1;
            }

            // Flash the player every frame when invincible
            if (health.invincible && health.invincibilityTimer % 1 == 0)
                sprite.SPRITES.get(0).setAlpha(0);
            else
                sprite.SPRITES.get(0).setAlpha(1);

            // Update position
            move.moveSpeed = MathUtils.lerp(3, 5, player.upSpeed / 4.0f);
            move.MOVEMENT_NORMAL.set(player.right - player.left, player.up - player.down);

        } else {
            if (player.lives > 0) { // If we still got lives left, reset the player while not in control
                if (!player.revive) {
                    sprite.SPRITES.first().setAlpha(0);
                    move.MOVEMENT_NORMAL.setZero();
                    player.reset();
                    player.bulletsPerSecond = 3;
                    transform.POSITION.set(GAME_SIZE.width / 2 - transform.ORIGIN.x, 128 - transform.ORIGIN.y);

                    health.respawnTimer = health.respawnDuration;
                    player.revive = true;
                } else if (health.respawnTimer == 0) { // Wait to revive, then respawn.
                    System.out.println("Revive!");
                    player.lives--;
                    health.invincibilityTimer = health.invincibilityDuration;
                    health.health = health.maxHealth;
                    sprite.SPRITES.first().setAlpha(1);
                    player.revive = false;
                }
            } else { // If we dead, just remove enough so that we can keep the gui but we are still dead.
                entity.remove(ColliderComponent.class);
                entity.remove(SpriteComponent.class);
                entity.remove(MovementComponent.class);
            }
        }
    }
}
