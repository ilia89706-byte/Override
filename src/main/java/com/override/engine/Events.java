package com.override.engine;

import com.raylib.Raylib;
import static com.raylib.Raylib.*;
import org.luaj.vm2.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Events {

    private static final Map<String, List<GameObject>> registry = new HashMap<>();
    private static final Map<Integer, String> keyMap = new HashMap<>();

    private static final Map<Integer, GameObject> activeCapturedObjects = new HashMap<>();

    private static GameObject focusedObject = null;

    public static void setFocus(GameObject obj) {
        if (focusedObject != obj) {
            if (focusedObject != null) {
                focusedObject.emit("blur", LuaValue.NIL);
            }
            focusedObject = obj;
            if (focusedObject != null) {
                focusedObject.emit("focus", LuaValue.NIL);
            }
        }
    }

    public static GameObject getFocusedObject() {
        return focusedObject;
    }

    public static void registerObjectToEvent(String eventType, GameObject obj) {
        List<GameObject> list = registry.computeIfAbsent(eventType, k -> new ArrayList<>());
        if (!list.contains(obj)) {
            list.add(obj);
        }
    }

    public static void unregisterObjectFromAll(GameObject obj) {
        for (List<GameObject> list : registry.values()) {
            list.remove(obj);
        }
        activeCapturedObjects.values().removeIf(v -> v == obj);
        if (focusedObject == obj) {
            focusedObject = null;
        }
    }

    public static boolean hasTouch(GameObject obj, float screenTouchX, float screenTouchY) {
        float worldTouchX = screenTouchX - (Global.windowWidth / 2f);
        float worldTouchY = -(screenTouchY - (Global.windowHeight / 2f));

        float worldX = obj.getWorldX();
        float worldY = obj.getWorldY();
        float worldAngleDeg = obj.getWorldAngle();

        float dx = worldTouchX - worldX;
        float dy = worldTouchY - worldY;

        double angleRad = Math.toRadians(-worldAngleDeg);
        float cosA = (float) Math.cos(angleRad);
        float sinA = (float) Math.sin(angleRad);

        float localX = dx * cosA - dy * sinA;
        float localY = dx * sinA + dy * cosA;

        if (obj.mirroredX)
            localX = -localX;
        if (obj.mirroredY)
            localY = -localY;

        float scaledWidth = obj.width * obj.scale;
        float scaledHeight = obj.height * obj.scale;

        float leftBound = -obj.anchorX * scaledWidth;
        float rightBound = leftBound + scaledWidth;

        float topBound = -obj.anchorY * scaledHeight;
        float bottomBound = topBound + scaledHeight;

        return localX >= leftBound && localX <= rightBound &&
                localY >= topBound && localY <= bottomBound;
    }

    public static void processTouchSystem() {
        List<GameObject> touchObjects = registry.get("touch");
        if (touchObjects == null || touchObjects.isEmpty())
            return;

        List<GameObject> candidates = new ArrayList<>(touchObjects);
        candidates.sort((o1, o2) -> Integer.compare(o2.layer, o1.layer));

        float mx = GetMouseX();
        float my = GetMouseY();
        Raylib.Vector2 mouseDelta = GetMouseDelta();
        boolean mouseMoved = (mouseDelta.x() != 0 || mouseDelta.y() != 0);

        int[] buttons = { MOUSE_BUTTON_LEFT, MOUSE_BUTTON_RIGHT, MOUSE_BUTTON_MIDDLE };
        String[] buttonNames = { "left", "right", "middle" };

        for (int i = 0; i < buttons.length; i++) {
            int btn = buttons[i];
            String bName = buttonNames[i];

            GameObject topObjUnderMouse = null;
            for (GameObject obj : candidates) {
                if (hasTouch(obj, mx, my)) {
                    topObjUnderMouse = obj;
                    break;
                }
            }

            if (!IsMouseButtonDown(btn)) {
                if (topObjUnderMouse != null && mouseMoved) {
                    sendTouchEvent(topObjUnderMouse, bName, "hover", mx, my, mouseDelta);
                }
            }

            if (IsMouseButtonPressed(btn)) {
                if (topObjUnderMouse != null) {
                    activeCapturedObjects.put(btn, topObjUnderMouse);
                    setFocus(topObjUnderMouse);
                    sendTouchEvent(topObjUnderMouse, bName, "began", mx, my, mouseDelta);
                } else {
                    setFocus(null);
                }
            } else if (IsMouseButtonDown(btn)) {
                GameObject captured = activeCapturedObjects.get(btn);
                if (captured != null && mouseMoved) {
                    sendTouchEvent(captured, bName, "moved", mx, my, mouseDelta);
                }
            } else if (IsMouseButtonReleased(btn)) {
                GameObject captured = activeCapturedObjects.get(btn);
                if (captured != null) {
                    if (topObjUnderMouse == captured) {
                        sendTouchEvent(captured, bName, "ended", mx, my, mouseDelta);
                    } else {
                        sendTouchEvent(captured, bName, "cancelled", mx, my, mouseDelta);
                    }
                    activeCapturedObjects.remove(btn);
                }
            }
        }
    }

    private static void sendTouchEvent(GameObject obj, String buttonName, String phase, float mx, float my,
            Raylib.Vector2 delta) {
        LuaTable info = new LuaTable();
        info.set("button", buttonName);
        info.set("phase", phase);
        info.set("x", mx);
        info.set("y", my);
        info.set("dx", delta.x());
        info.set("dy", delta.y());

        obj.emit("touch", info);
    }

    static {
        keyMap.put(KEY_SPACE, "space");
        keyMap.put(KEY_ENTER, "enter");
        keyMap.put(KEY_ESCAPE, "escape");
        keyMap.put(KEY_BACKSPACE, "backspace");
        keyMap.put(KEY_A, "a");
        keyMap.put(KEY_B, "b");
        keyMap.put(KEY_C, "c");
        keyMap.put(KEY_D, "d");
        keyMap.put(KEY_E, "e");
        keyMap.put(KEY_F, "f");
        keyMap.put(KEY_G, "g");
        keyMap.put(KEY_H, "h");
        keyMap.put(KEY_I, "i");
        keyMap.put(KEY_J, "j");
        keyMap.put(KEY_K, "k");
        keyMap.put(KEY_L, "l");
        keyMap.put(KEY_M, "m");
        keyMap.put(KEY_N, "n");
        keyMap.put(KEY_O, "o");
        keyMap.put(KEY_P, "p");
        keyMap.put(KEY_Q, "q");
        keyMap.put(KEY_R, "r");
        keyMap.put(KEY_S, "s");
        keyMap.put(KEY_T, "t");
        keyMap.put(KEY_U, "u");
        keyMap.put(KEY_V, "v");
        keyMap.put(KEY_W, "w");
        keyMap.put(KEY_X, "x");
        keyMap.put(KEY_Y, "y");
        keyMap.put(KEY_Z, "z");
        keyMap.put(KEY_ZERO, "0");
        keyMap.put(KEY_ONE, "1");
        keyMap.put(KEY_TWO, "2");
        keyMap.put(KEY_THREE, "3");
        keyMap.put(KEY_FOUR, "4");
        keyMap.put(KEY_FIVE, "5");
        keyMap.put(KEY_SIX, "6");
        keyMap.put(KEY_SEVEN, "7");
        keyMap.put(KEY_EIGHT, "8");
        keyMap.put(KEY_NINE, "9");
        keyMap.put(KEY_LEFT, "left");
        keyMap.put(KEY_RIGHT, "right");
        keyMap.put(KEY_UP, "up");
        keyMap.put(KEY_DOWN, "down");
    }

    public static void retakeEvents(float dt) {
        call("update", LuaValue.valueOf(dt));

        processTouchSystem();

        Raylib.Vector2 mouseDelta = GetMouseDelta();
        if (mouseDelta.x() != 0 || mouseDelta.y() != 0) {
            LuaTable mouseData = new LuaTable();
            mouseData.set("x", GetMouseX());
            mouseData.set("y", GetMouseY());
            mouseData.set("dx", mouseDelta.x());
            mouseData.set("dy", mouseDelta.y());
            call("mouseMoved", mouseData);
        }

        float scrollMove = GetMouseWheelMove();
        if (scrollMove != 0) {
            call("onMouseWheel", LuaValue.valueOf(scrollMove));
        }

        int[] buttons = { MOUSE_BUTTON_LEFT, MOUSE_BUTTON_RIGHT, MOUSE_BUTTON_MIDDLE };
        String[] buttonNames = { "left", "right", "middle" };

        for (int i = 0; i < buttons.length; i++) {
            int btn = buttons[i];
            String name = buttonNames[i];

            if (IsMouseButtonPressed(btn)) {
                LuaTable data = new LuaTable();
                data.set("button", name);
                data.set("phase", "pressed");
                data.set("x", GetMouseX());
                data.set("y", GetMouseY());
                call("mouseClick", data);
            }
            if (IsMouseButtonDown(btn)) {
                LuaTable data = new LuaTable();
                data.set("button", name);
                data.set("phase", "down");
                call("mouseClick", data);
            }
            if (IsMouseButtonReleased(btn)) {
                LuaTable data = new LuaTable();
                data.set("button", name);
                data.set("phase", "released");
                call("mouseClick", data);
            }
        }

        int charCode = GetCharPressed();
        while (charCode > 0) {
            call("charPressed", LuaValue.valueOf(charCode));
            charCode = GetCharPressed();
        }

        for (Map.Entry<Integer, String> entry : keyMap.entrySet()) {
            int prKey = entry.getKey();
            String luaName = entry.getValue();

            if (IsKeyPressed(prKey)) {
                LuaTable data = new LuaTable();
                data.set("phase", "pressed");
                data.set("key", luaName);
                call("keyboard", data);
            }
            if (IsKeyDown(prKey)) {
                LuaTable data = new LuaTable();
                data.set("phase", "down");
                data.set("key", luaName);
                call("keyboard", data);
            }
            if (IsKeyReleased(prKey)) {
                LuaTable data = new LuaTable();
                data.set("phase", "released");
                data.set("key", luaName);
                call("keyboard", data);
            }
        }

        if (IsWindowResized()) {
            LuaTable data = new LuaTable();
            data.set("width", GetRenderWidth());
            data.set("height", GetRenderHeight());
            call("windowResized", data);
        }
    }

    public static void call(String eventName, LuaValue data) {
        if ((eventName.equals("keyboard") || eventName.equals("charPressed")) && focusedObject != null) {
            focusedObject.emit(eventName, data);
        } else {
            List<GameObject> objects = registry.get(eventName);
            if (objects != null && !objects.isEmpty()) {
                List<GameObject> iterList = new ArrayList<>(objects);
                for (GameObject obj : iterList) {
                    obj.emit(eventName, data);
                }
            }
        }

        String globalFuncName = eventName;

        if (eventName.equals("mouseClick")) {
            globalFuncName = "onMouseEvent";
        } else if (eventName.equals("keyboard")) {
            globalFuncName = "onKeyEvent";
        } else if (eventName.equals("charPressed")) {
            LuaValue onCharPressedFunc = Global.globals.get("onCharPressed");
            if (onCharPressedFunc.isfunction()) {
                try {
                    String charStr = new String(Character.toChars(data.toint()));
                    onCharPressedFunc.call(LuaValue.valueOf(charStr));
                } catch (Exception ignored) {
                }
            }
            return;
        } else if (eventName.equals("windowResized")) {
            Global.windowWidth = data.get("width").toint();
            Global.windowHeight = data.get("height").toint();
        }

        LuaValue globalFunc = Global.globals.get(globalFuncName);
        if (globalFunc.isfunction()) {
            try {
                globalFunc.call(data);
            } catch (Exception ignored) {
            }
        }
    }
}
