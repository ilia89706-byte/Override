package com.override.engine;

import static com.raylib.Raylib.DrawTextPro;
import static com.raylib.Raylib.DrawTexturePro;
import com.override.engine.GameObjects.*;

import java.util.ArrayList;
import java.util.List;
import org.luaj.vm2.LuaValue;
import com.raylib.*;
import com.raylib.Raylib.Rectangle;

public class Display {
    public static java.util.List<GameObject> displayObjects = new java.util.ArrayList<>();

    public static void removeAll() {
        for (int i = displayObjects.size() - 1; i >= 0; i--) {
            displayObjects.get(i).remove();
        }
    }

    public static void setBackgroundColor(byte r, byte g, byte b) {
        Global.backgroundColor.color.r(r).g(g).b(b);
    }

    public static void setBackgroundColor(String hex) {
        if (hex.startsWith("#"))
            hex = hex.substring(1);
        if (hex.length() == 6)
            hex += "FF";
        try {
            long colorLong = Long.parseLong(hex, 16);
            byte r = (byte) ((colorLong >> 24) & 0xFF);
            byte g = (byte) ((colorLong >> 16) & 0xFF);
            byte b = (byte) ((colorLong >> 8) & 0xFF);
            int a = (int) (colorLong & 0xFF);
            Global.backgroundColor.color.r(r).g(g).b(b);
        } catch (NumberFormatException e) {
            System.err.println(hex);
        }
    }

    private static LuaValue processObject(GameObject obj) {
        displayObjects.add(obj);

        obj.aabb.updateBounds(obj.x, obj.y, obj.width, obj.height);

        return LuaBridge.wrap(obj, (javaObj, fieldName, newValue) -> {
            GameObject go = (GameObject) javaObj;
            float val = newValue.isnumber() ? newValue.tofloat() : 0.0f;

            switch (fieldName) {
                case "x":
                    go.setPosition(val, go.y);
                    break;
                case "y":
                    go.setPosition(go.x, val);
                    break;
                case "angle":
                    float offset = val - go.angle;
                    go.rotate(offset);
                    break;
                case "scale":
                    go.setScale(val);
                    break;
                case "width":
                    go.width = val;
                    break;
                case "height":
                    go.height = val;
                    break;
                case "radius":
                    go.width = go.radius / 2;
                    go.height = go.radius / 2;
            }

            if (fieldName.equals("x") || fieldName.equals("y") ||
                    fieldName.equals("scale") || fieldName.equals("anchorX") ||
                    fieldName.equals("anchorY") || fieldName.equals("width") ||
                    fieldName.equals("height")) {

                obj.aabb.setScale(go.scale);
                obj.aabb.setAnchorX(go.anchorX);
                obj.aabb.setAnchorY(go.anchorY);
                obj.aabb.updateBounds(go.x, go.y, go.width, go.height);
            }
        });
    }

    public static LuaValue rect(float x, float y, float width, float height) {
        return processObject(new Rect(x, y, width, height));
    }

    public static LuaValue circle(float x, float y, float radius) {
        return processObject(new Circle(x, y, radius));
    }

    public static LuaValue image(String path, float x, float y, float width, float height) {
        return processObject(new Image(path, x, y, width, height));
    }

    public static LuaValue text(String text, float x, float y, String fontPath, int fontSize) {
        return processObject(new Text(text, x, y, fontPath, fontSize));
    }

    public static LuaValue tile(String path, float x, float y, float width, float height,
            float srcX, float srcY, float srcW, float srcH) {
        return processObject(new Tile(path, x, y, width, height, srcX, srcY, srcW, srcH));
    }

    public static LuaValue text(String text, float x, float y, String fontPath) {
        return text(text, x, y, fontPath, 30);
    }
}
