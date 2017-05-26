package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.coffee.util.GUIHandler;

/**
 * Component containing a canvas and GUIHandler that allows an entity to contain a displayable
 * GUI that can be updated every game tick.
 * @author Phillip O'Reggio
 */
public class GUIComponent implements Component {
    public final Stage CANVAS;
    public GUIHandler handler;

    public GUIComponent() {
        CANVAS = new Stage();
        handler = new GUIHandler() {
            @Override
            public void update(float deltaTime) { }
        };
    }
}
