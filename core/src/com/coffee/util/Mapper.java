package com.coffee.util;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.coffee.entity.components.*;

/**
 * Class containing a {@link com.badlogic.ashley.core.ComponentMapper} for every {@link Component}
 * that has been made. This makes accessing components per entity much faster and resource-friendly
 *
 * @author Jared Tulayan
 */
public class Mapper {
    public static final ComponentMapper<TransformComponent> TRANSFORM = ComponentMapper.getFor(TransformComponent.class);
    public static final ComponentMapper<MovementComponent> MOVEMENT = ComponentMapper.getFor(MovementComponent.class);
    public static final ComponentMapper<SpriteComponent> SPRITE = ComponentMapper.getFor(SpriteComponent.class);
    public static final ComponentMapper<GUIComponent> GUI = ComponentMapper.getFor(GUIComponent.class);
}
