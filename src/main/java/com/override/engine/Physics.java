package com.override.engine;

import org.dyn4j.*;
import org.dyn4j.dynamics.Body;
import org.dyn4j.world.World;

public class Physics {
    public static final World<Body> world = new World<>();
    public static int SCALE = 32;

    static {
        world.setGravity(new org.dyn4j.geometry.Vector2(0, -9.81));
    }

    public static void updatePhys(float dt) {
        world.update(dt);
        for (GameObject obj : Display.displayObjects) {
            if (obj.body != null) {
                obj.x = (int) (obj.body.getTransform().getTranslationX() * SCALE);
                obj.y = (int) (obj.body.getTransform().getTranslationY() * SCALE);
                obj.angle = (int) Math.toDegrees(obj.body.getTransform().getRotationAngle());
            }
        }
    }

}
