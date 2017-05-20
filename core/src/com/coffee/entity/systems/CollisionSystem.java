package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.components.ColliderComponent;
import com.coffee.entity.components.TransformComponent;
import com.coffee.util.Mapper;
import com.coffee.util.QuadTree;

/**
 * @author Phillip O'Reggio
 */
public class CollisionSystem extends IteratingSystem {
    private final QuadTree TREE;
    private ShapeRenderer renderer;
    private Camera camera;

    public CollisionSystem(Viewport v) {
        super(Family.all(ColliderComponent.class, TransformComponent.class).get());
        TREE = new QuadTree(0, new Rectangle(0, 0, 720, 1280));

        if (v != null) {
            renderer = new ShapeRenderer();
            camera = v.getCamera();
        }
    }

    public CollisionSystem() {
        this(null);
    }

    public void update(float deltaTime) {
        // Clear tree
        TREE.clear();

        // Fill tree
        for (Entity e : getEntities()) {
            TREE.insert(e);
        }

        // Debug if necessary
        if (renderer != null) {
            renderer.setProjectionMatrix(camera.combined);
            TREE.draw(renderer);
        }

        // Process entities
        super.update(deltaTime);
    }

    public void processEntity(Entity entity, float deltaTime) {
        ColliderComponent curCollider = Mapper.COLLIDER.get(entity);
        TransformComponent curTrans = Mapper.TRANSFORM.get(entity);
        Array<Entity> collisions = new Array<Entity>();

        // Update locations of colliders.
        curCollider.body.setPosition(curTrans.POSITION.x, curTrans.POSITION.y);

        // Get possible collisions
        collisions = TREE.retrieve(collisions, entity);

        // Draw collision boxes
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.RED);
        renderer.polygon(curCollider.body.getTransformedVertices());
        renderer.end();

        // Check for collisions
        for (Entity e : collisions) {
            if (e == entity)
                continue;

            ColliderComponent otherCollider = Mapper.COLLIDER.get(e);
            Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();

            if (Intersector.overlapConvexPolygons(curCollider.body, otherCollider.body, mtv)) {
                // Technically, we have entered collision.
                curCollider.HANDLER.enterCollision(e);
                otherCollider.HANDLER.enterCollision(entity);

                // If both objects are solid, move them out of each other.
                if (curCollider.solid && otherCollider.solid) {
                    curTrans.POSITION.add(mtv.normal.scl(mtv.depth));

                    curCollider.HANDLER.exitCollision(e);
                    otherCollider.HANDLER.exitCollision(entity);
                } else {

                }
            }
        }
    }
}
