package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.coffee.entity.EntityFactory;
import com.coffee.util.AIState;
import com.coffee.util.BossActionHandler;
import com.coffee.util.BossActions;

/**
 * @author Phillip O'Reggio
 */
public class AIComponent implements Component, Pool.Poolable{
    /** Index 0 is reserved for the fake out */
    public final Array<BossActionHandler> ACTIONS;
    public final Array<BossActions.Action> TASKS;
    public final Vector2 BEGIN_POS, END_POS, TARGET_LOC;
    public float lerpTimer, lerpSpeed, actionTimer, fireTimer;
    /** -1 : move then attack   -2 : reset   -3: Determine if should chain attacks  */
    public int state;
    public AIState curState;

    public AIComponent() {
        ACTIONS = new Array<>();
        ACTIONS.add(EntityFactory.fakeOut());

        TASKS = new Array<>();
        END_POS = new Vector2();
        BEGIN_POS = new Vector2();
        TARGET_LOC = new Vector2(-999, -999);
        lerpTimer = 0;
        lerpSpeed = 1;
        actionTimer = 0;
        fireTimer = 0;
        state = 1;
    }

    public void reset() {
        ACTIONS.clear();
        ACTIONS.add(EntityFactory.fakeOut());

        TASKS.clear();

        END_POS.setZero();
        BEGIN_POS.setZero();
        TARGET_LOC.set(-999, -999);
        lerpTimer = 0;
        lerpSpeed = 1;
        actionTimer = 0;
        fireTimer = 0;
        state = 1;
    }
}
