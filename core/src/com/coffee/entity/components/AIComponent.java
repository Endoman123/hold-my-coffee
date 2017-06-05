package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

/**
 * @author Phillip O'Reggio
 */
public class AIComponent implements Component, Pool.Poolable{
    public final Vector2 BEGIN_POS, END_POS, TARGET_LOC;
    public float lerpTimer, lerpSpeed, actionTimer, fireTimer;
    public int state;

    public AIComponent() {
        END_POS = new Vector2();
        BEGIN_POS = new Vector2();
        TARGET_LOC = new Vector2();
        lerpTimer = 0;
        lerpSpeed = 1;
        actionTimer = 0;
        fireTimer = 0;
        state = 1;
    }

    public void reset() {
        END_POS.setZero();
        BEGIN_POS.setZero();
        TARGET_LOC.setZero();
        lerpTimer = 0;
        lerpSpeed = 1;
        actionTimer = 0;
        fireTimer = 0;
        state = -1;
    }
}
