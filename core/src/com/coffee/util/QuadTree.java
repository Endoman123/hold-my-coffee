package com.coffee.util;

import com.badlogic.gdx.graphics.Color;
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
    private Array<Polygon> polygons;
    private Rectangle bounds;
    /** Index of each zone follow coordinate grid conventions */
    private QuadTree[] nodes;

    public QuadTree(int pLevel, Rectangle boundary) {
        DEBUG = new ShapeRenderer();

        bounds = boundary;
        level = pLevel;
        nodes = new QuadTree[4]; //qpu aligns itself
    }

    public void clear() {
        polygons.clear();

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

    private int getIndex(Polygon polygon) {
        float verticalMid = bounds.getX() + (bounds.getWidth() / 2f);
        float horizontalMid = bounds.getY() + (bounds.getHeight() / 2f);

        boolean inTopQuandrant = (polygon.getY() > horizontalMid && polygon.getY() + polygon.getBoundingRectangle().getHeight() > horizontalMid);
        boolean inBottomQuadrant = (polygon.getY() < horizontalMid && polygon.getY() + polygon.getBoundingRectangle().getHeight() < horizontalMid);
        if (polygon.getX() > verticalMid && polygon.getX() + polygon.getBoundingRectangle().getWidth() > verticalMid) { //right side
            if (inTopQuandrant)
                return 0;
            else if (inBottomQuadrant)
                return 3;
        } else if (polygon.getX() < verticalMid && polygon.getX() + polygon.getBoundingRectangle().getWidth() < verticalMid) { //left side
            if (inTopQuandrant)
                return 1;
            else if (inBottomQuadrant)
                return 2;
        }

        return -1; //none

    }

    private void insert(Polygon polygon) {
        if (nodes[0] != null) { //if it has sub nodes
            int index = getIndex(polygon);
            if (index != -1)
                nodes[index].insert(polygon);
        }

        polygons.add(polygon);
        //check if it needs to split
        if (polygons.size > MAX_POLYGONS && level < MAX_LEVELS) {
            if (nodes[0] == null)
                split();

            for (int i = 0; i < polygons.size; i++) {
                int index = getIndex(polygon);
                if (index != -1)
                    nodes[index].insert(polygons.removeIndex(i));
            }
        }
    }

    private Array<Polygon> retrieve(Array<Polygon> possibleCollisons, Polygon collider) {
        int index = getIndex(collider);
        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(possibleCollisons, collider);
        }

        possibleCollisons.addAll(polygons);
        return possibleCollisons;
    }

    /**
     * Debug method
     * @param quadTree that is being drawn
     */
    private void draw(QuadTree quadTree) {
        if (nodes[0] != null) {
            for (int i = 0; i < nodes.length; i++)
                draw(nodes[i]);
        } else {
            DEBUG.begin();
            DEBUG.setColor(Color.CYAN);
            DEBUG.rect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
            DEBUG.end();
        }
    }
}
