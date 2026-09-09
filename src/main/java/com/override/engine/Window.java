package com.override.engine;

import static com.raylib.Raylib.CloseWindow;
import static com.raylib.Raylib.SetWindowPosition;
import static com.raylib.Raylib.SetWindowSize;

import com.raylib.*;

public class Window {
    public static void setWidth(int width) {
        SetWindowSize(width, Global.windowHeight);
        Global.windowWidth = width;
    }

    public static void setHeight(int height) {
        SetWindowSize(Global.windowWidth, height);
        Global.windowHeight = height;
    }

    public static void setSize(int width, int height) {
        SetWindowSize(width, height);
        Global.windowHeight = height;
        Global.windowWidth = width;
    }

    public static int getWidth() {
        return Global.windowWidth;
    }

    public static int getHeight() {
        return Global.windowHeight;
    }

    public static void close() {
        CloseWindow();
    }

    public static void setPosition(int x, int y) {
        SetWindowPosition(x, y);
    }
}
