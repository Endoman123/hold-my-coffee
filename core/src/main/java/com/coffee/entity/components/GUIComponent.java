package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Pool;

/**
 * Component containing a canvas and GUIHandler that allows an entity to contain a displayable
 * GUI that can be updated every game tick.
 * @author Phillip O'Reggio
 */
public class GUIComponent implements Component, Pool.Poolable {
    public Stage canvas;

    @Override
    public void reset() {
        canvas.clear();
    }
}
