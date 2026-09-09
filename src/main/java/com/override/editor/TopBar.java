package com.override.editor;

import com.override.engine.GameObjects.Rect;
import com.override.engine.GameObjects.Image;
import com.override.engine.Display;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;
import java.util.ArrayList;

public class TopBar {

    private static class JavaActionWrapper extends ZeroArgFunction {
        private final Runnable javaAction;

        public JavaActionWrapper(Runnable javaAction) {
            this.javaAction = javaAction;
        }

        @Override
        public LuaValue call() {
            if (javaAction != null) {
                javaAction.run();
            }
            return LuaValue.NIL;
        }
    }

    public static Rect createButton(String buttonIconPath, float x, float y, float width, float height,
            Runnable action) {
        Rect buttonPad = new Rect(x, y, width, height);
        buttonPad.setColor("#383838");
        Display.displayObjects.add(buttonPad);

        Image icon = new Image(buttonIconPath, 0, 0, width * 0.7f, height * 0.7f);
        buttonPad.insert(icon);
        Display.displayObjects.add(icon);

        LuaValue luaCallback = new JavaActionWrapper(action);
        buttonPad.on("touch", luaCallback);

        return buttonPad;
    }

    public static void create(float windowWidth, float windowHeight) {
        float barHeight = windowHeight / 10;

        Rect topRect = new Rect(0, windowHeight / 2, windowWidth, barHeight);
        topRect.anchorY = 0;
        topRect.setColor("#4a4a4a");
        Display.displayObjects.add(topRect);

        float cx = -windowWidth / 2;
        float cy = (windowHeight / 2) - (barHeight / 2);

        createButton("overriders/new.png", cx + (barHeight / 2), cy, barHeight, barHeight, () -> {
        });
        cx += barHeight;

        createButton("overriders/open.png", cx + (barHeight / 2), cy, barHeight, barHeight, () -> {
        });
        cx += barHeight;

        createButton("overriders/save.png", cx + (barHeight / 2), cy, barHeight, barHeight, () -> {
        });
        cx += barHeight;

        createButton("overriders/grid.png", cx + (barHeight / 2), cy, barHeight, barHeight, () -> {
        });
        cx += barHeight;
        createButton("overriders/delete.png", cx + (barHeight / 2), cy, barHeight, barHeight, () -> {
        });
        cx += barHeight;
    }
}
