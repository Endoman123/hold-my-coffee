package com.coffee.entity;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.components.*;
import com.coffee.main.Application;
import com.coffee.util.Assets;
import com.coffee.util.CollisionHandler;
import com.coffee.util.Mapper;
import com.kotcrab.vis.ui.util.ColorUtils;

/**
 * Builder class that automates the creation of entities and
 * attaching the necessary {@link Component}s onto them.
 */
public class EntityFactory {
    // Static fields that we can use throughout any instance of the EntityBuilder
    private static Viewport viewport;
    private static SpriteBatch batch;
    private static InputMultiplexer inputMultiplexer;
    private static TextureAtlas goAtlas;
    private static Skin uiSkin;
    private static PooledEngine engine;

    // Boolean to check if the factory has already been pre-initialized.
    private static boolean initialized = false;

    /**
     * Initializes the {@link EntityFactory} static fields
     * only if it has not been done already.
     */
    public static void init() {
        if (!initialized) {
            Application app = (Application) Gdx.app.getApplicationListener();

            viewport = app.getViewport();
            batch = app.getBatch();
            inputMultiplexer = app.getInputMultiplexer();

            goAtlas = Assets.MANAGER.get(Assets.GameObjects.ATLAS);
            uiSkin = Assets.MANAGER.get(Assets.UI.SKIN);

            initialized = true;
        }
    }

    /**
     * Sets the {@link PooledEngine} to use for initializing poolable {@link Entity}s
     *
     * @param p the {@code PooledEngine} to use for pooling and creating poolable {@code Entity}s
     */
    public static void setEngine(PooledEngine p) {
        engine = p;
    }

    // region Player Stuff
    /**
     * Creates a player that can move and shoot. Note that you need to add the
     * {@link InputProcessor} to the game's {@link InputMultiplexer}; this builder does not do so.
     *
     * @param x the x-coordinate to start the player at
     * @param y the y-coordinate to start the player at
     * @return a player {@code Entity} that can move, shoot, and be killed.
     */
    public static Entity createPlayer(float x, float y) {
        final Entity E = new Entity();
        final TransformComponent TRANSFORM = new TransformComponent();
        final MovementComponent MOVEMENT = new MovementComponent();
        final SpriteComponent SPRITE = new SpriteComponent();
        final PlayerComponent PLAYER = new PlayerComponent();
        final HealthComponent HEALTH = new HealthComponent();
        final ColliderComponent COLLIDER = new ColliderComponent();
        final GUIComponent GUI = new GUIComponent();
        final InputComponent INPUT;

        // Initialize MovmementComponent
        MOVEMENT.moveSpeed = 5;

        // Initialize SpriteComponent
        Sprite main = goAtlas.createSprite("player");
        main.setOrigin(main.getWidth() / 2, main.getHeight() / 2);

        SPRITE.SPRITES.add(main);
        SPRITE.zIndex = -5;

        // Initialize TransformComponent
        TRANSFORM.SIZE.setSize(main.getWidth(), main.getHeight());
        TRANSFORM.ORIGIN.set(main.getOriginX(), main.getOriginY());
        TRANSFORM.POSITION.set(x - TRANSFORM.ORIGIN.x, y - TRANSFORM.ORIGIN.y);

        // Initialize ColliderComponent
        COLLIDER.handler = new CollisionHandler() {
            @Override
            public void enterCollision(Entity entity) {

            }

            @Override
            public void whileCollision(Entity entity) {

            }

            @Override
            public void exitCollision(Entity entity) {

            }
        };
        COLLIDER.BODY.setVertices(new float[]{
                0,0,
                16,0,
                16,16,
                0,16
        });
        COLLIDER.BODY.setOrigin(8, 8);
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

        INPUT = new InputComponent(ip);

        //GUI Component
        GUI.canvas = new Stage(viewport, batch);

        final Table
            TABLE = new Table(),
            HEALTH_STATS = new Table();

        final Array<Image>
                FIRE_RATE_BOOSTS = new Array<Image>(new Image[] {
                    new Image(),
                    new Image(),
                    new Image(),
                    new Image(),
                    new Image()
                }),
                BULLET_DAMAGE_BOOSTS = new Array<Image>(new Image[] {
                    new Image(),
                    new Image(),
                    new Image(),
                    new Image(),
                    new Image()
                });

        final Label
                FIRE_RATE_ID = new Label("Fire Rate:", uiSkin),
                BULLET_DAMAGE_ID = new Label("Damage:", uiSkin),
                LIFE_COUNTER = new Label("", uiSkin);

        final Image
                CONTAINER = new Image(uiSkin.getDrawable("bar_container")),
                FILL = new Image(uiSkin.getDrawable("bar_fill")),
                LIFE = new Image(goAtlas.findRegion("player"));

        final Stack
            HEALTH_BAR = new Stack(CONTAINER, FILL);

        final NinePatchDrawable BACK = new NinePatchDrawable(uiSkin.getAtlas().createPatch("button_up"));

        BACK.getPatch().setColor(Color.DARK_GRAY);
        FILL.setColor(Color.GREEN);

        TABLE.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                for (int i = 0; i < PLAYER.upFireRate; i++)
                    FIRE_RATE_BOOSTS.get(i).setDrawable(new TextureRegionDrawable(goAtlas.findRegion("up_arrow")));

                for (int i = 0; i < PLAYER.upBulletDamage; i++)
                    BULLET_DAMAGE_BOOSTS.get(i).setDrawable(new TextureRegionDrawable(goAtlas.findRegion("up_arrow")));

                LIFE_COUNTER.setText("= " + PLAYER.lives);

                FILL.setBounds(4, 4, (CONTAINER.getWidth() - 9) * HEALTH.getHealthPercent(), CONTAINER.getHeight() - 9);
                return false;
            }
        });

        HEALTH_STATS.add(HEALTH_BAR).expand().top().left().fillX().height(24).colspan(8).padBottom(5).row();
        HEALTH_STATS.add(LIFE).size(20).colspan(1);
        HEALTH_STATS.add(LIFE_COUNTER).colspan(7).align(Align.left);

        TABLE.setSkin(uiSkin);
        TABLE.setBackground(BACK);
        TABLE.bottom().pad(10).setSize(viewport.getWorldWidth(), 64);
        TABLE.add(HEALTH_STATS).expand().align(Align.left).width(160);
        //TABLE.setDebug(true, true);

/*        TABLE.add(LIFE).size(16, 16).align(Align.left).colspan(1);
        TABLE.add(LIFE_COUNTER).height(16).align(Align.left).colspan(2);*/

        /*TABLE.add(HEALTH_LBL).left().padBottom(GUI.canvas.getWidth() / 40).row();
        TABLE.add(FIRE_RATE_ID).padRight(GUI.canvas.getWidth() / 40);
        for (VisImage i : FIRE_RATE_BOOSTS)
            TABLE.add(i);
        TABLE.padBottom(GUI.canvas.getWidth() / 40).row();
        TABLE.add(BULLET_DAMAGE_ID).padRight(GUI.canvas.getWidth() / 40);
        for (VisImage i : BULLET_DAMAGE_BOOSTS)
            TABLE.add(i);*/


        GUI.canvas.addActor(TABLE);

        // Initialize PlayerComponent
        PLAYER.bulletsPerSecond = 3;

        return E.add(TRANSFORM).add(MOVEMENT).add(COLLIDER).add(SPRITE).add(INPUT).add(PLAYER).add(HEALTH).add(GUI);
    }

    /**
     * Creates a bullet with a velocity of 10 at the specified location.
     *
     * @param x      the x-coordinate of the bullet
     * @param y      the y-coordinate of the bullet
     * @return an {@code Entity} with all the necessary components for a bullet
     */
    public static Entity createPlayerBullet(float x, float y) {
        final Entity E = engine.createEntity();
        final TransformComponent TRANSFORM = engine.createComponent(TransformComponent.class);
        final MovementComponent MOVEMENT = engine.createComponent(MovementComponent.class);
        final SpriteComponent SPRITE = engine.createComponent(SpriteComponent.class);
        final ColliderComponent COLLIDER = engine.createComponent(ColliderComponent.class);
        final BulletComponent BULLET = engine.createComponent(BulletComponent.class);

        // Initialize SpriteComponent
        Sprite main = goAtlas.createSprite("bullet");
        main.setOrigin(main.getWidth(), main.getHeight() / 2);
        SPRITE.SPRITES.add(main);
        SPRITE.zIndex = -10;

        // Initialize TransformComponent
        TRANSFORM.SIZE.setSize(main.getWidth(), main.getHeight());
        TRANSFORM.ORIGIN.set(main.getOriginX(), main.getOriginY());
        TRANSFORM.POSITION.set(x - main.getOriginX(), y - main.getOriginY());
        TRANSFORM.rotation = 90;

        // Initialize MovementComponent
        MOVEMENT.moveSpeed = 10;
        MOVEMENT.MOVEMENT_NORMAL.set(Vector2.Y);

        // Initialize ColliderComponent
        COLLIDER.handler = new CollisionHandler() {
            @Override
            public void enterCollision(Entity entity) {
                if (Mapper.AI.has(entity)) {
                    HealthComponent health = Mapper.HEALTH.get(entity);
                    if (health.invincibilityTimer <= 0) {
                        health.health -= BULLET.damage;
                        engine.removeEntity(E);
                    }
                }
            }

            @Override
            public void whileCollision(Entity entity) {

            }

            @Override
            public void exitCollision(Entity entity) {

            }
        };
        COLLIDER.BODY.setVertices(new float[]{
                0,0,
                4,0,
                4,4,
                0,4
        });
        COLLIDER.BODY.setOrigin(2, 2);
        COLLIDER.solid = false;

        return E.add(TRANSFORM).add(MOVEMENT).add(COLLIDER).add(SPRITE).add(BULLET);
    }
    // endregion

    // region Enemy Damagables
    public static Entity createEnemyDamagable(float rot) {
        final Entity E = engine.createEntity();
        final TransformComponent TRANSFORM = engine.createComponent(TransformComponent.class);
        final MovementComponent MOVEMENT = engine.createComponent(MovementComponent.class);
        final SpriteComponent SPRITE = engine.createComponent(SpriteComponent.class);
        final ColliderComponent COLLIDER = engine.createComponent(ColliderComponent.class);
        final BulletComponent BULLET = engine.createComponent(BulletComponent.class);

        // Initialize MovementComponent
        MOVEMENT.moveSpeed = 4;
        MOVEMENT.MOVEMENT_NORMAL.set(Vector2.Y);
        MOVEMENT.MOVEMENT_NORMAL.setAngle(rot);

        // Initialize ColliderComponent
        COLLIDER.handler = new CollisionHandler() {
            @Override
            public void enterCollision(Entity entity) {
                if (Mapper.PLAYER.has(entity)) {
                    HealthComponent health = Mapper.HEALTH.get(entity);

                    if (health.invincibilityTimer <= 0) {
                        health.health -= BULLET.damage;
                        engine.removeEntity(E);
                    }
                }
            }

            @Override
            public void whileCollision(Entity entity) {

            }

            @Override
            public void exitCollision(Entity entity) {

            }
        };
        COLLIDER.solid = false;

        return E.add(TRANSFORM).add(MOVEMENT).add(COLLIDER).add(SPRITE).add(BULLET);
    }

    /**
     * Creates a bullet with a velocity of 8 at the specified location.
     *
     * @param x      the x-coordinate of the bullet
     * @param y      the y-coordinate of the bullet
     * @param rot    the rotation of the bullet
     * @return an {@code Entity} with all the necessary components for a bullet
     */
    public static Entity createEnemyBullet(float x, float y, float rot) {
        final Entity E = createEnemyDamagable(rot);
        final TransformComponent TRANSFORM = Mapper.TRANSFORM.get(E);
        final MovementComponent MOVEMENT = Mapper.MOVEMENT.get(E);
        final SpriteComponent SPRITE = Mapper.SPRITE.get(E);
        final ColliderComponent COLLIDER = Mapper.COLLIDER.get(E);
        final BulletComponent BULLET = Mapper.BULLET.get(E);

        // Initialize SpriteComponent
        Sprite main = goAtlas.createSprite("bullet_large");
        main.setSize(24, 24);
        main.setOriginCenter();
        SPRITE.SPRITES.add(main);
        SPRITE.zIndex = -2;

        // Initialize TransformComponent
        TRANSFORM.SIZE.setSize(main.getWidth(), main.getHeight());
        TRANSFORM.ORIGIN.set(main.getOriginX(), main.getOriginY());
        TRANSFORM.POSITION.set(x - TRANSFORM.ORIGIN.x, y - TRANSFORM.ORIGIN.y);
        TRANSFORM.rotation = rot;

        // Initialize MovementComponent
        MOVEMENT.moveSpeed = 7;

        // Set up collider
        COLLIDER.BODY.setVertices(new float[]{
                0,0,
                16,0,
                16,16,
                0,16
        });
        COLLIDER.BODY.setOrigin(8, 8);
        COLLIDER.BODY.setRotation(rot);

        // Initialize BulletComponent
        BULLET.handler = (float dt) -> {};

        return E;
    }

    /**
     * Creates a bullet with a velocity of 10 at the specified location. Does only 2 damage.
     *
     * @param x      the x-coordinate of the bullet
     * @param y      the y-coordinate of the bullet
     * @param rot    the rotation of the bullet
     * @return an {@code Entity} with all the necessary components for a bullet
     */
    public static Entity createWeakFastEnemyBullet(float x, float y, float rot) {
        final Entity E = createEnemyDamagable(rot);
        final TransformComponent TRANSFORM = Mapper.TRANSFORM.get(E);
        final MovementComponent MOVEMENT = Mapper.MOVEMENT.get(E);
        final SpriteComponent SPRITE = Mapper.SPRITE.get(E);
        final ColliderComponent COLLIDER = Mapper.COLLIDER.get(E);
        final BulletComponent BULLET = Mapper.BULLET.get(E);

        // Initialize SpriteComponent
        Sprite main = goAtlas.createSprite("bullet_large");
        main.setSize(24, 24);
        main.setOriginCenter();
        SPRITE.SPRITES.add(main);
        SPRITE.zIndex = -2;

        // Initialize TransformComponent
        TRANSFORM.SIZE.setSize(main.getWidth(), main.getHeight());
        TRANSFORM.ORIGIN.set(main.getOriginX(), main.getOriginY());
        TRANSFORM.POSITION.set(x - TRANSFORM.ORIGIN.x, y - TRANSFORM.ORIGIN.y);
        TRANSFORM.rotation = rot;

        // Initialize MovementComponent
        MOVEMENT.moveSpeed = 10;

        // Initialize BulletComponent
        BULLET.damage = 2;

        // Set up collider
        COLLIDER.BODY.setVertices(new float[]{
                0,0,
                16,0,
                16,16,
                0,16
        });
        COLLIDER.BODY.setOrigin(8, 8);
        COLLIDER.BODY.setRotation(rot);

        // Initialize BulletComponent
        BULLET.handler = (float dt) -> {};

        return E;
    }

    /**
     * Creates a bullet with a velocity of 8 at the specified location, that slows over time
     *
     * @param x      the x-coordinate of the bullet
     * @param y      the y-coordinate of the bullet
     * @param rot    the rotation of the bullet
     * @return an {@code Entity} with all the necessary components for a bullet
     */
    public static Entity createEnemyBulletSlows(float x, float y, float rot) {
        final Entity E = createEnemyDamagable(rot);
        final TransformComponent TRANSFORM = Mapper.TRANSFORM.get(E);
        final MovementComponent MOVEMENT = Mapper.MOVEMENT.get(E);
        final SpriteComponent SPRITE = Mapper.SPRITE.get(E);
        final ColliderComponent COLLIDER = Mapper.COLLIDER.get(E);
        final BulletComponent BULLET = Mapper.BULLET.get(E);

        // Initialize SpriteComponent
        Sprite main = goAtlas.createSprite("bullet_large");
        main.setSize(24, 24);
        main.setOriginCenter();
        SPRITE.SPRITES.add(main);
        SPRITE.zIndex = -2;

        // Initialize TransformComponent
        TRANSFORM.SIZE.setSize(main.getWidth(), main.getHeight());
        TRANSFORM.ORIGIN.set(main.getOriginX(), main.getOriginY());
        TRANSFORM.POSITION.set(x - TRANSFORM.ORIGIN.x, y - TRANSFORM.ORIGIN.y);
        TRANSFORM.rotation = rot;

        // Initialize MovementComponent
        MOVEMENT.moveSpeed = 7;

        // Set up collider
        COLLIDER.BODY.setVertices(new float[]{
                0,0,
                16,0,
                16,16,
                0,16
        });
        COLLIDER.BODY.setOrigin(8, 8);
        COLLIDER.BODY.setRotation(rot);

        // Initialize BulletComponent
        BULLET.handler = (float dt) -> {
            if (MOVEMENT.moveSpeed != 2) {
                if (MOVEMENT.moveSpeed > 2)
                    MOVEMENT.moveSpeed -= dt * 3;
                if (MOVEMENT.moveSpeed < 2)
                    MOVEMENT.moveSpeed = 2;
            }
        };

        return E;
    }

    /**
     * Creates a bullet with a velocity of 10 at the specified location.
     *
     * @param x      the x-coordinate of the bullet
     * @param y      the y-coordinate of the bullet
     * @param rot    the rotation of the bullet
     * @return an {@code Entity} with all the necessary components for a bullet
     */
    public static Entity createEnemyBulletFast(float x, float y, float rot) {
        final Entity E = createEnemyDamagable(rot);
        TransformComponent TRANSFORM = Mapper.TRANSFORM.get(E);
        SpriteComponent SPRITE = Mapper.SPRITE.get(E);
        ColliderComponent COLLIDER = Mapper.COLLIDER.get(E);

        // Initialize SpriteComponent
        Sprite main = goAtlas.createSprite("bullet_large");
        main.setSize(24, 24);
        main.setOriginCenter();
        SPRITE.SPRITES.add(main);
        SPRITE.zIndex = -2;

        // Initialize TransformComponent
        TRANSFORM.SIZE.setSize(main.getWidth(), main.getHeight());
        TRANSFORM.ORIGIN.set(main.getOriginX(), main.getOriginY());
        TRANSFORM.POSITION.set(x - TRANSFORM.ORIGIN.x, y - TRANSFORM.ORIGIN.y);
        TRANSFORM.rotation = rot;

        // Initialize ColliderComponent
        COLLIDER.BODY.setVertices(new float[]{
                0,0,
                16,0,
                16,16,
                0,16
        });
        COLLIDER.BODY.setOrigin(8, 8);
        COLLIDER.BODY.setRotation(rot);

        Mapper.MOVEMENT.get(E).moveSpeed = 10;

        return E;
    }

    /**
     * Creates a bullet with a velocity of 6 at the specified location. Becomes more transparent over time.
     *
     * @param x      the x-coordinate of the bullet
     * @param y      the y-coordinate of the bullet
     * @param rot    the rotation of the bullet
     * @return an {@code Entity} with all the necessary components for a bullet
     */
    public static Entity createEnemyBulletFade(float x, float y, float rot) {
        final Entity E = createEnemyDamagable(rot);

        TransformComponent TRANSFORM = Mapper.TRANSFORM.get(E);
        SpriteComponent SPRITE = Mapper.SPRITE.get(E);
        ColliderComponent COLLIDER = Mapper.COLLIDER.get(E);

        // Initialize SpriteComponent
        Sprite main = goAtlas.createSprite("bullet_large");
        main.setSize(24, 24);
        main.setOriginCenter();
        SPRITE.SPRITES.add(main);
        SPRITE.zIndex = -2;

        // Initialize TransformComponent
        TRANSFORM.SIZE.setSize(main.getWidth(), main.getHeight());
        TRANSFORM.ORIGIN.set(main.getOriginX(), main.getOriginY());
        TRANSFORM.POSITION.set(x - TRANSFORM.ORIGIN.x, y - TRANSFORM.ORIGIN.y);
        TRANSFORM.rotation = rot;

        // Initialize ColliderComponent
        COLLIDER.BODY.setVertices(new float[]{
                0,0,
                16,0,
                16,16,
                0,16
        });
        COLLIDER.BODY.setOrigin(8, 8);
        COLLIDER.BODY.setRotation(rot);

        // Initialize BulletComponent
        Mapper.BULLET.get(E).handler = (float dt) -> {
            SpriteComponent sprite = Mapper.SPRITE.get(E);
            sprite.SPRITES.get(0).setColor(
                    sprite.SPRITES.get(0).getColor().r,
                    MathUtils.clamp(sprite.SPRITES.get(0).getColor().g - dt, 0, 1),
                    MathUtils.clamp(sprite.SPRITES.get(0).getColor().b - dt, 0, 1),
                    MathUtils.clamp(sprite.SPRITES.get(0).getColor().a - dt / 20f, 0, 1)
            );
        };

        return E;
    }

    /**
     * Creates a bullet with a velocity of 6 at the specified location. Becomes more transparent over time.
     *
     * @param x      the x-coordinate of the bullet
     * @param y      the y-coordinate of the bullet
     * @param rot    the rotation of the bullet
     * @return an {@code Entity} with all the necessary components for a bullet
     */
    public static Entity createEnemyBulletExploding(float x, float y, float rot) {
        final Entity E = createEnemyDamagable(rot);

        TransformComponent TRANSFORM = Mapper.TRANSFORM.get(E);
        SpriteComponent SPRITE = Mapper.SPRITE.get(E);
        ColliderComponent COLLIDER = Mapper.COLLIDER.get(E);
        BulletComponent BULLET = Mapper.BULLET.get(E);
        MovementComponent MOVEMENT = Mapper.MOVEMENT.get(E);

        // Initialize SpriteComponent
        Sprite main = goAtlas.createSprite("energy_ball");
        main.setSize(64, 64);
        main.setOriginCenter();
        main.setScale(0);
        main.setColor(Color.CYAN);
        SPRITE.SPRITES.add(main);
        SPRITE.zIndex = -2;

        // Initialize TransformComponent
        TRANSFORM.SIZE.setSize(main.getWidth(), main.getHeight());
        TRANSFORM.ORIGIN.set(main.getOriginX(), main.getOriginY());
        TRANSFORM.POSITION.set(x - TRANSFORM.ORIGIN.x, y - TRANSFORM.ORIGIN.y);
        TRANSFORM.rotation = rot;

        // Initialize MovementComponent
        MOVEMENT.moveSpeed = 0;

        // Initialize ColliderComponent
        COLLIDER.handler = new CollisionHandler() {
            @Override
            public void enterCollision(Entity entity) {
                if (Mapper.PLAYER.has(entity)) {
                    HealthComponent health = Mapper.HEALTH.get(entity);

                    health.health -= BULLET.damage;
                }
            }

            @Override
            public void whileCollision(Entity entity) {

            }

            @Override
            public void exitCollision(Entity entity) {

            }
        };

        COLLIDER.BODY.setVertices(new float[]{
                0,0,
                TRANSFORM.SIZE.width / 1.41421356f,0,
                TRANSFORM.SIZE.width / 1.41421356f,TRANSFORM.SIZE.height / 1.41421356f,
                0,TRANSFORM.SIZE.height / 1.41421356f
        });
        COLLIDER.BODY.setOrigin(COLLIDER.BODY.getBoundingRectangle().getWidth() / 2, COLLIDER.BODY.getBoundingRectangle().getHeight() / 2);
        COLLIDER.BODY.setRotation(rot);

        // Initialize BulletComponent
        BULLET.handler = (float dt) -> {
            if (BULLET.timer > 0) {
                BULLET.timer -= dt;
                float scale = MathUtils.clamp(3 - BULLET.timer, 0, 1);
                main.setScale(scale);
                COLLIDER.BODY.setScale(scale, scale);
                if (BULLET.timer <= 0) {
                    for (int i = 0; i < 20; i++) {
                        float deg = 257.5f + MathUtils.random(25);
                        float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(deg * MathUtils.degreesToRadians);
                        float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(deg * MathUtils.degreesToRadians);

                        engine.addEntity(EntityFactory.createEnemyBulletFast(xPlace, yPlace, deg));
                    }
                    engine.removeEntity(E);
                }
            }
        };

        BULLET.timer = 3;

        return E;
    }

    /**
     * Creates a bullet that moves then bursts into more bullets
     *
     * @param x      the x-coordinate of the bullet
     * @param y      the y-coordinate of the bullet
     * @param rot    the rotation of the bullet
     * @return an {@code Entity} with all the necessary components for a bullet
     */
    public static Entity createEnemyBulletExplodingMoving(float x, float y, float rot) {
        final Entity E = createEnemyDamagable(rot);

        TransformComponent TRANSFORM = Mapper.TRANSFORM.get(E);
        SpriteComponent SPRITE = Mapper.SPRITE.get(E);
        ColliderComponent COLLIDER = Mapper.COLLIDER.get(E);
        BulletComponent BULLET = Mapper.BULLET.get(E);
        MovementComponent MOVEMENT = Mapper.MOVEMENT.get(E);

        // Initialize SpriteComponent
        Sprite main = goAtlas.createSprite("energy_ball");
        main.setSize(32, 32);
        main.setOriginCenter();
        main.setScale(0);
        main.setColor(Color.CYAN);
        SPRITE.SPRITES.add(main);
        SPRITE.zIndex = -2;

        // Initialize TransformComponent
        TRANSFORM.SIZE.setSize(main.getWidth(), main.getHeight());
        TRANSFORM.ORIGIN.set(main.getOriginX(), main.getOriginY());
        TRANSFORM.POSITION.set(x - TRANSFORM.ORIGIN.x, y - TRANSFORM.ORIGIN.y);
        TRANSFORM.rotation = rot;

        // Initialize MovementComponent
        MOVEMENT.moveSpeed = MathUtils.random(.8f, 2.2f);

        // Initialize ColliderComponent
        COLLIDER.handler = new CollisionHandler() {
            @Override
            public void enterCollision(Entity entity) {
                if (Mapper.PLAYER.has(entity)) {
                    HealthComponent health = Mapper.HEALTH.get(entity);

                    health.health -= BULLET.damage;
                }
            }

            @Override
            public void whileCollision(Entity entity) {

            }

            @Override
            public void exitCollision(Entity entity) {

            }
        };

        COLLIDER.BODY.setVertices(new float[]{
                0,0,
                TRANSFORM.SIZE.width / 1.41421356f,0,
                TRANSFORM.SIZE.width / 1.41421356f,TRANSFORM.SIZE.height / 1.41421356f,
                0,TRANSFORM.SIZE.height / 1.41421356f
        });
        COLLIDER.BODY.setOrigin(COLLIDER.BODY.getBoundingRectangle().getWidth() / 2, COLLIDER.BODY.getBoundingRectangle().getHeight() / 2);
        COLLIDER.BODY.setRotation(rot);

        // Initialize BulletComponent
        BULLET.handler = (float dt) -> {
            if (BULLET.timer > 2 && BULLET.timer <= 4) { //shrink
                BULLET.timer -= dt;
                float scale = MathUtils.clamp((4 - BULLET.timer) / 2f, 0, 1);
                main.setScale(scale);
                COLLIDER.BODY.setScale(scale, scale);

            } else if (BULLET.timer > 0 && BULLET.timer <= 2) { //shoot at center of screen
                BULLET.timer -= dt;
                MOVEMENT.moveSpeed = 0;

                float theta = MathUtils.radiansToDegrees * MathUtils.atan2(viewport.getWorldHeight() / 2 - (TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y), viewport.getWorldWidth() / 2 - (TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x));
                float xPlace = TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x + 3 * MathUtils.cos(theta * MathUtils.degreesToRadians);
                float yPlace = TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y + 3 * MathUtils.sin(theta * MathUtils.degreesToRadians);
                engine.addEntity(EntityFactory.createWeakFastEnemyBullet(xPlace, yPlace, theta));

            } else { //remove
                engine.removeEntity(E);
            }
        };

        BULLET.timer = 4;

        return E;
    }

    /**
     * Creates a bullet with a velocity of 4 at the specified location. It slows, points at the player
     * then moves towards them while becoming transparent.
     *
     * @param x      the x-coordinate of the bullet
     * @param y      the y-coordinate of the bullet
     * @param rot    the rotation of the bullet
     * @return an {@code Entity} with all the necessary components for a bullet
     */
    public static Entity createHomingEnemyBullet(float x, float y, float rot) {
        final Entity E = createEnemyDamagable(rot);

        TransformComponent TRANSFORM = Mapper.TRANSFORM.get(E);
        SpriteComponent SPRITE = Mapper.SPRITE.get(E);
        ColliderComponent COLLIDER = Mapper.COLLIDER.get(E);
        MovementComponent MOVEMENT = Mapper.MOVEMENT.get(E);
        BulletComponent BULLET = Mapper.BULLET.get(E);

        // Initialize SpriteComponent
        Sprite main = goAtlas.createSprite("energy_ball");
        main.setColor(122 / 255f, 1, 104 / 255f, 1);
        main.setSize(24, 24);
        main.setOriginCenter();
        SPRITE.SPRITES.add(main);
        SPRITE.zIndex = -2;

        // Initialize TransformComponent
        TRANSFORM.SIZE.setSize(main.getWidth(), main.getHeight());
        TRANSFORM.ORIGIN.set(main.getOriginX(), main.getOriginY());
        TRANSFORM.POSITION.set(x - TRANSFORM.ORIGIN.x, y - TRANSFORM.ORIGIN.y);
        TRANSFORM.rotation = rot;

        // Initialize MovementComponent
        MOVEMENT.moveSpeed = 4;

        // Initialize ColliderComponent
        COLLIDER.BODY.setVertices(new float[]{
                0,0,
                16,0,
                16,16,
                0,16
        });
        COLLIDER.BODY.setOrigin(8, 8);
        COLLIDER.BODY.setRotation(rot);

        // Initialize BulletComponent
        BULLET.damage = 5;

        /*
        Slows down when bullet's speed is between 0 (exclusive) and 4.
        Slowly turns towards player when speed is 0.
        When it faces player, it changes speed to 6, and changes to red.
         */
        BULLET.handler  = (float dt) -> {
            if (MOVEMENT.moveSpeed > 0 && MOVEMENT.moveSpeed <= 4) { // slow down slowly
                MOVEMENT.moveSpeed = MathUtils.clamp(MOVEMENT.moveSpeed - dt * 4, 0, MOVEMENT.moveSpeed);
                if (MOVEMENT.moveSpeed == 0)
                    BULLET.timer = 1;
            } else if (MOVEMENT.moveSpeed == 0) { // Move towards player
                if (BULLET.timer > 0) {
                    BULLET.timer -= dt;
                    if (BULLET.timer <= 0)
                        BULLET.timer = -1;
                } else if (BULLET.timer == -1) {
                    final ImmutableArray<Entity> PLAYERS = engine.getEntitiesFor(Family.one(PlayerComponent.class).get());
                    final Vector2
                            LOC = new Vector2(TRANSFORM.POSITION.cpy().add(TRANSFORM.ORIGIN)),
                            TARGET = new Vector2();

                    if (PLAYERS.size() > 0 && Mapper.TRANSFORM.has(PLAYERS.first())) { // If there is a player to target
                        final TransformComponent PLAYER_TRANS = Mapper.TRANSFORM.get(PLAYERS.first());
                        TARGET.set(PLAYER_TRANS.POSITION).add(PLAYER_TRANS.ORIGIN);
                    }

                    float theta = MathUtils.atan2(TARGET.y - LOC.y, TARGET.x - LOC.x);

                    MOVEMENT.moveSpeed = 6;
                    MOVEMENT.MOVEMENT_NORMAL.setAngleRad(theta);
                }
            } else if (MOVEMENT.moveSpeed == 6) {
                SPRITE.SPRITES.get(0).setColor(
                        MathUtils.clamp(SPRITE.SPRITES.get(0).getColor().r + dt, 0, 1),
                        MathUtils.clamp(SPRITE.SPRITES.get(0).getColor().g - dt, 0, 1),
                        MathUtils.clamp(SPRITE.SPRITES.get(0).getColor().b - dt, 0, 1),
                        MathUtils.clamp(SPRITE.SPRITES.get(0).getColor().a - dt * 0.5f, 0, 1)
                );
            }
        };

        return E;
    }


    /**
     * Creates an energy ball with a velocity of 3 at the specified location.
     *
     * @param x      the x-coordinate of the bullet
     * @param y      the y-coordinate of the bullet
     * @param dir    the rotation of the bullet
     * @return an {@code Entity} with all the necessary components for a bullet
     */
    public static Entity createEnemyBall(float x, float y, float dir) {
        final Entity E = createEnemyDamagable(dir);
        final TransformComponent TRANSFORM = Mapper.TRANSFORM.get(E);
        final MovementComponent MOVEMENT = Mapper.MOVEMENT.get(E);
        final SpriteComponent SPRITE = Mapper.SPRITE.get(E);
        final ColliderComponent COLLIDER = Mapper.COLLIDER.get(E);
        final BulletComponent BULLET = Mapper.BULLET.get(E);

        // Initialize SpriteComponent
        Sprite main = goAtlas.createSprite("energy_ball");
        main.setColor(191 / 255f, 106 / 255f, 221 / 255f, 1);
        main.setSize(16, 16);
        main.setOriginCenter();
        SPRITE.SPRITES.add(main);
        SPRITE.zIndex = -2;

        // Initialize TransformComponent
        TRANSFORM.SIZE.setSize(main.getWidth(), main.getHeight());
        TRANSFORM.ORIGIN.set(main.getOriginX(), main.getOriginY());
        TRANSFORM.POSITION.set(x - TRANSFORM.ORIGIN.x, y - TRANSFORM.ORIGIN.y);

        // Initialize MovementComponent
        MOVEMENT.moveSpeed = 3;

        // Initialize ColliderComponent
        COLLIDER.BODY.setVertices(new float[]{
                0,0,
                TRANSFORM.SIZE.width / 1.41421356f,0,
                TRANSFORM.SIZE.width / 1.41421356f,TRANSFORM.SIZE.height / 1.41421356f,
                0,TRANSFORM.SIZE.height / 1.41421356f
        });
        COLLIDER.BODY.setOrigin(COLLIDER.BODY.getBoundingRectangle().getWidth() / 2, COLLIDER.BODY.getBoundingRectangle().getHeight() / 2);
        COLLIDER.BODY.setRotation(dir);

        return E;
    }
    // endregion

    // region Powerups
    /**
     * Create a spawner that randomly spawns power-ups
     *
     * @param x x location of bottom left corner
     * @param y y location of bottom left corner
     * @param engine {@link PooledEngine}
     * @return a random power-up spawner
     */
    public static Entity createRandomPowerUpSpawner(float x, float y, PooledEngine engine) {
        final PooledEngine ENGINE = engine;
        final Entity E = new Entity();
        final TransformComponent TRANSFORM = new TransformComponent();
        final Sprite REF = new Sprite(goAtlas.createSprite("upgrade_base"));
        final SpawnerComponent SPAWNER;

        //Set up Transform Component
        TRANSFORM.POSITION.set(x, y);

        //Set up Spawn Component
        SPAWNER = new SpawnerComponent(() -> {
            Array<Entity> spawnedEntities = new Array<Entity>(1);

            // Spawn at the top of the screen. Make sure that it isn't out of the reach of the player.
            float spawnX = MathUtils.random(REF.getWidth(), viewport.getWorldWidth() - REF.getWidth() * 2);
            float spawnY = viewport.getWorldHeight() + 32;

            if (MathUtils.randomBoolean(0.5f))
                spawnedEntities.add(createDamagePowerUp(spawnX, spawnY, ENGINE));
            else if (MathUtils.randomBoolean(0.5f))
                spawnedEntities.add(createSpeedPowerUp(spawnX, spawnY, ENGINE));
            else
                spawnedEntities.add(createFireRatePowerUp(spawnX, spawnY, ENGINE));

            return spawnedEntities;
        });

        SPAWNER.spawnRateMin = 20;
        SPAWNER.spawnRateMax = 40;

        SPAWNER.timer = MathUtils.random(SPAWNER.spawnRateMin, SPAWNER.spawnRateMax);

        return E.add(TRANSFORM).add(SPAWNER);
    }

    /**
     * Builder method for the basis of any powerup
     *
     * @param x the x-coordinate to spawn the powerup at
     * @param y the y-coordinate to spawn the powerup at
     * @return an {@code Entity} with the necessary {@code Component}s attached
     *         to make any powerup (sans the {@code ColliderComponent}).
     */
    public static Entity createBasePowerUp(float x, float y) {
        final Entity E = engine.createEntity();
        final TransformComponent TRANSFORM = engine.createComponent(TransformComponent.class);
        final MovementComponent MOVEMENT = engine.createComponent(MovementComponent.class);
        final ColliderComponent COLLIDER = engine.createComponent(ColliderComponent.class);
        final SpriteComponent SPRITE = engine.createComponent(SpriteComponent.class);
        final LifetimeComponent LIFETIME = engine.createComponent(LifetimeComponent.class);

        //Set up Sprite Component
        Sprite
            base = goAtlas.createSprite("upgrade_base"),
            up = goAtlas.createSprite("up_arrow");

        base.setOriginCenter();
        up.setOriginCenter();

        SPRITE.SPRITES.add(base);
        SPRITE.SPRITES.add(up);

        SPRITE.zIndex = -3;

        // Set up TransformComponent
        TRANSFORM.SIZE.setSize(base.getWidth(), base.getHeight());
        TRANSFORM.ORIGIN.set(base.getOriginX(), base.getOriginY());
        TRANSFORM.POSITION.set(x - TRANSFORM.ORIGIN.x, y - TRANSFORM.ORIGIN.y);

        // Set up ColliderComponent pt 1
        COLLIDER.BODY.setVertices(new float[] {
                0, 0,
                TRANSFORM.SIZE.width, 0,
                TRANSFORM.SIZE.width, TRANSFORM.SIZE.height,
                0, TRANSFORM.SIZE.height
        });
        COLLIDER.solid = false;
        COLLIDER.BODY.setOrigin(TRANSFORM.ORIGIN.x, TRANSFORM.ORIGIN.y);

        // Set up MovementComponent
        MOVEMENT.MOVEMENT_NORMAL.set(0, -1);
        MOVEMENT.moveSpeed = 4;

        // Set up powerup lifetime
        LIFETIME.timer = 10;

        return E.add(TRANSFORM).add(SPRITE).add(MOVEMENT).add(COLLIDER).add(LIFETIME);
    }

    /**
     * Creates a power up that upgrades the damage output of the player.
     */
    public static Entity createDamagePowerUp(float x, float y, Engine engine) {
        final Entity E = createBasePowerUp(x, y);
        final ColliderComponent COLLIDER = Mapper.COLLIDER.get(E);
        final SpriteComponent SPRITE = Mapper.SPRITE.get(E);

        // Set up base sprite
        SPRITE.SPRITES.get(0).setColor(Color.RED);

        //Set up Collider Component
        COLLIDER.handler = new CollisionHandler() {
            @Override
            public void enterCollision(Entity entity) {
                if (Mapper.PLAYER.has(entity) && Mapper.HEALTH.get(entity).getHealthPercent() > 0) {
                    PlayerComponent player = Mapper.PLAYER.get(entity);
                    if (player.upBulletDamage < 4) {
                        player.upBulletDamage++;
                        System.out.println("Damage up!");
                    }
                    engine.removeEntity(E);
                }
            }

            @Override
            public void whileCollision(Entity entity) {
            }

            @Override
            public void exitCollision(Entity entity) {
            }
        };

        return E;
    }

    /**
     * Creates a fire rate power up that increases the player's fire rate. Stacks up to 5 times.
     */
    public static Entity createFireRatePowerUp(float x, float y, PooledEngine engine) {
        final PooledEngine ENGINE = engine;
        final Entity E = createBasePowerUp(x, y);
        final SpriteComponent SPRITE = Mapper.SPRITE.get(E);
        final ColliderComponent COLLIDER = Mapper.COLLIDER.get(E);

        // Set up base sprite
        SPRITE.SPRITES.get(0).setColor(Color.YELLOW);

        //Set up Collider Component
        COLLIDER.handler = new CollisionHandler() {
            @Override
            public void enterCollision(Entity entity) {
                if (Mapper.PLAYER.has(entity) && Mapper.HEALTH.get(entity).getHealthPercent() > 0) {
                    PlayerComponent player = Mapper.PLAYER.get(entity);
                    if (player.upFireRate < 4) {
                        player.upFireRate++;
                        System.out.println("Bullet Up!");
                    }
                    ENGINE.removeEntity(E);
                }
            }

            @Override
            public void whileCollision(Entity entity) {
            }

            @Override
            public void exitCollision(Entity entity) {
            }
        };

        return E;
    }

    /**
     * Creates a speed power up that increases the player's speed. Stacks up to 5 times.
     */
    public static Entity createSpeedPowerUp(float x, float y, PooledEngine engine) {
        final PooledEngine ENGINE = engine;
        final Entity E = createBasePowerUp(x, y);
        final SpriteComponent SPRITE = Mapper.SPRITE.get(E);
        final ColliderComponent COLLIDER = Mapper.COLLIDER.get(E);

        // Set up base sprite
        SPRITE.SPRITES.get(0).setColor(Color.CYAN);

        //Set up Collider Component
        COLLIDER.handler = new CollisionHandler() {
            @Override
            public void enterCollision(Entity entity) {
                if (Mapper.PLAYER.has(entity) && Mapper.HEALTH.get(entity).getHealthPercent() > 0) {
                    PlayerComponent player = Mapper.PLAYER.get(entity);

                    if (player.upSpeed < 4) {
                        player.upSpeed++;
                        ENGINE.removeEntity(E);
                    }
                }
            }

            @Override
            public void whileCollision(Entity entity) {
            }

            @Override
            public void exitCollision(Entity entity) {
            }
        };

        return E;
    }
    // endregion

    // region Particles
    /**
     * Creates a star entity.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the virtual depth of the star
     * @return an {@code Entity} made to look like a star with a random burning temperature,
     *         with its size & speed made proportional to its depth
     */
    public static Entity createStar(float x, float y, int z) {
        final Entity E = engine.createEntity();
        final TransformComponent TRANSFORM = engine.createComponent(TransformComponent.class);
        final MovementComponent MOVEMENT = engine.createComponent(MovementComponent.class);
        final SpriteComponent SPRITE = engine.createComponent(SpriteComponent.class);
        final LifetimeComponent LIFETIME = engine.createComponent(LifetimeComponent.class);

        // Clamp z between 0 and 100
        z = MathUtils.clamp(z, 0, 100);

        // Create star sprite
        Sprite main = new Sprite(goAtlas.createSprite("star1"));
        float temp = (float)Math.pow(MathUtils.random(), 1);
        Color tint = ColorUtils.HSVtoRGB(MathUtils.lerp(0, 300, temp), (int)MathUtils.lerp(25, 0, temp), 100);
        float size = MathUtils.lerp(1, 6, (100 - z) / 100f);

        main.setSize(size, size);
        main.setOriginCenter();
        main.setColor(tint);

        SPRITE.SPRITES.add(main);
        SPRITE.zIndex = -100 - z;

        // Initialize transform
        TRANSFORM.SIZE.setSize(main.getWidth(), main.getHeight());
        TRANSFORM.ORIGIN.set(main.getOriginX(), main.getOriginY());
        TRANSFORM.POSITION.set(x - TRANSFORM.ORIGIN.x, y - TRANSFORM.ORIGIN.y);

        // Initialize movement
        MOVEMENT.MOVEMENT_NORMAL.set(0, -1);
        MOVEMENT.moveSpeed = MathUtils.lerp(3, 6, (100 - z) / 100f);

        // Initialize particle lifetime
        LIFETIME.timer = 5;

        return E.add(TRANSFORM).add(MOVEMENT).add(SPRITE).add(LIFETIME);
    }

    /**
     * Creates a particle generator that simulates traveling through space.
     * This also acts as a background for the game.
     *
     * @return an {@link Entity} with a {@link SpawnerComponent} configured to spawn stars, asteroids, etc.
     */
    public static Entity createParticleGenerator() {
        final Entity E = new Entity();
        final TransformComponent TRANSFORM = new TransformComponent();
        final SpriteComponent SPRITE = new SpriteComponent();
        final SpawnerComponent SPAWNER;

        // Initialize SpriteComponent
        final Pixmap PIX = new Pixmap((int)viewport.getWorldWidth(), (int)viewport.getWorldHeight(), Pixmap.Format.RGBA8888);
        final Texture TEX;
        final Sprite SPR;

        PIX.setColor(0, 0, 10 / 255f, 1);
        PIX.fill();
        TEX = new Texture(PIX);
        SPR = new Sprite(TEX);

        SPR.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        SPR.setOriginCenter();
        SPRITE.SPRITES.add(SPR);
        SPRITE.zIndex = -999;

        TRANSFORM.SIZE.setSize(SPR.getWidth(), SPR.getHeight());
        TRANSFORM.POSITION.set(0, 0);
        TRANSFORM.ORIGIN.set(SPR.getOriginX(), SPR.getOriginY());

        SPAWNER = new SpawnerComponent(() -> {
            Array<Entity> entities = new Array<>();

            float x = MathUtils.random(0, viewport.getWorldWidth());
            float y = viewport.getWorldHeight();
            int z = MathUtils.random(0, 100);

            entities.add(createStar(x, y, z));

            return entities;
        });

        SPAWNER.spawnRateMin = 0.02f;
        SPAWNER.spawnRateMax = 0.05f;

        return E.add(TRANSFORM).add(SPAWNER).add(SPRITE);
    }
    // endregion

    /**
     * Creates a boss entity complete with AI, displayable GUI, and other things.
     *
     * @param x the x-coordinate to spawn the enemy at
     * @param y the y-coordinate to spawn the enemy at
     * @return the boss {@code Entity} with all the necessary components needed to be a boss
     */
    public static Entity createBossShip(float x, float y) {
        final Entity E = new Entity();
        final TransformComponent TRANSFORM = new TransformComponent();
        final MovementComponent MOVEMENT = new MovementComponent();
        final SpriteComponent SPRITE = new SpriteComponent();
        final HealthComponent HEALTH = new HealthComponent();
        final ColliderComponent COLLIDER = new ColliderComponent();
        final AIComponent AI = new AIComponent();
        final GUIComponent GUI = new GUIComponent();

        // Initialize MovmementComponent
        MOVEMENT.rotSpeed = 2;

        // Initialize SpriteComponent
        Sprite main = goAtlas.createSprite("enemy");
        main.setOriginCenter();

        SPRITE.SPRITES.add(main);

        // Initialize TransformComponent
        TRANSFORM.SIZE.setSize(main.getWidth(), main.getHeight());
        TRANSFORM.ORIGIN.set(main.getOriginX(), main.getOriginY());
        TRANSFORM.POSITION.set(x - TRANSFORM.ORIGIN.x, y - TRANSFORM.ORIGIN.y);

        // Initialize ColliderComponent
        COLLIDER.handler = new CollisionHandler() {
            @Override
            public void enterCollision(Entity entity) {

            }

            @Override
            public void whileCollision(Entity entity) {

            }

            @Override
            public void exitCollision(Entity entity) {

            }
        };

        COLLIDER.BODY.setVertices(new float[]{
                0,0,
                64, 0,
                64, 64,
                0, 64
        });
        COLLIDER.BODY.setOrigin(32, 32);
        COLLIDER.solid = true;

        // Initialize AIComponent
        AI.END_POS.set(
                MathUtils.random(viewport.getWorldWidth() - TRANSFORM.SIZE.width),
                MathUtils.random(viewport.getWorldHeight() * 2.0f / 3.0f, viewport.getWorldHeight() - TRANSFORM.SIZE.height)
        );
        AI.BEGIN_POS.set(TRANSFORM.POSITION);
        AI.lerpSpeed = 1.6f;
        AI.state = -1;

        // Initialize HealthComponent
        HEALTH.maxHealth = 10000;
        HEALTH.health = 10000;

        //GUI Component
        GUI.canvas = new Stage(viewport, batch);

        final Image
                CONTAINER = new Image(uiSkin.getPatch("bar_container")),
                FILL = new Image(uiSkin.getPatch("bar_fill"));

        final Table TABLE = new Table();
        final Stack HEALTH_BAR = new Stack(CONTAINER, FILL);
        final Label HEALTH_LBL = new Label("BOSS", uiSkin, "title");

        CONTAINER.setFillParent(true);
        FILL.setColor(Color.PURPLE);
        HEALTH_LBL.setAlignment(Align.center);

        TABLE.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                FILL.setPosition(4, 4);
                FILL.setSize((CONTAINER.getWidth() - 9) * HEALTH.getHealthPercent(), CONTAINER.getHeight() - 9);
                return false;
            }
        });

        TABLE.setSkin(uiSkin);
        TABLE.top().pad(20).setFillParent(true);
        TABLE.add(HEALTH_LBL).expandX().fillX().row();
        TABLE.add(HEALTH_BAR).size(300, 20);
        // TABLE.debug();

        GUI.canvas.addActor(TABLE);

        //HEALTH.health = (int) (HEALTH.maxHealth * .30);

        return E.add(TRANSFORM).add(MOVEMENT).add(COLLIDER).add(SPRITE).add(HEALTH).add(AI).add(GUI);
    }
}