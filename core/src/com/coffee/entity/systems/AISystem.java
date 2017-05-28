package com.coffee.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.coffee.entity.EntityFactory;
import com.coffee.entity.components.AIComponent;
import com.coffee.entity.components.MovementComponent;
import com.coffee.entity.components.TransformComponent;
import com.coffee.util.Mapper;

/**
 * @author Phillip O'Reggio
 */
public class AISystem extends IteratingSystem {

    public AISystem() {
        super(Family.all(AIComponent.class).get());
    }


    public void processEntity(Entity entity, float deltaTime) {
        AIComponent AI = Mapper.AI.get(entity);
        TransformComponent TRANSFORM = Mapper.TRANSFORM.get(entity);
        MovementComponent MOVE = Mapper.MOVEMENT.get(entity);

        Vector2 node = AI.path.get(AI.currentNode);

        //Has reached target node (with some buffer space)
        boolean reachedXLocation = (TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x) >= node.x - 10 && (TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x) <= node.x + 10;
        boolean reachedYLocation = (TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y) >= node.y - 10 && (TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y) <= node.y + 10;
        if (reachedXLocation && reachedYLocation) {
            //select new node
            int newNode = MathUtils.random(0, AI.path.size - 1);
            if (newNode == AI.currentNode)
                newNode = MathUtils.clamp(newNode + MathUtils.randomSign(), 0, AI.path.size - 1);
            AI.currentNode = newNode;
            performAction(entity);
        } else {
            float direction = (float) Math.atan2(node.y - (TRANSFORM.POSITION.y + TRANSFORM.ORIGIN.y), node.x - (TRANSFORM.POSITION.x + TRANSFORM.ORIGIN.x));
            MOVE.MOVEMENT_NORMAL.setAngle((float) Math.toDegrees(direction));
        }
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
