package com.override;

import static com.raylib.Raylib.*;
import com.override.engine.Physics;
import com.override.engine.Render;
import com.raylib.Colors;
import java.io.File;
import com.override.engine.Tween.*;

import org.luaj.vm2.*;

import com.override.editor.EditorMain;
import com.override.engine.API;
import com.override.engine.Display;
import com.override.engine.Events;
import com.override.engine.Global;

public class Override {

    public static void loadScene(String path) {
        for (int i = Display.displayObjects.size() - 1; i >= 0; i--) {
            Display.displayObjects.get(i).remove();
        }
        Display.displayObjects.clear();

        com.override.engine.Sound.closeAll();
        Global.callLuaFile(Global.projectPath + "\\" + path);
    }

    public static void loadConf() {
        LuaValue val = Global.callLuaFile(Global.projectPath + File.separator + "conf.lua");
        Global.generateConf(val);
    }

    public static void start(boolean hasEditor) {
        if (!hasEditor) {
            String luaPathPattern = Global.projectPath + File.separator + "?.lua;"
                    + Global.projectPath + File.separator + "?" + File.separator + "init.lua";
            Global.globals.get("package").set("path", LuaValue.valueOf(luaPathPattern));

            loadConf();
            SetTraceLogLevel(LOG_NONE);
            Display.displayObjects.clear();
            if (!Global.hasCreated) {
                InitWindow(Global.windowWidth, Global.windowHeight, Global.windowTitle);
                InitAudioDevice();
            }
            if (Global.fullscreen) {
                ToggleFullscreen();
            }
            API.genAPI();
            Global.callLuaFile(Global.projectPath + File.separator + "main.lua");

            SetTargetFPS(Global.maxFPS);
            if (Global.windowIcon != null) {
                Global.loadIcon(Global.windowIcon);
            }
            if (Global.globals.get("init").isfunction()) {
                Global.globals.get("init").call();
            }

        } else {
            InitWindow(640, 480, "Override editor");
            Global.windowWidth = 640;
            Global.windowHeight = 640;
            SetTargetFPS(60);
            EditorMain.create();
        }

        while (!WindowShouldClose()) {
            Physics.updatePhys(GetFrameTime());
            Events.retakeEvents(GetFrameTime());
            com.override.engine.Sound.update();
            TweenManager.update(GetFrameTime());
            BeginDrawing();

            ClearBackground(Global.backgroundColor.color);

            Render.draw();

            if (Global.globals.get("update").isfunction()) {
                Global.globals.get("update").call();
            }

            DrawText("Objects:" + Display.displayObjects.size() + "/FPS:" + GetFPS(), 0,
                    0, 20, Colors.GOLD);

            EndDrawing();
        }
        CloseWindow();

        System.out.println(Global.windowTitle);
    }
}
