package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
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
 * {@link EntitySystem} that updates the location of collision boxes and
 * checks for collisions.
 *
 * @author Phillip O'Reggio
 */
public class CollisionSystem extends IteratingSystem {
    private final QuadTree TREE;
    private final Array<Entity> POSSIBLE_COLLISIONS;
    private ShapeRenderer renderer;
    private Camera camera;
    public boolean isDebugging;

    public CollisionSystem(ShapeRenderer r, Viewport v, boolean debug) {
        super(Family.all(ColliderComponent.class, TransformComponent.class).get());
        TREE = new QuadTree(0, new Rectangle((v.getWorldWidth() - v.getWorldHeight()) / 2, 0, v.getWorldHeight(), v.getWorldHeight()));

        renderer = r;
        camera = v.getCamera();
        POSSIBLE_COLLISIONS = new Array<>();
    }

    public CollisionSystem(Viewport v) {
        this(null, v, false);
    }

    public void update(float deltaTime) {
        // Clear tree
        TREE.clear();

        // Fill tree
        for (Entity e : getEntities()) {
            ColliderComponent curCollider = Mapper.COLLIDER.get(e);
            TransformComponent curTrans = Mapper.TRANSFORM.get(e);

            // Centers collider onto transform based on origins
            float
                transX = curTrans.POSITION.x + curTrans.ORIGIN.x,
                transY = curTrans.POSITION.y + curTrans.ORIGIN.y,
                bodyX = curCollider.body.getBoundingRectangle().getWidth() - curCollider.body.getOriginX(),
                bodyY = curCollider.body.getBoundingRectangle().getHeight() - curCollider.body.getOriginY();

            curCollider.body.setPosition(transX - bodyX, transY - bodyY);

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

        // Clear list
        POSSIBLE_COLLISIONS.clear();

        // Get possible collisions
        TREE.retrieve(POSSIBLE_COLLISIONS, entity);

        // Check for collisions
        for (Entity e : POSSIBLE_COLLISIONS) {
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
