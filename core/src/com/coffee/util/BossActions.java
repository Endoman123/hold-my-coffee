package com.coffee.util;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.components.*;

/**
 * Class of actions that the boss can do.
 * This allows for some nice control over what the boss does over certain periods.
 *
 */
public class BossActions {

    /**
     * {@link Action Action} that moves the boss to a specified location after a certain wait period.
     */
    public static class Move implements Action {
        private final Vector2 TARGET_LOC;
        private Vector2 beginLoc;
        private boolean moving = false;
        private float actionTimer;

        public Move(float wait, Vector2 loc) {
            actionTimer = wait;

            beginLoc = new Vector2();
            TARGET_LOC = new Vector2(loc);
        }

        @Override
        public boolean act(Entity boss, float deltaTime) {
            final AIComponent AI = Mapper.AI.get(boss);
            final TransformComponent TRANSFORM = Mapper.TRANSFORM.get(boss);

            if (!moving) { // Wait to move
                actionTimer -= deltaTime;
                if (actionTimer <= 0) { // Initialize movement state
                    actionTimer = 0;
                    beginLoc.set(TRANSFORM.POSITION);
                    moving = true;
                }
                return false;
            }

            actionTimer = MathUtils.clamp(actionTimer + deltaTime * AI.lerpSpeed, 0, 1);
            float perc = MathUtils.sin(actionTimer * MathUtils.PI / 2.0f);

            TRANSFORM.POSITION.set(
                    MathUtils.lerp(beginLoc.x, TARGET_LOC.x, perc),
                    MathUtils.lerp(beginLoc.y, TARGET_LOC.y, perc)
            );

            return actionTimer == 1;
        }
    }

    /**
     * {@link Action Action} that just has the boss do nothing for a specified amount of time.
     */
    public static class DoNothing implements Action {
        private float timer;

        public DoNothing(float t) {
            timer = t;
        }

        public boolean act(Entity e, float deltaTime){
            timer -= deltaTime;

            return timer <= 0;
        }
    }

    /**
     * {@link Action Action} that creates one bullet that explodes into a shotgun spray.
     */
    public static class ShotgunSpray implements Action {
        private float timer;
        boolean createdBall = false;

        public boolean act(Entity boss, float deltaTime) {
            AIComponent AI = Mapper.AI.get(boss);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(boss);

            if (!createdBall)
                EntityFactory.createEnemyBulletExploding(TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x, TRANSFORM.POSITION.y + 32, 0);
            else
                timer += deltaTime;

            return timer >= 2.5;
        }
    }

    /**
     * {@link Action Action} that creates a slow-moving spiral of bullets.
     */
    public static class SimpleSpiralAttack implements Action {
        private int iterations;
        private float fireTimer;

        public boolean act (Entity boss, float deltaTime) {
            AIComponent AI = Mapper.AI.get(boss);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(boss);

           fireTimer += deltaTime;

            if (fireTimer >= 0.1f) {
                for (int i = 0; i < 6; i++) {
                    float deg = iterations * 7 + i * 60;
                    float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                    float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                    EntityFactory.createEnemyBall(xPlace, yPlace, deg);
                }
            }

            return iterations == 75;
        }
    }

    /**
     * {@link Action Action} that has the boss shoot one laser at the player.
     */
    public static class SimpleLaserAttack implements Action {
        private float fireTimer;
        private int iterations;
        private final Engine ENGINE;
        private final Viewport VIEWPORT;

        public SimpleLaserAttack(Engine e, Viewport v) {
            ENGINE = e;
            VIEWPORT = v;
        }
        public boolean act(Entity boss, float deltaTime) {
            AIComponent AI = Mapper.AI.get(boss);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(boss);

            fireTimer += deltaTime;

            if (iterations == 0) { // Assume that if we have not shot a bullet yet, we have not even specified a target
                final ImmutableArray<Entity> PLAYERS = ENGINE.getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class).get());
                AI.TARGET_LOC.set(VIEWPORT.getWorldWidth() / 2, VIEWPORT.getWorldHeight() / 2);

                if (PLAYERS.size() != 0) {
                    TransformComponent PLAYER_TRANS = Mapper.TRANSFORM.get(PLAYERS.first());

                    AI.TARGET_LOC.set(PLAYER_TRANS.POSITION).add(PLAYER_TRANS.ORIGIN);
                }
            }

            if (fireTimer >= 0.001f) {
                final Vector2 SELF_LOC = new Vector2(TRANSFORM.POSITION).add(TRANSFORM.ORIGIN);

                float theta = MathUtils.atan2(AI.TARGET_LOC.y - SELF_LOC.y, AI.TARGET_LOC.x - SELF_LOC.x);

                float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(theta);
                float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(theta);

                ENGINE.addEntity(EntityFactory.createWeakFastEnemyBullet(xPlace, yPlace, theta * MathUtils.radDeg));

                fireTimer = 0;
                iterations++;
            }

            return iterations == 250;
        }
    }

    /**
     * {@link Action Action} that has the boss create bullets that launch towards the player and fade away.
     */
    public static class InvisibleHomingBulletsAttack implements Action {
        private float fireTimer;

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);
            HealthComponent HEALTH = Mapper.HEALTH.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.1f) {
                for (int i = 0; i < 6; i++) {
                    float deg = 220 + i * 20;
                    float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                    float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                    EntityFactory.createHomingEnemyBullet(xPlace, yPlace, deg);
                }
                return true;
            }
            return false;
        }
    }

    /**
     * {@link Action Action} that shoots a spiral of fading bullets.
     */
    public static class TempestBloom implements Action {
        private float fireTimer;
        private int iterations;

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);
            HealthComponent HEALTH = Mapper.HEALTH.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.1f) {
                for (int i = 0; i < 6; i++) {
                    float deg = AI.actionTimer * 7 + i * 60;
                    float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                    float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                    EntityFactory.createEnemyBulletFade(xPlace, yPlace, deg);
                }

                fireTimer = 0;
                iterations++;
            }
            return iterations == 75;
        }
    }

    /**
     * {@link Action Action} that fires a cone of bullets.
     */
    public static class SimpleConeAttack implements Action {
        private float fireTimer;
        private int iterations;

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);
            HealthComponent HEALTH = Mapper.HEALTH.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.15) {
                if (iterations % 2 == 0)
                    for (int i = 0; i < 17; i++) {
                        float deg = 190 + i * 10;
                        float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                        float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                        EntityFactory.createEnemyBulletSlows(xPlace, yPlace, deg);
                    }
                else
                    for (int i = 0; i < 16; i++) {
                        float deg = 195 + i * 10;
                        float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                        float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                        EntityFactory.createEnemyBulletSlows(xPlace, yPlace, deg);
                    }

                fireTimer = 0;
                iterations++;
            }
            return iterations == 20;
        }
    }

    /**
     * {@link Action Action} that fires a circle of homing bullets.
     */
    public static class HomingBulletCircleAttack implements Action {
        private float fireTimer;
        private int iterations;

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);
            HealthComponent HEALTH = Mapper.HEALTH.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.1f) {
                for (int i = 0; i < 6; i++) {
                    float deg = iterations * 7 + i * 60;
                    float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                    float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                    EntityFactory.createHomingEnemyBullet(xPlace, yPlace, deg);
                }

                fireTimer = 0;
                iterations++;
            }
            return iterations == 9;
        }
    }

    /**
     * {@link Action Action} that has the boss creates 3 balls that shoot lasers at the player.
     */
    public static class TripleLaserBallAttack implements Action {
        private float fireTimer;
        private final Viewport VIEWPORT;

        public TripleLaserBallAttack(Viewport v) {
            VIEWPORT = v;
        }

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);
            HealthComponent HEALTH = Mapper.HEALTH.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.1f) {
                final Vector2
                        WORLD_CENTER = new Vector2(VIEWPORT.getWorldWidth() / 2, VIEWPORT.getWorldHeight() / 2),
                        TRANS_CENTER = new Vector2(TRANSFORM.POSITION).add(TRANSFORM.ORIGIN);

                float theta = MathUtils.radDeg * MathUtils.atan2(WORLD_CENTER.y - TRANS_CENTER.y, WORLD_CENTER.x - TRANS_CENTER.x);

                for (int i = 0; i < 3; i++) {
                    float deg = theta + i * 30 - 30;
                    float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                    float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                    EntityFactory.createEnemyLaserEmitter(xPlace, yPlace, deg);
                }
                return true;
            }
            return false;
        }
    }

    /**
     * {@link Action Action} that has the boss create a wave of shifting spiraling bullets.
     */
    public static class ShiftingSpiralAttack implements Action {
        private float fireTimer;
        private int iterations;

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);
            HealthComponent HEALTH = Mapper.HEALTH.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.1) {
                for (int i = 0; i < 6; i++) {
                    float deg = AI.actionTimer * 7 + i * 60;
                    float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                    float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                    EntityFactory.createEnemyBallShifter(xPlace, yPlace, deg);
                }

                fireTimer = 0;
                iterations++;
            }
            return iterations == 50;
        }
    }

    /**
     * {@link Action Action} that is an inverted version of the {@link ShiftingSpiralAttack}.
     */
    public static class ReverseShiftingSpiralAttack implements Action {
        private float fireTimer;
        private int iterations;

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.1) {
                for (int i = 0; i < 6; i++) {
                    float deg = AI.actionTimer * 7 + i * 60;
                    float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                    float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                    Entity bullet = EntityFactory.createEnemyBallShifter(xPlace, yPlace, deg);
                    SpriteComponent SPRITE = Mapper.SPRITE.get(bullet);
                    MovementComponent MOVEMENT = Mapper.MOVEMENT.get(bullet);

                    MOVEMENT.moveSpeed = 0;
                    Mapper.BULLET.get(bullet).handler = (float dt) -> {
                        SPRITE.SPRITES.get(0).setColor(
                                MathUtils.clamp(SPRITE.SPRITES.get(0).getColor().r - dt / 20f, 0, 1),
                                MathUtils.clamp(SPRITE.SPRITES.get(0).getColor().g + dt / 2f, 0, 1),
                                MathUtils.clamp(SPRITE.SPRITES.get(0).getColor().b - dt / 15f, 0, 1),
                                SPRITE.SPRITES.get(0).getColor().a
                        );
                        MOVEMENT.MOVEMENT_NORMAL.rotate(-dt * 36);
                        MOVEMENT.moveSpeed += dt * 4;
                    };
                }

                fireTimer = 0;
                iterations++;
            }
            return iterations == 50;
        }
    }

    /**
     * {@link Action Action} that shoots a wave of fading bullets and laser emitters at the player.
     */
    // TODO We can separate this attack into two different attacks.
    public static class ImperishableNight implements Action {
        private float fireTimer;
        private int iterations;
        private final Engine ENGINE;
        private final Viewport VIEWPORT;

        public ImperishableNight(Engine e, Viewport v) {
            ENGINE = e;
            VIEWPORT = v;
        }

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);

            fireTimer += deltaTime;

            if (iterations == 0) { // Assume that target has not been decided yet.
                final ImmutableArray<Entity> PLAYERS = ENGINE.getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class).get());
                AI.TARGET_LOC.set(VIEWPORT.getWorldWidth() / 2, VIEWPORT.getWorldHeight() / 2);

                if (PLAYERS.size() != 0) {
                    TransformComponent PLAYER_TRANS = Mapper.TRANSFORM.get(PLAYERS.first());

                    AI.TARGET_LOC.set(PLAYER_TRANS.POSITION).add(PLAYER_TRANS.ORIGIN);
                }
            }

            if (fireTimer >= 0.1f) {
                float xPlace;
                float yPlace;

                if (iterations % 20 != 0) { // Spiral Attack
                    for (int i = 0; i < 12; i++) {
                        float deg = iterations * 7 + i * 30;
                        xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                        yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                        Entity bullet = EntityFactory.createEnemyBall(xPlace, yPlace, deg);
                        SpriteComponent SPRITE = Mapper.SPRITE.get(bullet);
                        BulletComponent BULLET = Mapper.BULLET.get(bullet);
                        MovementComponent MOVE = Mapper.MOVEMENT.get(bullet);

                        MOVE.moveSpeed = 2;
                        BULLET.handler = (float dt) -> {
                            BULLET.timer += dt / 2f;
                            SPRITE.SPRITES.first().setColor(Color.RED.cpy().lerp(new Color(0, 0, 1f, 0), (MathUtils.cos(BULLET.timer * MathUtils.PI2) + 1) / 2f));
                            MOVE.MOVEMENT_NORMAL.rotate(dt * 5);
                        };
                    }

                } else { // Laser
                    for (int i = 0; i < 4; i++) {
                        float deg = iterations * 7 + i * 90;
                        xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                        yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                        EntityFactory.createEnemyLaserEmitter(xPlace, yPlace, deg);
                    }
                }

                fireTimer = 0;
                iterations++;
            }
            return iterations == 70;
        }
    }

    /**
     * {@link Action Action} that shoots helix of lasers.
     */
    public static class HelixLaserAttack implements Action {
        private float fireTimer;
        private float iterations;
        private final Vector2 TARGET;

        public HelixLaserAttack(Engine e, Viewport v) {
            final ImmutableArray<Entity> PLAYERS = e.getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class).get());
            TARGET = new Vector2(v.getWorldWidth() / 2, v.getWorldHeight() / 2);

            if (PLAYERS.size() != 0) {
                TransformComponent PLAYER_TRANS = Mapper.TRANSFORM.get(PLAYERS.first());

                TARGET.set(PLAYER_TRANS.POSITION).add(PLAYER_TRANS.ORIGIN);
            }
        }


        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.001f) {
                final Vector2 SELF_LOC = new Vector2(TRANSFORM.POSITION).add(TRANSFORM.ORIGIN);

                float theta = MathUtils.atan2(TARGET.y - SELF_LOC.y, TARGET.x - SELF_LOC.x);

                float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(theta);
                float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(theta);

                Entity bullet = EntityFactory.createWeakFastEnemyBullet(xPlace, yPlace, theta * MathUtils.radDeg);
                SpriteComponent SPRITE = Mapper.SPRITE.get(bullet);
                BulletComponent BULLET = Mapper.BULLET.get(bullet);
                MovementComponent MOVE = Mapper.MOVEMENT.get(bullet);

                if (iterations % 2 == 0)
                    BULLET.handler = (float dt) -> {
                        MOVE.MOVEMENT_NORMAL.setAngle((theta * MathUtils.radDeg) + MathUtils.cos(BULLET.timer * MathUtils.PI2) * 50);
                        BULLET.timer += dt * 2;
                    };
                else
                    BULLET.handler = (float dt) -> {
                        MOVE.MOVEMENT_NORMAL.setAngle((theta * MathUtils.radDeg) - MathUtils.cos(BULLET.timer * MathUtils.PI2) * 50);
                        BULLET.timer += dt * 2;
                    };

                fireTimer = 0;
                iterations++;
            }
            return iterations == 250;
        }
    }

    /**
     * A stronger version of {@link HelixLaserAttack}
     */
    // TODO this might actually be a combo of helix and regular laser. Will keep for now to keep consistencies.
    public static class HelixPlusAttack implements Action {
        private float fireTimer;
        private int iterations;
        private final Vector2 TARGET;

        public HelixPlusAttack(Engine e, Viewport v) {
            final ImmutableArray<Entity> PLAYERS = e.getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class).get());
            TARGET = new Vector2(v.getWorldWidth() / 2, v.getWorldHeight() / 2);

            if (PLAYERS.size() != 0) {
                TransformComponent PLAYER_TRANS = Mapper.TRANSFORM.get(PLAYERS.first());

                TARGET.set(PLAYER_TRANS.POSITION).add(PLAYER_TRANS.ORIGIN);
            }
        }

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.0001f) {
                final Vector2 SELF_LOC = new Vector2(TRANSFORM.POSITION).add(TRANSFORM.ORIGIN);
                float xPlace, yPlace;

                if (AI.actionTimer % 200 != 0) { // All laser scenarios
                    float theta = MathUtils.atan2(TARGET.y - SELF_LOC.y, TARGET.x - SELF_LOC.x);
                    xPlace = SELF_LOC.x + 3 * MathUtils.cos(theta);
                    yPlace = SELF_LOC.y + 3 * MathUtils.sin(theta);

                    Entity bullet = EntityFactory.createWeakFastEnemyBullet(xPlace, yPlace, theta * MathUtils.radDeg);
                    SpriteComponent SPRITE = Mapper.SPRITE.get(bullet);
                    BulletComponent BULLET = Mapper.BULLET.get(bullet);
                    MovementComponent MOVE = Mapper.MOVEMENT.get(bullet);

                    if (AI.actionTimer % 3 == 0)
                        BULLET.handler = (float dt) -> {
                            MOVE.MOVEMENT_NORMAL.setAngle((theta * MathUtils.radDeg) + MathUtils.cos(BULLET.timer * MathUtils.PI2) * 60);
                            BULLET.timer += dt * 2;
                        };
                    else if (AI.actionTimer % 3 == 1)
                        BULLET.handler = (float dt) -> {
                            MOVE.MOVEMENT_NORMAL.setAngle((theta * MathUtils.radDeg) - MathUtils.cos(BULLET.timer * MathUtils.PI2) * 60);
                            BULLET.timer += dt * 2;
                        };
                    else if (AI.actionTimer % 3 == 2)
                        BULLET.handler = null;

                } else { // Lasers
                    for (int i = 0; i < 3; i++) {
                        float theta = AI.actionTimer * 7 + i * 120;
                        xPlace = SELF_LOC.x + 3 * MathUtils.cos(theta * MathUtils.degreesToRadians);
                        yPlace = SELF_LOC.y + 3 * MathUtils.sin(theta * MathUtils.degreesToRadians);

                        EntityFactory.createEnemyLaserEmitter(xPlace, yPlace, theta);
                    }
                }

                fireTimer = 0;
                iterations++;
            }
            return iterations == 250;
        }
    }

    /**
     * {@link Action Action} that has the boss shoot a fast stream of bullets in all directions.
     */
    // TODO improve this
    public static class LunaticGun implements Action {
        private float fireTimer;
        private int iterations;

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.002f) {
                for (int i = 0; i < 8; i++) {
                    float deg = AI.actionTimer * 35;
                    float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                    float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                    final Entity BALL = EntityFactory.createEnemyBall(xPlace, yPlace, deg);
                    final SpriteComponent SPRITE = Mapper.SPRITE.get(BALL);
                    final BulletComponent BULLET = Mapper.BULLET.get(BALL);
                    final MovementComponent MOVE = Mapper.MOVEMENT.get(BALL);

                    SPRITE.SPRITES.first().setColor(1, 174 / 255f, 117 / 255f, 1);

                    MOVE.moveSpeed = MathUtils.lerp(7, 4, i / 6.0f);

                    BULLET.handler = (float dt) -> {
                        if (MOVE.moveSpeed > 4) {
                            MOVE.moveSpeed -= dt * 3;
                            if (MOVE.moveSpeed <= 4)
                                MOVE.moveSpeed = 4;
                        }
                    };
                }

                fireTimer = 0;
                iterations++;
            }
            return iterations == 500;
        }
    }

    /**
     * {@link Action Action} that has the boss shoot one emitter ball.
     */
    public static class SpiralColumnAttack implements Action {
        private float fireTimer;

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.02f) {
                final Vector2 TRANS_LOC = new Vector2(TRANSFORM.POSITION).add(TRANSFORM.ORIGIN);

                EntityFactory.createEmitterBall(TRANS_LOC.x, TRANS_LOC.y, 270);

                return true;
            }

            return false;
        }
    }

    /**
     * A {@link SimpleSpiralAttack} but all the bullets explode after a few seconds.
     */
    public static class AsteroidField implements Action {
        private float fireTimer;
        private float iterations;
        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.175f) {
                for (int i = 0; i < 6; i++) {
                    float deg = AI.actionTimer * 7 + i * 60;
                    float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                    float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                    EntityFactory.createEnemyBallExploding(xPlace, yPlace, deg);
                }

                fireTimer = 0;
                iterations++;
            }
            return iterations == 75;
        }
    }

    /**
     * Interface intended on creating actions that can be schedule with the boss.
     * This allows for better control over what the boss does/can do during certain stages
     * or events.
     *
     * @author Jared Tulayan
     */
    public interface Action {
        /**
         * Runs the task.
         *
         * @param boss the {@code Entity} that this task is scheduled for
         * @param deltaTime the amount of time passed since last frame
         * @return whether or not the task is finished
         */
        public boolean act(Entity boss, float deltaTime);
    }
}
