package com.override.engine;

import static com.raylib.Raylib.GetMouseX;
import static com.raylib.Raylib.GetMouseY;
import static com.raylib.Raylib.SetMouseScale;
import static com.raylib.Raylib.IsMouseButtonPressed;
import static com.raylib.Raylib.IsMouseButtonDown;
import static com.raylib.Raylib.IsMouseButtonReleased;
import static com.raylib.Raylib.IsMouseButtonUp;
import static com.raylib.Raylib.GetMouseWheelMove;
import static com.raylib.Raylib.SetMousePosition;
import static com.raylib.Raylib.HideCursor;
import static com.raylib.Raylib.ShowCursor;
import static com.raylib.Raylib.IsCursorHidden;
import static com.raylib.Raylib.EnableCursor;
import static com.raylib.Raylib.DisableCursor;

public class Mouse {

    public static int getX() {
        return GetMouseX() - Global.windowWidth / 2;
    }

    public static int getY() {
        return -GetMouseY() + Global.windowHeight / 2;
    }

    public static void setScale(float x, float y) {
        SetMouseScale(x, y);
    }

    public static void setPosition(int gameX, int gameY) {
        int screenX = gameX + Global.windowWidth / 2;
        int screenY = Global.windowHeight / 2 - gameY;
        SetMousePosition(screenX, screenY);
    }

    public static void hide() {
        HideCursor();
    }

    public static void show() {
        ShowCursor();
    }

    public static boolean isHidden() {
        return IsCursorHidden();
    }

    public static void enable() {
        EnableCursor();
    }

    public static void disable() {
        DisableCursor();
    }
}
