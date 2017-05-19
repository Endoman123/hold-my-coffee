package com.coffee.util;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * @author Phillip O'Reggio
 */
public class QuadTree {
    private ShapeRenderer DEBUG;

    private final int MAX_LEVELS = 5;
    private final int MAX_POLYGONS = 10;

    private int level;
    private Array<Entity> entities;
    private Rectangle bounds;
    /** Index of each zone follow coordinate grid conventions */
    private QuadTree[] nodes;

    public QuadTree(int pLevel, Rectangle boundary) {
        bounds = boundary;
        level = pLevel;
        nodes = new QuadTree[4]; //qpu aligns itself
    }

    public void clear() {
        entities.clear();

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

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

    private void getIndex(Polygon polygon) {
        int index = -1;
        float verticalMid = bounds.getX() + (bounds.getWidth() / 2f);
        float horizontalMid = bounds.getY() + (bounds.getHeight() / 2f);

        //TODO work in progress...

    }

    private void insert() {

    }

    private Array<Entity> retreive() {
        // TODO implement
        return null;
    }

    private void draw() {

    }
}
