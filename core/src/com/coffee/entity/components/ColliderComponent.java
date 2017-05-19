package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.coffee.util.CollisionHandler;

/**
 * @author Phillip O'Reggio
 */
public class ColliderComponent implements Component {
    public Polygon body;
    public CollisionHandler handler;
    public boolean solid;
    public Array<Entity> collidingWith;

    /**
     * Creates component with  a polygonal square body of size 32.
     */
    public ColliderComponent() {
        body = new Polygon(new float[]{
                0,0,
                32, 0,
                32, 32,
                0, 32
        });
        solid = true;
        collidingWith = new Array<Entity>();
        handler = new CollisionHandler() {

            @Override
            public void enterCollision(Entity entity) {
                System.out.println("enter");
            }

            @Override
            public void whileCollision(Entity entity) {
                System.out.println("while");
            }

            @Override
            public void exitCollision(Entity entity) {
                System.out.println("exit");

            }
        };
    }
}
