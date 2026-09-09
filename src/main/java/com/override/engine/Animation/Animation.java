package com.override.engine.Animation;

import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.LuaTable;

import com.override.engine.GameObject;

class AnimationObj {
    public boolean playing;
    public boolean connected;
    public GameObject connectedObject;

    public float currentTime = 0;
    public float finalTime = 0;

    public float stepX;
    public float stepY;
    public float startX;
    public float startY;

    public float x, y = 0.0f;

    public static float delayPerFrame = 0.0f;

    AnimationObj(LuaTable startPos, LuaTable steps, float delay) {
        this.playing = false;
        this.connected = false;
        this.connectedObject = new GameObject();

        startX = startPos.get(1).tofloat();
        startY = startPos.get(2).tofloat();

        stepX = steps.get(1).tofloat();
        stepY = steps.get(2).tofloat();

    }

    public void changeFrame() {

    }

    public void update(float dt) {
        if (finalTime < currentTime) {
            currentTime += dt;
        } else {
            currentTime = 0.0f;
        }
    }

    public void connect(GameObject obj) {
        connectedObject = obj;
        connected = true;
    }

    public void play() {
        if (connected) {
            playing = true;
        }
    }
}

public class Animation {
    private static final List<AnimationObj> anims = new ArrayList<>();

    public static AnimationObj create(LuaTable startPos, LuaTable steps, float delay) {
        AnimationObj anim = new AnimationObj(startPos, steps, delay);
        anims.add(anim);
        return anims.getLast();
    }
}
