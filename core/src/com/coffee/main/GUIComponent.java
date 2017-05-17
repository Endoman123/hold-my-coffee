package com.coffee.main;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Component containing a canvas and GUIHandler that allows an entity to contain a displayable
 * GUI that can be updated every game tick.
 * @author Phillip O'Reggio
 */
public class GUIComponent implements Component {
    public Stage CANVAS;
    public GUIHandler HANDLER;
}
