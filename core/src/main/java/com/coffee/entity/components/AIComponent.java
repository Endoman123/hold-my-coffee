package com.coffee.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.coffee.util.AIState;
import com.coffee.util.BossActions;

/**
 * @author Phillip O'Reggio
 */
public class AIComponent implements Component, Pool.Poolable{
    public final Array<BossActions.Action> TASKS;
    public AIState curState;

    public AIComponent() {
        TASKS = new Array<>();
        curState = AIState.SCHEDULING;
    }

    public void reset() {
        TASKS.clear();
        curState = AIState.SCHEDULING;
    }
}
