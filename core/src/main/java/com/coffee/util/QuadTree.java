package com.coffee.util;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.coffee.entity.components.ColliderComponent;

/**
 * Data structure that recursively grows as it stores more objects
 * by breaking itself up into four subnodes. This class represents one of those nodes.
 * Each individual node has the capability of breaking up into 4 subnodes, as well
 * as retrieving all the objects from its node downward, and other methods.
 *
 * @author Phillip O'Reggio
 */
public class QuadTree {
    private final int MAX_LEVELS = 6;
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
        objects = new Array<>();
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
        float subWidth = bounds.getWidth() / 2f;
        float subHeight = bounds.getHeight() / 2f;
        float x = bounds.getX();
        float y = bounds.getY();

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
        // Initialize initial index, midpoint coordinates, and get collider BODY from entity.
        int index = -1;
        float verticalMid = bounds.getX() + bounds.getWidth() / 2f;
        float horizontalMid = bounds.getY() + bounds.getHeight() / 2f;
        final Rectangle BODY = Mapper.COLLIDER.get(entity).BODY.getBoundingRectangle();

        // Check if it can fit into either the top quadrants or bottom quadrants.
        boolean inTopQuadrant = (BODY.getY() > horizontalMid && BODY.getY() + BODY.getHeight() > horizontalMid);
        boolean inBottomQuadrant = (BODY.getY() < horizontalMid && BODY.getY() + BODY.getHeight() < horizontalMid);

        // Check if it can fit into either right-side quadrants.
        if (BODY.getX() > verticalMid) {
            if (inTopQuadrant)
                index = 0;
            else if (inBottomQuadrant)
                index = 3;
        }
        // Check if it can fit into either left-side quadrants.
        else if (BODY.getX() < verticalMid && BODY.getX() + BODY.getWidth() < verticalMid) {
            if (inTopQuadrant)
                index = 1;
            else if (inBottomQuadrant)
                index = 2;
        }

        // Return final value
        return index;

    }

    /**
     * Inserts an {@link Entity} into the bottom most {@link QuadTree} node
     * that it can fit into.
     *
     * @param entity the {@code Entity} to place into the {@code Quadtree}.
     */
    public void insert(Entity entity) {
        // If this node has subnodes,
        // try to see if it can fit into any of them.
        if (nodes[0] != null) {
            int index = getIndex(entity);
            if (index != -1) {
                nodes[index].insert(entity);
                return;
            }
        }

        // Add the object to this node.
        objects.add(entity);

        // Check if the max capacity has been reached
        // and if we can still split down a level.
        if (objects.size > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null)
                split();

            // Iterate through all the objects in this node
            // and see if you can place them into any of the subnodes.
            int i = 0;
            while (i < objects.size) {
                int index = getIndex(objects.get(i));
                if (index != -1)
                    nodes[index].insert(objects.removeIndex(i));
                else
                    i++;
            }
        }
    }

    /**
     * Gets all the possible collisions with the specified collider.
     *
     * @param possibleCollisions an empty {@code Array<Entity>} to store all the possible entities that could be in collision
     * @param entity the {@code Entity} to check for collisions
     * @return an {@code Array<Entity>} of all the entities that could be colliding with the specified {@code Entity}
     */
    public Array<Entity> retrieve(Array<Entity> possibleCollisions, Entity entity) {
        int index = getIndex(entity);

        // Get to the bottom-most node of the tree
        if (index != -1 && nodes[0] != null)
            nodes[index].retrieve(possibleCollisions, entity);

        possibleCollisions.addAll(objects);

        return possibleCollisions;
    }

    /**
     * Debug draw method
     */
    public void draw(ShapeRenderer DEBUG) {
        DEBUG.set(ShapeRenderer.ShapeType.Line);
        DEBUG.setColor(Color.PINK);
        DEBUG.rect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());

        if (nodes[0] != null) {
            for(QuadTree node : nodes)
                node.draw(DEBUG);
        }
    }

    public String toString() {
        return "Level: " + level + " Polygons: " + objects.size;
    }
}
