package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

/**
 * @author Phillip O'Reggio
 */
public class AIComponent implements Component, Pool.Poolable{
    public final Vector2 BEGIN_POS, END_POS;
    public float lerpTimer, actionTimer, fireTimer;

    public AIComponent() {
        END_POS = new Vector2();
        BEGIN_POS = new Vector2();
        lerpTimer = 0;
        actionTimer = 0;
        fireTimer = 0;
    }

    public void reset() {
        END_POS.setZero();
        BEGIN_POS.setZero();
        lerpTimer = 0;
        actionTimer = 0;
        fireTimer = 0;
    }
}
