package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;

/**
 * Component that stores the vertical and horizontal movement data.
 *
 * @author Jared Tulayan
 */
public class PlayerComponent implements Component {
    public int up = 0, down = 0, left = 0, right = 0;
}
