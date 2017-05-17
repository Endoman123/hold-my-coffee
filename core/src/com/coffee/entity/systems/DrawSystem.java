package com.coffee.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.coffee.entity.components.GUIComponent;
import com.coffee.entity.components.SpriteComponent;
import com.coffee.main.Application;

import java.util.Comparator;

/**
 * Sorts all the entities with sprite components by z-order, then draws all of them.
 * Next, GUI elements are drawn so that they are always on top of sprites.
 * @author Phillip O'Reggio
 */
public class DrawSystem extends SortedIteratingSystem {
    final Application PARENT;

    //this should be in a static mapper class that didn't exist at the time of writing
    ComponentMapper<SpriteComponent> spriteMapper = ComponentMapper.getFor(SpriteComponent.class);
    ComponentMapper<GUIComponent> GUIMapper = ComponentMapper.getFor(GUIComponent.class);

    public DrawSystem(Application application) {
        super(Family.one(SpriteComponent.class, GUIComponent.class).get(), new ZComparator());
        PARENT = application;
    }

    @Override
    public void processEntity(Entity e, float deltaTime) {
        int tempX = 0, tempY = 0, tempRotation = 0; //replace with whatever it actually is

        if (GUIMapper.has(e)) {
            GUIMapper.get(e).HANDLER.update(deltaTime);
            GUIMapper.get(e).CANVAS.draw();
        } else if (true/*PositionMapper.has(e)*/) {
            PARENT.getBatch().begin();
            for (int i = 0; i < spriteMapper.get(e).sprites.size; i++) {
                spriteMapper.get(e).sprites.get(i).setPosition(tempX, tempY);
                spriteMapper.get(e).sprites.get(i).setRotation(tempRotation);
                spriteMapper.get(e).sprites.get(i).draw(PARENT.getBatch());
            }
            PARENT.getBatch().end();
        }
    }

    public static class ZComparator implements Comparator<Entity> {
        @Override
        public int compare(Entity o1, Entity o2) {
            //this should be in a static mapper class that didn't exist at the time of writing
            ComponentMapper<SpriteComponent> spriteMapper = ComponentMapper.getFor(SpriteComponent.class);
            ComponentMapper<GUIComponent> GUIMapper = ComponentMapper.getFor(GUIComponent.class);
            //---

            int z1, z2;
            //GUI elements; if there, they should have a greater z index than sprites always
            if (GUIMapper.has(o1) && GUIMapper.has(o2))
                return 0;
            else if (GUIMapper.has(o1) && !GUIMapper.has(o2))
                return 1;
            else if (!GUIMapper.has(o1) && GUIMapper.has(o2))
                return -1;

            z1 = spriteMapper.get(o1).zIndex;
            z2 = spriteMapper.get(o2).zIndex;
            if (z1 < z2)
                return -1;
            else if (z1 > z2)
                return 1;
            else
                return 0;
        }
    }
}

