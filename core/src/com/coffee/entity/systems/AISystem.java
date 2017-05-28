package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.components.AIComponent;
import com.coffee.entity.components.HealthComponent;
import com.coffee.entity.components.MovementComponent;
import com.coffee.entity.components.TransformComponent;
import com.coffee.util.Mapper;

/**
 * @author Phillip O'Reggio
 */
public class AISystem extends IteratingSystem {
    public final Viewport VIEWPORT;

    public AISystem(Viewport v) {
        super(Family.all(AIComponent.class).get());
        VIEWPORT = v;
    }


    public void processEntity(Entity entity, float deltaTime) {
        final AIComponent AI = Mapper.AI.get(entity);
        final TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);
        final MovementComponent MOVE = Mapper.MOVEMENT.get(entity);
        final HealthComponent HEALTH = Mapper.HEALTH.get(entity);

        if (AI.lerpTimer == 1) {
            System.out.println("burp");
            AI.BEGIN_POS.set(TRANSFORM.POSITION);
            AI.END_POS.set(
                    MathUtils.random(VIEWPORT.getWorldWidth() - TRANSFORM.SIZE.width),
                    MathUtils.random(TRANSFORM.SIZE.height, VIEWPORT.getWorldHeight() * 2.0f / 3.0f)
            );
            AI.lerpTimer = 0;
        }

        AI.lerpTimer = MathUtils.clamp(AI.lerpTimer + deltaTime, 0, 1);
        float perc = MathUtils.sin(AI.lerpTimer * MathUtils.PI / 2.0f);

        TRANSFORM.POSITION.set(
                MathUtils.lerp(AI.BEGIN_POS.x, AI.END_POS.x, perc),
                MathUtils.lerp(AI.BEGIN_POS.y, AI.END_POS.y, perc)
        );

        if (HEALTH.health <= 0) {
            getEngine().removeEntity(entity);
            System.out.println("dead boi");
        }

        /*//Has reached target node (with some buffer space)
        boolean reachedXLocation = (TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x) >= node.x - 10 && (TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x) <= node.x + 10;
        boolean reachedYLocation = (TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y) >= node.y - 10 && (TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y) <= node.y + 10;
        if (reachedXLocation && reachedYLocation) {
            //select new node
            int newNode = MathUtils.random(0, AI.path.size - 1);
            if (newNode == AI.currentNode)
                newNode = MathUtils.clamp(newNode + MathUtils.randomSign(), 0, AI.path.size - 1);
            AI.currentNode = newNode;
            performAction(entity);
            int newNode = MathUtils.random(0, AI.END_POS.size - 1);
            if (newNode == AI.currentNodes[0] || newNode == AI.currentNodes[1])
                newNode = MathUtils.clamp(newNode + MathUtils.randomSign(), 0, AI.END_POS.size - 1);
            //move [1] node to [0] and set new node as [1]
            AI.currentNodes[0] = AI.currentNodes[1];
            AI.currentNodes[1] = newNode;
            performAction();
        } else {
            float direction = (float) Math.atan2(node.y - (TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y), node.x - (TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x));
            MOVE.MOVEMENT_NORMAL.setAngle((float) Math.toDegrees(direction));
        }*/
    }

    /**
     * Makes the boss perform a random action based on variables such as the amount of health or time left.
     */
    private void performAction(Entity entity) {
        TransformComponent transform = Mapper.TRANSFORM.get(entity);
        int choice = 1;

        switch (choice) {
            case 1 :
                for (int i = 0; i < 9; i++)
                    getEngine().addEntity(EntityFactory.createEnemyBullet(
                            transform.POSITION.x + transform.ORIGIN.x + 8,
                            transform.POSITION.y + transform.SIZE.height + 10,
                            180 + i * 20,
                            entity
                    ));
                break;
        }
    }


}
