package com.override.engine;

import static com.raylib.Raylib.LoadImage;
import static com.raylib.Raylib.SetWindowIcon;
import java.io.InputStream;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.JsePlatform;

public class Global {
    public static Globals globals = JsePlatform.standardGlobals();

    public static String projectPath = "F:\\PROJECTS\\overrideProject";

    public static OverrideColor backgroundColor = new OverrideColor(0, 0, 0, 255);

    public static int windowWidth, windowHeight, maxFPS;
    public static String windowIcon, windowTitle;

    public static boolean fullscreen = false;
    public static boolean hasCreated;

    public static void loadIcon(String path) {
        SetWindowIcon(LoadImage(projectPath + "\\" + path));
    }

    public static void generateConf(LuaValue val) {
        windowWidth = val.get("windowWidth").toint();
        windowHeight = val.get("windowHeight").toint();
        fullscreen = val.get("fullscreen").toboolean();
        hasCreated = false;

        maxFPS = val.get("maxFPS").toint();
        windowTitle = val.get("windowTitle").tojstring();
        if (val.get("windowIcon") != null) {
            windowIcon = val.get("windowIcon").tojstring();
        }
    }

    public static LuaValue callLua(String code) {
        LuaValue val = globals.load(code).call();
        return val;
    }

    public static LuaValue callLuaFile(String filePath) {
        try {
            return globals.loadfile(filePath).call();
        } catch (Exception e) {
            String resourcePath = filePath.replace(projectPath, "").replace("\\", "/");
            if (!resourcePath.startsWith("/")) {
                resourcePath = "/" + resourcePath;
            }
            InputStream in = Global.class.getResourceAsStream(resourcePath);
            if (in == null) {
                in = Global.class.getClassLoader().getResourceAsStream(filePath.replace("\\", "/"));
            }
            if (in != null) {
                return globals.load(in, "@" + filePath, "bt", globals).call();
            }
            throw e;
        }
    }
}
