package com.coffee.util;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.components.*;

/**
 * Class of actions that the boss can do.
 * This allows for some nice control over what the boss does over certain periods.
 */
public class BossActions {

    /**
     * {@link Action Action} that moves the boss to a specified location at a specified speed.
     */
    public static class Move extends Action {
        private final Vector2 TARGET_LOC;
        private final Vector2 BEGIN_LOC;
        private float timer;
        private final float SPEED;

        public Move(float speed, Vector2 loc) {
            SPEED = speed;

            BEGIN_LOC = new Vector2();
            TARGET_LOC = new Vector2(loc);
        }

        @Override
        public boolean act(Entity boss, float deltaTime) {
            final AIComponent AI = Mapper.AI.get(boss);
            final TransformComponent TRANSFORM = Mapper.TRANSFORM.get(boss);

            if (BEGIN_LOC.epsilonEquals(Vector2.Zero, 0))
                BEGIN_LOC.set(TRANSFORM.POSITION);

            timer = MathUtils.clamp(timer + deltaTime * SPEED, 0, 1);
            float perc = MathUtils.sin(timer * MathUtils.PI / 2.0f);

            TRANSFORM.POSITION.set(
                    MathUtils.lerp(BEGIN_LOC.x, TARGET_LOC.x, perc),
                    MathUtils.lerp(BEGIN_LOC.y, TARGET_LOC.y, perc)
            );

            return timer == 1;
        }
    }

    /**
     * {@link Action Action} that just has the boss do nothing for a specified amount of time.
     */
    public static class DoNothing extends Action {
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
    public static class ShotgunSpray extends Action {
        private float timer;
        boolean createdBall = false;
        private final Engine ENGINE;

        public ShotgunSpray(Engine e) {
            ENGINE = e;
        }

        public boolean act(Entity boss, float deltaTime) {
            AIComponent AI = Mapper.AI.get(boss);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(boss);

            if (!createdBall) {
                ENGINE.addEntity(EntityFactory.createEnemyShotgunBlast(TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x, TRANSFORM.POSITION.y + 32, 0));
                createdBall = true;
                timer = 2.5f;
            } else
                timer -= deltaTime;

            return createdBall && timer <= 0;
        }
    }

    /**
     * {@link Action Action} that creates a slow-moving spiral of bullets.
     */
    public static class SimpleSpiralAttack extends Action {
        private int iterations;
        private float fireTimer;
        private final Engine ENGINE;

        public SimpleSpiralAttack(Engine e) {
            ENGINE = e;
        }

        public boolean act (Entity boss, float deltaTime) {
            AIComponent AI = Mapper.AI.get(boss);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(boss);

           fireTimer += deltaTime;

            if (fireTimer >= 0.1f) {
                for (int i = 0; i < 6; i++) {
                    float deg = iterations * 7 + i * 60;
                    float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                    float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                    ENGINE.addEntity(EntityFactory.createEnemyBall(xPlace, yPlace, deg));
                }

                fireTimer = 0;
                iterations++;
            }

            return iterations == 75;
        }
    }

    /**
     * {@link Action Action} that has the boss shoot one laser at the player.
     */
    public static class SimpleLaserAttack extends Action {
        private float fireTimer;
        private int iterations;
        private final Vector2 TARGET;
        private final Engine ENGINE;
        private final Viewport VIEWPORT;

        public SimpleLaserAttack(Engine e, Viewport v) {
            ENGINE = e;
            VIEWPORT = v;

            TARGET = new Vector2(VIEWPORT.getWorldWidth() / 2, VIEWPORT.getWorldHeight() / 2);
        }

        public boolean act(Entity boss, float deltaTime) {
            AIComponent AI = Mapper.AI.get(boss);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(boss);

            fireTimer += deltaTime;

            if (iterations == 0) { // Assume that if we have not shot a bullet yet, we have not even specified a target
                final ImmutableArray<Entity> PLAYERS = ENGINE.getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class).get());
                if (PLAYERS.size() != 0) {
                    TransformComponent PLAYER_TRANS = Mapper.TRANSFORM.get(PLAYERS.first());

                    TARGET.set(PLAYER_TRANS.POSITION).add(PLAYER_TRANS.ORIGIN);
                }
            }

            if (fireTimer >= 0.001f) {
                final Vector2 SELF_LOC = new Vector2(TRANSFORM.POSITION).add(TRANSFORM.ORIGIN);

                float theta = MathUtils.atan2(TARGET.y - SELF_LOC.y, TARGET.x - SELF_LOC.x);

                float xPlace = SELF_LOC.x + 3 * MathUtils.cos(theta);
                float yPlace = SELF_LOC.y + 3 * MathUtils.sin(theta);

                final Entity E = EntityFactory.createEnemyBullet(xPlace, yPlace, theta * MathUtils.radDeg);
                final MovementComponent MOVE = Mapper.MOVEMENT.get(E);
                final BulletComponent BULLET = Mapper.BULLET.get(E);

                MOVE.moveSpeed = 10;
                BULLET.damage = 2;

                ENGINE.addEntity(E);

                fireTimer = 0;
                iterations++;
            }

            return iterations == 250;
        }
    }

    /**
     * {@link Action Action} that has the boss create bullets that launch towards the player and fade away.
     */
    public static class InvisibleHomingBulletsAttack extends Action {
        private float fireTimer;
        private final Engine ENGINE;

        public InvisibleHomingBulletsAttack(Engine e) {
            ENGINE = e;
        }

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

                    ENGINE.addEntity(EntityFactory.createHomingEnemyBullet(xPlace, yPlace, deg));
                }
                return true;
            }
            return false;
        }
    }

    /**
     * {@link Action Action} that shoots a spiral of fading bullets.
     */
    public static class TempestBloom extends Action {
        private float fireTimer;
        private int iterations;
        private final Engine ENGINE;

        public TempestBloom(Engine e) {
            ENGINE = e;
        }

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

                    final Entity E = EntityFactory.createEnemyBullet(xPlace, yPlace, deg);
                    final BulletComponent BULLET = Mapper.BULLET.get(E);
                    final SpriteComponent SPRITE = Mapper.SPRITE.get(E);
                    final MovementComponent MOVE = Mapper.MOVEMENT.get(E);

                    BULLET.handler = (float dt) -> SPRITE.SPRITES.get(0).setColor(
                        SPRITE.SPRITES.get(0).getColor().r,
                        MathUtils.clamp(SPRITE.SPRITES.get(0).getColor().g - dt, 0, 1),
                        MathUtils.clamp(SPRITE.SPRITES.get(0).getColor().b - dt, 0, 1),
                        MathUtils.clamp(SPRITE.SPRITES.get(0).getColor().a - dt / 20f, 0, 1)
                    );

                    ENGINE.addEntity(E);
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
    public static class SimpleConeAttack extends Action {
        private float fireTimer;
        private int iterations;
        private final Engine ENGINE;

        public SimpleConeAttack(Engine e) {
            ENGINE = e;
        }

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

                        final Entity E = EntityFactory.createEnemyBullet(xPlace, yPlace, deg);
                        final MovementComponent MOVEMENT = Mapper.MOVEMENT.get(E);
                        final BulletComponent BULLET = Mapper.BULLET.get(E);

                        MOVEMENT.moveSpeed = 7;
                        BULLET.handler = (float dt) -> {
                            if (MOVEMENT.moveSpeed != 2) {
                                if (MOVEMENT.moveSpeed > 2)
                                    MOVEMENT.moveSpeed -= dt * 3;
                                if (MOVEMENT.moveSpeed < 2)
                                    MOVEMENT.moveSpeed = 2;
                            }
                        };

                        ENGINE.addEntity(E);
                    }
                else
                    for (int i = 0; i < 16; i++) {
                        float deg = 195 + i * 10;
                        float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                        float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                        final Entity E = EntityFactory.createEnemyBullet(xPlace, yPlace, deg);
                        final MovementComponent MOVEMENT = Mapper.MOVEMENT.get(E);
                        final BulletComponent BULLET = Mapper.BULLET.get(E);

                        MOVEMENT.moveSpeed = 7;
                        BULLET.handler = (float dt) -> {
                            if (MOVEMENT.moveSpeed != 2) {
                                if (MOVEMENT.moveSpeed > 2)
                                    MOVEMENT.moveSpeed -= dt * 3;
                                if (MOVEMENT.moveSpeed < 2)
                                    MOVEMENT.moveSpeed = 2;
                            }
                        };

                        ENGINE.addEntity(E);
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
    public static class HomingBulletCircleAttack extends Action {
        private float fireTimer;
        private int iterations;
        private final Engine ENGINE;

        public HomingBulletCircleAttack(Engine e) {
            ENGINE = e;
        }

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

                    ENGINE.addEntity(EntityFactory.createHomingEnemyBullet(xPlace, yPlace, deg));
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
    public static class TripleLaserBallAttack extends Action {
        private float fireTimer;
        private final Engine ENGINE;
        private final Viewport VIEWPORT;

        public TripleLaserBallAttack(Engine e, Viewport v) {
            ENGINE = e;
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

                    ENGINE.addEntity(EntityFactory.createEnemyLaserEmitter(xPlace, yPlace, deg));
                }
                return true;
            }
            return false;
        }
    }

    /**
     * {@link Action Action} that has the boss create a wave of shifting spiraling bullets.
     */
    public static class ShiftingSpiralAttack extends Action {
        private float fireTimer;
        private int iterations;
        private final Engine ENGINE;

        public ShiftingSpiralAttack(Engine e) {
            ENGINE = e;
        }

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);
            HealthComponent HEALTH = Mapper.HEALTH.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.1) {
                for (int i = 0; i < 6; i++) {
                    float deg = iterations * 7 + i * 60;
                    float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                    float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                    final Entity E = EntityFactory.createEnemyBall(xPlace, yPlace, deg);
                    final MovementComponent MOVE = Mapper.MOVEMENT.get(E);
                    final SpriteComponent SPRITE = Mapper.SPRITE.get(E);
                    final BulletComponent BULLET = Mapper.BULLET.get(E);

                    SPRITE.SPRITES.first().setColor(221 / 255f, 66f / 255f, 121f / 255f, 1);

                    BULLET.handler = (float dt) -> {
                        SPRITE.SPRITES.get(0).setColor(
                                MathUtils.clamp(SPRITE.SPRITES.get(0).getColor().r - dt / 20f, 0, 1),
                                MathUtils.clamp(SPRITE.SPRITES.get(0).getColor().g - dt / 15f, 0, 1),
                                MathUtils.clamp(SPRITE.SPRITES.get(0).getColor().b + dt / 2f, 0, 1),
                                SPRITE.SPRITES.get(0).getColor().a
                        );
                        MOVE.MOVEMENT_NORMAL.rotate(dt * 24);
                        MOVE.moveSpeed += dt;
                    };

                    ENGINE.addEntity(E);
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
    public static class ReverseShiftingSpiralAttack extends Action {
        private float fireTimer;
        private int iterations;
        private final Engine ENGINE;

        public ReverseShiftingSpiralAttack(Engine e) {
            ENGINE = e;
        }

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.1) {
                for (int i = 0; i < 6; i++) {
                    float deg = iterations * 7 + i * 60;
                    float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                    float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                    final Entity E = EntityFactory.createEnemyBall(xPlace, yPlace, deg);
                    final MovementComponent MOVE = Mapper.MOVEMENT.get(E);
                    final SpriteComponent SPRITE = Mapper.SPRITE.get(E);
                    final BulletComponent BULLET = Mapper.BULLET.get(E);

                    SPRITE.SPRITES.first().setColor(221 / 255f, 66f / 255f, 121f / 255f, 1);

                    MOVE.moveSpeed = 0;

                    BULLET.handler = (float dt) -> {
                        SPRITE.SPRITES.get(0).setColor(
                                MathUtils.clamp(SPRITE.SPRITES.get(0).getColor().r - dt / 20f, 0, 1),
                                MathUtils.clamp(SPRITE.SPRITES.get(0).getColor().g + dt / 2f, 0, 1),
                                MathUtils.clamp(SPRITE.SPRITES.get(0).getColor().b - dt / 15f, 0, 1),
                                SPRITE.SPRITES.get(0).getColor().a
                        );
                        MOVE.MOVEMENT_NORMAL.rotate(-dt * 36);
                        MOVE.moveSpeed += dt * 4;
                    };

                    ENGINE.addEntity(E);
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
    // TODO We can separate this attack into two different actions.
    public static class ImperishableNight extends Action {
        private float fireTimer;
        private int iterations;
        private final Engine ENGINE;

        public ImperishableNight(Engine e) {
            ENGINE = e;
        }

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.1f) {
                float xPlace;
                float yPlace;

                if (iterations % 20 != 0) { // Spiral Attack
                    for (int i = 0; i < 12; i++) {
                        float deg = iterations * 7 + i * 30;
                        xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                        yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                        final Entity B = EntityFactory.createEnemyBall(xPlace, yPlace, deg);
                        final SpriteComponent SPRITE = Mapper.SPRITE.get(B);
                        final BulletComponent BULLET = Mapper.BULLET.get(B);
                        final MovementComponent MOVE = Mapper.MOVEMENT.get(B);

                        MOVE.moveSpeed = 2;
                        BULLET.handler = new BulletHandler() {
                            private float timer = 0;
                            @Override
                            public void update(float dt) {
                                timer += dt / 2f;
                                SPRITE.SPRITES.first().setColor(Color.RED.cpy().lerp(new Color(0, 0, 1f, 0), (MathUtils.cos(timer * MathUtils.PI2) + 1) / 2f));
                                MOVE.MOVEMENT_NORMAL.rotate(dt * 5);
                            }
                        };

                        ENGINE.addEntity(B);
                    }
                } else { // Laser
                    for (int i = 0; i < 4; i++) {
                        float deg = iterations * 7 + i * 90;
                        xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                        yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                        ENGINE.addEntity(EntityFactory.createEnemyLaserEmitter(xPlace, yPlace, deg));
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
    public static class HelixLaserAttack extends Action {
        private float fireTimer;
        private float iterations;
        private final Vector2 TARGET;
        private final Engine ENGINE;

        public HelixLaserAttack(Engine e, Viewport v) {
            ENGINE = e;
            TARGET = new Vector2(v.getWorldWidth() / 2, v.getWorldHeight() / 2);
        }

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);

            fireTimer += deltaTime;

            if (iterations == 0) {
                final ImmutableArray<Entity> PLAYERS;
                PLAYERS = ENGINE.getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class).get());
                if (PLAYERS.size() != 0) {
                    TransformComponent PLAYER_TRANS = Mapper.TRANSFORM.get(PLAYERS.first());

                    TARGET.set(PLAYER_TRANS.POSITION).add(PLAYER_TRANS.ORIGIN);
                }
            }

            if (fireTimer >= 0.001f) {
                final Vector2 SELF_LOC = new Vector2(TRANSFORM.POSITION).add(TRANSFORM.ORIGIN);

                float theta = MathUtils.atan2(TARGET.y - SELF_LOC.y, TARGET.x - SELF_LOC.x);

                float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(theta);
                float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(theta);

                final Entity B = EntityFactory.createEnemyBullet(xPlace, yPlace, theta * MathUtils.radDeg);
                final SpriteComponent SPRITE = Mapper.SPRITE.get(B);
                final BulletComponent BULLET = Mapper.BULLET.get(B);
                final MovementComponent MOVE = Mapper.MOVEMENT.get(B);

                if (iterations % 2 == 0)
                    BULLET.handler = new BulletHandler() {
                        private float timer = 0;
                        @Override
                        public void update(float dt) {
                            MOVE.MOVEMENT_NORMAL.setAngle((theta * MathUtils.radDeg) + MathUtils.cos(timer * MathUtils.PI2) * 50);
                            timer += dt * 2;
                        }
                    };
                else
                    BULLET.handler = new BulletHandler() {
                        private float timer = 0;
                        @Override
                        public void update(float dt) {
                            MOVE.MOVEMENT_NORMAL.setAngle((theta * MathUtils.radDeg) - MathUtils.cos(timer * MathUtils.PI2) * 50);
                            timer += dt * 2;
                        }
                    };

                ENGINE.addEntity(B);

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
    public static class HelixPlusAttack extends Action {
        private float timer;
        private int iterations;
        private final Vector2 TARGET;
        private final Engine ENGINE;

        public HelixPlusAttack(Engine e, Viewport v) {
            ENGINE = e;
            TARGET = new Vector2(v.getWorldWidth() / 2, v.getWorldHeight() / 2);
        }

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);

            timer += deltaTime;


            if (iterations < 250) {
                if (iterations == 0) {
                    final ImmutableArray<Entity> PLAYERS = ENGINE.getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class).get());
                    if (PLAYERS.size() != 0) {
                        TransformComponent PLAYER_TRANS = Mapper.TRANSFORM.get(PLAYERS.first());

                        TARGET.set(PLAYER_TRANS.POSITION).add(PLAYER_TRANS.ORIGIN);
                    }
                }

                if (timer >= 0.0001f) {
                    final Vector2 SELF_LOC = new Vector2(TRANSFORM.POSITION).add(TRANSFORM.ORIGIN);
                    float xPlace, yPlace;

                    if (iterations % 200 != 0) { // All laser scenarios
                        float theta = MathUtils.atan2(TARGET.y - SELF_LOC.y, TARGET.x - SELF_LOC.x);
                        xPlace = SELF_LOC.x + 3 * MathUtils.cos(theta);
                        yPlace = SELF_LOC.y + 3 * MathUtils.sin(theta);

                        final Entity B = EntityFactory.createEnemyBullet(xPlace, yPlace, theta * MathUtils.radDeg);
                        final SpriteComponent SPRITE = Mapper.SPRITE.get(B);
                        final BulletComponent BULLET = Mapper.BULLET.get(B);
                        final MovementComponent MOVE = Mapper.MOVEMENT.get(B);

                        MOVE.moveSpeed = 10;
                        BULLET.damage = 2;

                        if (iterations % 3 == 0)
                            BULLET.handler = new BulletHandler() {
                                private float timer;
                                @Override
                                public void update(float dt) {
                                    MOVE.MOVEMENT_NORMAL.setAngle((theta * MathUtils.radDeg) + MathUtils.cos(timer * MathUtils.PI2) * 50);
                                    timer += dt * 2;
                                }
                            };
                        else if (iterations % 3 == 1)
                            BULLET.handler = new BulletHandler() {
                                private float timer;
                                @Override
                                public void update(float dt) {
                                    MOVE.MOVEMENT_NORMAL.setAngle((theta * MathUtils.radDeg) - MathUtils.cos(timer * MathUtils.PI2) * 50);
                                    timer += dt * 2;
                                }
                            };
                        else if (iterations % 3 == 2)
                            BULLET.handler = null;

                        ENGINE.addEntity(B);

                    } else { // Lasers
                        for (int i = 0; i < 3; i++) {
                            float theta = iterations * 7 + i * 120;
                            xPlace = SELF_LOC.x + 3 * MathUtils.cos(theta * MathUtils.degreesToRadians);
                            yPlace = SELF_LOC.y + 3 * MathUtils.sin(theta * MathUtils.degreesToRadians);

                            ENGINE.addEntity(EntityFactory.createEnemyLaserEmitter(xPlace, yPlace, theta));
                        }
                    }

                    timer = 0;
                    iterations++;
                }
            } else {
                return timer >= 5f;
            }
            return false;
        }
    }

    /**
     * {@link Action Action} that has the boss shoot a fast stream of bullets in all directions.
     */
    // TODO improve this
    public static class LunaticGun extends Action {
        private float fireTimer;
        private int iterations;
        private final Engine ENGINE;

        public LunaticGun (Engine e) {
            ENGINE = e;
        }

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.002f) {
                for (int i = 0; i < 8; i++) {
                    float deg = iterations * 35;
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

                    ENGINE.addEntity(BALL);
                }

                fireTimer = 0;
                iterations++;
            }
            return iterations == 500;
        }
    }

    /**
     * {@link Action Action} that has the boss shoot one emitter ball that creates a spiral of bullets as it travels.
     */
    public static class SpiralColumnAttack extends Action {
        private float fireTimer;
        private final Engine ENGINE;

        public SpiralColumnAttack(Engine e) {
            ENGINE = e;
        }

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.02f) {
                final Vector2 TRANS_LOC = new Vector2(TRANSFORM.POSITION).add(TRANSFORM.ORIGIN);

                ENGINE.addEntity(EntityFactory.createEmitterBall(TRANS_LOC.x, TRANS_LOC.y, 270));
                return true;
            }
            return false;
        }
    }

    /**
     * A {@link SimpleSpiralAttack} but all the bullets explode after a few seconds.
     */
    public static class AsteroidField extends Action {
        private float fireTimer;
        private float iterations;
        private final Engine ENGINE;

        public AsteroidField(Engine e) {
            ENGINE = e;
        }

        public boolean act(Entity entity, float deltaTime) {
            AIComponent AI = Mapper.AI.get(entity);
            TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);

            fireTimer += deltaTime;

            if (fireTimer >= 0.175f) {
                for (int i = 0; i < 6; i++) {
                    float deg = iterations * 7 + i * 60;
                    float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                    float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                    ENGINE.addEntity(EntityFactory.createEnemyBallExploding(xPlace, yPlace, deg));
                }

                fireTimer = 0;
                iterations++;
            }
            return iterations == 75;
        }
    }

    /**
     * A sequence of actions that can be run. This runs basically in the same way that
     * the {@link com.coffee.entity.systems.AISystem AISystem} runs the task list.
     * This class is useful for sequences you want running parallel to another task.
     */
    public static class ActionSequence extends Action {
        private final Array<Action> SEQUENCE;
        private final float BUFFER_LENGTH;
        private int curTask;

        public ActionSequence(float bufferTime) {
            SEQUENCE = new Array<>();
            BUFFER_LENGTH = bufferTime;
        }

        public ActionSequence() {
            this(0);
        }

        @Override
        public boolean act(Entity boss, float deltaTime) {
            if (curTask < SEQUENCE.size) {
                Action a = SEQUENCE.get(curTask);

                if (!a.parallel && curTask > 0)
                    curTask = 0;
                else if (a.act(boss, deltaTime))
                    SEQUENCE.removeIndex(curTask);
                else {
                    if (curTask == SEQUENCE.size - 1)
                        curTask = 0;
                    else
                        curTask++;
                }
            }

            return SEQUENCE.size == 0;
        }

        public void addAction(Action a) {
            SEQUENCE.add(a);
            if (BUFFER_LENGTH > 0 && !a.parallel && SEQUENCE.size > 1)
                SEQUENCE.add(new DoNothing(BUFFER_LENGTH));
        }
    }

    /**
     * Abstract class intended on creating actions that can be schedule with the boss.
     * This allows for better control over what the boss does/can do during certain stages
     * or events.
     *
     * @author Jared Tulayan
     */
    public static abstract class Action {
        public boolean parallel = false;
        /**
         * Runs the task.
         *
         * @param boss the {@code Entity} that this task is scheduled for
         * @param deltaTime the amount of time passed since last frame
         * @return whether or not the task is finished
         */
        public abstract boolean act(Entity boss, float deltaTime);
    }
}
