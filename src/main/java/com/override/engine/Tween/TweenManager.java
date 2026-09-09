package com.override.engine.Tween;

import org.luaj.vm2.LuaValue;
import java.util.ArrayList;
import java.util.List;

public class TweenManager {

    private static class Tween {
        LuaValue targetObject;
        String key;
        float startValue;
        float endValue;
        float duration;
        float elapsedTime = 0f;
        EaseType easeType;
        LuaValue onComplete;

        boolean isFinished = false;
    }

    private static final List<Tween> activeTweens = new ArrayList<>();
    private static final List<Tween> toAdd = new ArrayList<>();

    public static void to(LuaValue target, String key, float duration, float endValue, String easeTypeStr,
            LuaValue onComplete) {
        Tween tween = new Tween();
        tween.targetObject = target;
        tween.key = key;

        tween.startValue = target.get(key).tofloat();
        tween.endValue = endValue;
        tween.duration = duration;
        tween.onComplete = onComplete;

        try {
            tween.easeType = EaseType.valueOf(easeTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            tween.easeType = EaseType.LINEAR;
        }

        synchronized (toAdd) {
            toAdd.add(tween);
        }
    }

    public static void update(float deltaTime) {
        synchronized (toAdd) {
            if (!toAdd.isEmpty()) {
                activeTweens.addAll(toAdd);
                toAdd.clear();
            }
        }

        for (int i = activeTweens.size() - 1; i >= 0; i--) {
            Tween tween = activeTweens.get(i);
            tween.elapsedTime += deltaTime;

            float alpha = Math.min(1.0f, tween.elapsedTime / tween.duration);
            float easedAlpha = tween.easeType.apply(alpha);

            float currentValue = tween.startValue + (tween.endValue - tween.startValue) * easedAlpha;

            tween.targetObject.set(tween.key, LuaValue.valueOf(currentValue));

            if (alpha >= 1.0f) {
                tween.isFinished = true;
                activeTweens.remove(i);

                if (tween.onComplete != null && tween.onComplete.isfunction()) {
                    tween.onComplete.call(tween.targetObject);
                }
            }
        }
    }

    public static void clear() {
        activeTweens.clear();
        synchronized (toAdd) {
            toAdd.clear();
        }
    }
}
