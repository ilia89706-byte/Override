package com.override.engine;

import java.util.HashMap;
import java.util.Map;
import com.override.engine.Animation.*;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.override.engine.Tween.TweenManager;

public class API {

    public static void genAPI() {
        Map<String, Class<?>> apiMap = new HashMap<>();

        apiMap.put("display", com.override.engine.Display.class);
        apiMap.put("window", com.override.engine.Window.class);
        apiMap.put("override", com.override.Override.class);
        apiMap.put("events", com.override.engine.Events.class);
        apiMap.put("mouse", com.override.engine.Mouse.class);
        apiMap.put("sound", Sound.class);
        Global.globals.set("print", new Output());

        Global.globals.set("tween", new org.luaj.vm2.lib.VarArgFunction() {
            @Override
            public org.luaj.vm2.Varargs invoke(org.luaj.vm2.Varargs args) {
                LuaValue target = args.arg(1);
                String key = args.arg(2).tojstring();
                float duration = args.arg(3).tofloat();
                float endValue = args.arg(4).tofloat();
                String easeType = args.arg(5).tojstring();
                LuaValue onComplete = args.arg(6);

                TweenManager.to(target, key, duration, endValue, easeType, onComplete);
                return LuaValue.NIL;
            }
        });

        for (Map.Entry<String, Class<?>> entry : apiMap.entrySet()) {
            String luaName = entry.getKey();
            Class<?> javaClass = entry.getValue();

            Global.globals.set(luaName, CoerceJavaToLua.coerce(javaClass));
        }
    }
}
