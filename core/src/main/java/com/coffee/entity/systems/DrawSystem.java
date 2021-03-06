package com.coffee.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.components.SpriteComponent;
import com.coffee.entity.components.TransformComponent;
import com.coffee.util.Mapper;

import java.util.Comparator;

/**
 * Sorts all the entities with sprite components by z-order, then draws all of them.
 * Next, GUI elements are drawn so that they are always on top of sprites.
 *
 * @author Phillip O'Reggio
 */
public class DrawSystem extends SortedIteratingSystem {
    private final SpriteBatch BATCH;
    private final Viewport VIEWPORT;

    //this should be in a static mapper class that didn't exist at the time of writing
    ComponentMapper<SpriteComponent> spriteMapper = Mapper.SPRITE;

    public DrawSystem(SpriteBatch batch, Viewport viewport) {
        super(Family.one(SpriteComponent.class).get(), new ZComparator());
        BATCH = batch;
        VIEWPORT = viewport;
    }

    public void update(float deltaTime) {
        VIEWPORT.getCamera().update();
        BATCH.setProjectionMatrix(VIEWPORT.getCamera().combined);

        BATCH.begin();
        super.update(deltaTime);
        BATCH.end();
    }

    @Override
    public void processEntity(Entity e, float deltaTime) {
        final SpriteComponent SPRITE = Mapper.SPRITE.get(e);
        final TransformComponent TRANSFORM = Mapper.TRANSFORM.get(e);

        Array<Sprite> sprites = SPRITE.SPRITES;
        Vector2 pos = TRANSFORM.POSITION.cpy().add(TRANSFORM.ORIGIN);

        for (int i = 0; i < sprites.size; i++) {
            Sprite s = sprites.get(i);

            s.setPosition(pos.x - s.getOriginX(), pos.y - s.getOriginY());
            s.setRotation(TRANSFORM.rotation);
            s.draw(BATCH);
        }
    }

    public static class ZComparator implements Comparator<Entity> {
        @Override
        public int compare(Entity o1, Entity o2) {
            int z1 = Mapper.SPRITE.get(o1).zIndex,
            z2 = Mapper.SPRITE.get(o2).zIndex;

            if (z1 < z2)
                return -1;
            else if (z1 > z2)
                return 1;
            else
                return 0;
        }
    }
}

