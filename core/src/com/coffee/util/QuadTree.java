package com.coffee.util;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.coffee.entity.components.ColliderComponent;

/**
 * Class that recursively splits up a map into quadrants for
 * optimized collision checking.
 *
 * @author Phillip O'Reggio
 */
public class QuadTree {
    private final int MAX_LEVELS = 10;
    private final int MAX_OBJECTS = 3;

    private int level;
    private Array<Entity> objects;
    private Rectangle bounds;
    /** Index of each zone follow coordinate grid conventions */
    private QuadTree[] nodes;

    /**
     * Constructor that sets the level of node as well as the boundary.
     * @param pLevel the level of the node
     * @param boundary the bounds of the node
     */
    public QuadTree(int pLevel, Rectangle boundary) {
        bounds = boundary;
        level = pLevel;
        objects = new Array<Entity>();
        nodes = new QuadTree[4];
    }

    /**
     * Recursively clears the entire {@link QuadTree}.
     */
    public void clear() {
        objects.clear();

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    /**
     * Splits the {@link QuadTree} into four quadrants/nodes.
     */
    private void split() {
        int subWidth = (int) (bounds.getWidth() / 2f);
        int subHeight = (int) (bounds.getHeight() / 2f);
        int x = (int) bounds.getX();
        int y = (int) bounds.getY();

        nodes[0] = new QuadTree(level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
        nodes[1] = new QuadTree(level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        nodes[2] = new QuadTree(level + 1, new Rectangle(x, y, subWidth, subHeight));
        nodes[3] = new QuadTree(level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight));
    }

    /**
     * Get which quadrant the {@link ColliderComponent} of this {@link Entity}
     * completely fits into.
     *
     * @param entity the {@code Entity} whose {@code ColliderComponent} to check within the node
     * @return the index of the quadrant, or -1 if it does not fit completely into any single quadrant
     */
    private int getIndex(Entity entity) {
        final Rectangle BOUNDS = Mapper.COLLIDER.get(entity).body.getBoundingRectangle();
        float verticalMid = bounds.getX() + (bounds.getWidth() / 2f);
        float horizontalMid = bounds.getY() + (bounds.getHeight() / 2f);

        boolean inTopQuadrant = (BOUNDS.getY() > horizontalMid && BOUNDS.getY() + BOUNDS.getHeight() > horizontalMid);
        boolean inBottomQuadrant = (BOUNDS.getY() < horizontalMid && BOUNDS.getY() + BOUNDS.getHeight() < horizontalMid);

        if (BOUNDS.getX() > verticalMid && BOUNDS.getX() + BOUNDS.getWidth() > verticalMid) { //right side
            if (inTopQuadrant)
                return 0;
            else if (inBottomQuadrant)
                return 3;
        } else if (BOUNDS.getX() < verticalMid && BOUNDS.getX() + BOUNDS.getWidth() < verticalMid) { //left side
            if (inTopQuadrant)
                return 1;
            else if (inBottomQuadrant)
                return 2;
        }

        return -1; //none

    }

    /**
     * Inserts an {@link Entity} into the bottom most {@link QuadTree} node
     * that it can fit into.
     * @param entity the {@code Entity} to place into the {@code Quadtree}.
     */
    public void insert(Entity entity) {
        if (nodes[0] != null) { //if it has sub nodes
            int index = getIndex(entity);
            if (index != -1)
                nodes[index].insert(entity);
            return;
        }

        objects.add(entity);
        //check if it needs to split
        if (objects.size > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null)
                split();

            for (int i = 0; i < objects.size; i++) {
                int index = getIndex(entity);
                if (index != -1)
                    nodes[index].insert(objects.removeIndex(i));
            }
        }
    }

    /**
     * Gets all the possible collisions with the specified collider.
     *
     * @param possibleCollisons an empty {@code Array<Entity>} to store all the possible entities that could be in collision
     * @param entity the {@code Entity} to check for collisions
     * @return an {@code Array<Entity>} of all the entities that could be colliding with the specified {@code Entity}
     */
    public Array<Entity> retrieve(Array<Entity> possibleCollisons, Entity entity) {
        int index = getIndex(entity);

        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(possibleCollisons, entity);
        }

        possibleCollisons.addAll(objects);
        return possibleCollisons;
    }

    /**
     * Debug draw method
     */
    public void draw(ShapeRenderer DEBUG) {
        DEBUG.begin(ShapeRenderer.ShapeType.Line);
        DEBUG.setColor(Color.CYAN);
        DEBUG.rect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        DEBUG.end();

        if (nodes[0] != null) {
            for (int i = 0; i < nodes.length; i++)
                nodes[i].draw(DEBUG);
        }
    }

    public String toString() {
        return "Level: " + level + " Polygons : " + objects.size;
    }
}
