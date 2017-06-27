package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.coffee.entity.components.GUIComponent;
import com.coffee.util.Mapper;

/**
 * @author Jared Tulayan
 */
public class GUISystem extends IteratingSystem {
    public GUISystem() {
        super(Family.one(GUIComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Stage canvas = Mapper.GUI.get(entity).canvas;

        canvas.act(deltaTime);
        canvas.draw();
    }
}
