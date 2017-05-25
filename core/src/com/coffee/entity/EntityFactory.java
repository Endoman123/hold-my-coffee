package com.coffee.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.components.*;
import com.coffee.main.Application;
import com.coffee.util.Assets;
import com.coffee.util.CollisionHandler;
import com.coffee.util.Mapper;
import com.coffee.util.SpawnerHandler;

import java.awt.*;

/**
 * Builder class that automates the creation of entities and
 * attaching the necessary {@link Component}s onto them.
 */
public class EntityFactory {
    // Please never change these values once they have been initialized.
    // I will throw a hissy fit otherwise.
    // - Game
    // gonna change viewport to null, fight me 1v1
    private static Viewport viewport;
    private static Batch batch;
    private static InputMultiplexer inputMultiplexer;
    private static TextureAtlas goAtlas;

    private static EntityFactory _inst;

    /**
     * Initializes the {@link EntityFactory} instance only if it has not been already.
     *
     * @param a the {@code Application} to feed into the {@code EntityFactory} constructor
     */
    public static void init(Application a) {
        if (_inst == null) {
            _inst = new EntityFactory(a);

            System.out.println("Initialized EntityFactory");
        }
    }

    public static void getAssets() {
        goAtlas = Assets.MANAGER.get(Assets.GameObjects.ATLAS);
    }

    /**
     * Initializes the constants in the class.
     *
     * @param app the {@code Application} to take the {@code Viewport}, {@code Engine}, {@code InputMultiplexer}, and {@code Batch} from
     */
    public EntityFactory(Application app) {
        viewport = app.getViewport();
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
        final PlayerComponent PLAYER = new PlayerComponent();
        final ColliderComponent COLLIDER;
        final InputComponent INPUT;

        // Initialize MovmementComponent
        MOVEMENT.moveSpeed = 5;

        // Initialize SpriteComponent
        Sprite main = goAtlas.createSprite("player");
        main.setOrigin(main.getWidth() / 2, main.getHeight() / 2);

        SPRITE.SPRITES.add(main);

        // Initialize TransformComponent
        TRANSFORM.SIZE.setSize(main.getWidth(), main.getHeight());
        TRANSFORM.ORIGIN.set(main.getOriginX(), main.getOriginY());

        // Initialize ColliderComponent
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
        COLLIDER.body.setOrigin(8, 8);
        COLLIDER.solid = true;

        // Initialize InputComponent
        InputProcessor ip = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.W:
                    case Input.Keys.UP:
                        PLAYER.up = 1;
                        break;
                    case Input.Keys.A:
                    case Input.Keys.LEFT:
                        PLAYER.left = 1;
                        break;
                    case Input.Keys.S:
                    case Input.Keys.DOWN:
                        PLAYER.down = 1;
                        break;
                    case Input.Keys.D:
                    case Input.Keys.RIGHT:
                        PLAYER.right = 1;
                        break;
                    case Input.Keys.SPACE:
                    case Input.Keys.SHIFT_LEFT:
                    case Input.Keys.SHIFT_RIGHT:
                        PLAYER.shoot = true;
                        break;
                    default:
                        return false;
                }
                return true;
            }

            public boolean keyUp(int keycode) {
                switch (keycode) {
                    case Input.Keys.W:
                    case Input.Keys.UP:
                        PLAYER.up = 0;
                        break;
                    case Input.Keys.A:
                    case Input.Keys.LEFT:
                        PLAYER.left = 0;
                        break;
                    case Input.Keys.S:
                    case Input.Keys.DOWN:
                        PLAYER.down = 0;
                        break;
                    case Input.Keys.D:
                    case Input.Keys.RIGHT:
                        PLAYER.right = 0;
                        break;
                    case Input.Keys.SPACE:
                    case Input.Keys.SHIFT_LEFT:
                    case Input.Keys.SHIFT_RIGHT:
                        PLAYER.shoot = false;
                        break;
                    default:
                        return false;
                }
                return true;
            }
        };

        inputMultiplexer.addProcessor(ip);

        INPUT = new InputComponent(ip);

        PLAYER.bulletsPerSecond = 10;

        return E.add(TRANSFORM).add(MOVEMENT).add(COLLIDER).add(SPRITE).add(INPUT).add(PLAYER);
    }

    public static Entity createPlayerBullet(float x, float y, float rot, Entity source) {
        final Entity E = new Entity();
        final TransformComponent TRANSFORM = new TransformComponent();
        final MovementComponent MOVEMENT = new MovementComponent();
        final SpriteComponent SPRITE = new SpriteComponent();
        final ColliderComponent COLLIDER;
        final BulletComponent BULLET = new BulletComponent();

        // Initialize SpriteComponent
        Sprite main = goAtlas.createSprite("bullet");
        main.setOrigin(main.getWidth(), main.getHeight() / 2);
        SPRITE.SPRITES.add(main);
        SPRITE.zIndex = -99;

        // Initialize TransformComponent
        TRANSFORM.SIZE.setSize(main.getWidth(), main.getHeight());
        TRANSFORM.ORIGIN.set(main.getOriginX(), main.getOriginY());
        TRANSFORM.POSITION.set(x - main.getOriginX(), y - main.getOriginY());
        TRANSFORM.rotation = rot;

        // Initialize MovementComponent
        MOVEMENT.moveSpeed = 10;
        MOVEMENT.MOVEMENT_NORMAL.set(Vector2.Y);

        // Initialize ColliderComponent
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
                4,0,
                4,4,
                0,4
        });
        COLLIDER.body.setOrigin(2, 2);
        COLLIDER.solid = false;

        return E.add(TRANSFORM).add(MOVEMENT).add(COLLIDER).add(SPRITE).add(BULLET);
    }

    /**
     * Create a spawner that randomly spawns power-ups
     * @param x x location of bottom left corner
     * @param y y location of bottom left corner
     * @param engine {@link PooledEngine}
     * @return a random power-up spawner
     */
    public static Entity createRandomPowerUpSpawner(float x, float y, PooledEngine engine) {
        final PooledEngine ENGINE = engine;
        final Entity E = new Entity();
        final TransformComponent TRANSFORM = new TransformComponent();
        final SpriteComponent SPRITE = new SpriteComponent(); //debug
        final SpawnerComponent SPAWN = new SpawnerComponent(new SpawnerHandler() {
            @Override
            public Array<Entity> spawn() {
                Array<Entity> spawnedEntities = new Array<Entity>(1);
                //Spawn a random power up within a 300x300 square of the position
                float spawnX = TRANSFORM.POSITION.x  + (float) ((Math.random() * 600) - 300);
                float spawnY = TRANSFORM.POSITION.y  + (float) ((Math.random() * 600) - 300);
                int randomPowerUpSpawned = (int) ((Math.random() * 3));

                switch (randomPowerUpSpawned) {
                    case 0: //health power up
                        spawnedEntities.add(createHealthPowerUp(spawnX, spawnY, ENGINE));
                        break;
                    case 1: //speed power up
                        spawnedEntities.add(createSpeedPowerUp(spawnX, spawnY, ENGINE));
                        break;
                    case 2: //create fire rate up
                        spawnedEntities.add(createFireRatePowerUp(spawnX, spawnY, ENGINE));
                        break;
                }
                return spawnedEntities;
            }
        });

        //Set up Sprite Component
        Sprite sprite = goAtlas.createSprite("bullet_large");
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
        SPRITE.SPRITES.add(sprite);

        //Set up Transform Component
        TRANSFORM.POSITION.x = x;
        TRANSFORM.POSITION.y = y;

        //Set up Spawn Component
        SPAWN.spawnRate = 2;

        return E.add(TRANSFORM).add(SPRITE).add(SPAWN);
    }

    /**
     * Creates a health power up that restores the player's health to max.
     */
    public static Entity createHealthPowerUp(float x, float y, PooledEngine engine) {
        final PooledEngine ENGINE = engine;
        final Entity E = new Entity();
        final TransformComponent TRANSFORM = new TransformComponent();
        final SpriteComponent SPRITE = new SpriteComponent();
        final ColliderComponent COLLIDER = new ColliderComponent(new CollisionHandler() {
            @Override
            public void enterCollision(Entity entity) {
                if (Mapper.PLAYER.has(entity)) {
                    Mapper.HEALTH.get(entity).health = Mapper.HEALTH.get(entity).MAX_HEALTH;
                    ENGINE.removeEntity(E);
                }
            }

            @Override
            public void whileCollision(Entity entity) {
            }

            @Override
            public void exitCollision(Entity entity) {
            }
        });

        //Set up Transform Component
        TRANSFORM.POSITION.x = x;
        TRANSFORM.POSITION.y = y;

        //Set up Sprite Component
        Sprite sprite = goAtlas.createSprite("bullet_ball");
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
        SPRITE.SPRITES.add(sprite);

        //Set up Collider Component
        COLLIDER.body = new Polygon(new float[] {
                0, 0,
                16, 0,
                16, 16,
                0, 16
        });
        COLLIDER.solid = false;
        COLLIDER.body.setOrigin(8, 8);

        return E.add(TRANSFORM).add(SPRITE).add(COLLIDER);
    }

    /**
     * Creates a fire rate power up that increases the player's fire rate. The effect of the power up starts to have
     * diminishing returns after 20 bullets per second.
     */
    public static Entity createFireRatePowerUp(float x, float y, PooledEngine engine) {
        final PooledEngine ENGINE = engine;
        final Entity E = new Entity();
        final TransformComponent TRANSFORM = new TransformComponent();
        final SpriteComponent SPRITE = new SpriteComponent();
        final ColliderComponent COLLIDER = new ColliderComponent(new CollisionHandler() {
            @Override
            public void enterCollision(Entity entity) {
                PlayerComponent player = Mapper.PLAYER.get(entity);
                if (player != null) {
                    player.bulletsPerSecond += 2 * MathUtils.clamp(20 / player.bulletsPerSecond, 0, 1);
                    ENGINE.removeEntity(E);
                }
            }

            @Override
            public void whileCollision(Entity entity) {
            }

            @Override
            public void exitCollision(Entity entity) {
            }
        });

        //Set up Transform Component
        TRANSFORM.POSITION.x = x;
        TRANSFORM.POSITION.y = y;

        //Set up Sprite Component
        Sprite sprite = goAtlas.createSprite("bullet_ball");
        sprite.setColor(Color.RED);
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
        SPRITE.SPRITES.add(sprite);

        //Set up Collider Component
        COLLIDER.body = new Polygon(new float[] {
                0, 0,
                16, 0,
                16, 16,
                0, 16
        });
        COLLIDER.solid = false;
        COLLIDER.body.setOrigin(8, 8);

        return E.add(TRANSFORM).add(SPRITE).add(COLLIDER);
    }

    /**
     * Creates a speed power up that increases the player's speed. The effects of the power up starts to have diminishing
     * returns after 10.
     */
    public static Entity createSpeedPowerUp(float x, float y, PooledEngine engine) {
        final PooledEngine ENGINE = engine;
        final Entity E = new Entity();
        final TransformComponent TRANSFORM = new TransformComponent();
        final SpriteComponent SPRITE = new SpriteComponent();
        final ColliderComponent COLLIDER = new ColliderComponent(new CollisionHandler() {
            @Override
            public void enterCollision(Entity entity) {
                MovementComponent move = Mapper.MOVEMENT.get(entity);
                if (move != null) {
                    move.moveSpeed += 1 * MathUtils.clamp(10 / move.moveSpeed, 0, 1);
                    ENGINE.removeEntity(E);
                }
            }

            @Override
            public void whileCollision(Entity entity) {
            }

            @Override
            public void exitCollision(Entity entity) {
            }
        });

        //Set up Transform Component
        TRANSFORM.POSITION.x = x;
        TRANSFORM.POSITION.y = y;

        //Set up Sprite Component
        Sprite sprite = goAtlas.createSprite("bullet_ball");
        sprite.setColor(Color.PINK);
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
        SPRITE.SPRITES.add(sprite);

        //Set up Collider Component
        COLLIDER.body = new Polygon(new float[] {
                0, 0,
                16, 0,
                16, 16,
                0, 16
        });
        COLLIDER.solid = false;
        COLLIDER.body.setOrigin(8, 8);

        return E.add(TRANSFORM).add(SPRITE).add(COLLIDER);

    }
}