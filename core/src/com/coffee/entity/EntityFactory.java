package com.coffee.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.components.*;
import com.coffee.main.Application;
import com.coffee.util.Assets;
import com.coffee.util.CollisionHandler;
import com.coffee.util.Mapper;

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

        if (goAtlas == null) {
            System.out.println("Atlas == null");
        }
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

        PLAYER.fireRate = 0.1;

        return E.add(TRANSFORM).add(MOVEMENT).add(COLLIDER).add(SPRITE).add(INPUT).add(PLAYER);
    }

    public static Entity createPlayerBullet(float x, float y, float rot, Entity source) {
        final Entity E = new Entity();
        final TransformComponent TRANSFORM = new TransformComponent();
        final MovementComponent MOVEMENT = new MovementComponent();
        final SpriteComponent SPRITE = new SpriteComponent();
        final ColliderComponent COLLIDER;

        // Initialize SpriteComponent
        Sprite main = goAtlas.createSprite("bullet");
        main.setOrigin(main.getWidth(), main.getHeight() / 2);
        SPRITE.SPRITES.add(main);
        SPRITE.zIndex = -99;

        // Initialize TransformComponent
        TRANSFORM.SIZE.setSize(main.getWidth(), main.getHeight());
        TRANSFORM.ORIGIN.set(main.getOriginX(), main.getOriginY());
        TRANSFORM.POSITION.set(x, y);
        TRANSFORM.rotation = 0;

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

        return E.add(TRANSFORM).add(MOVEMENT).add(COLLIDER).add(SPRITE);
    }

    public static Entity createHealthUp() {
        final Entity E;
        final TransformComponent TRANSFORM = new TransformComponent();
        final ColliderComponent COLLIDER = new ColliderComponent(new CollisionHandler() {
            @Override
            public void enterCollision(Entity entity) {
                if (Mapper.PLAYER.has(entity)) {
                    Mapper.HEALTH.get(entity).health = Mapper.HEALTH.get(entity).MAX_HEALTH;
                }
            }

            @Override
            public void whileCollision(Entity entity) {
            }

            @Override
            public void exitCollision(Entity entity) {
            }
        });
        final SpriteComponent SPRITE;

        //Set up TransformComponent

        return null;

    }
}