package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.InputProcessor;

/**
 * {@link Component} that contains one {@link InputProcessor}
 * for input processing.
 *
 * @author Jared Tulayan
 */
public class InputComponent implements Component{
    public final InputProcessor PROCESSOR;

    public InputComponent(InputProcessor ip) {
        PROCESSOR = ip;
    }
}
