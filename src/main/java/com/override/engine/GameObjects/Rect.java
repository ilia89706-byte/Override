package com.override.engine.GameObjects;

import static com.raylib.Raylib.DrawRectanglePro;

import com.override.engine.GameObject;
import com.override.engine.Global;
import com.raylib.Raylib;

public class Rect extends GameObject {

    public Rect(float x, float y, float width, float height) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw() {
        pos
                .x(Global.windowWidth / 2f + this.getWorldX())
                .y(Global.windowHeight / 2f - this.getWorldY())
                .width(this.width * this.scale)
                .height(this.height * this.scale);

        origin.x(pos.width() * anchorX).y(pos.height() * anchorY);
        DrawRectanglePro(pos, origin, -this.getWorldAngle(),
                new Raylib.Color().r(color.get("r").tobyte()).g(color.get("g").tobyte()).b(color.get("b").tobyte())
                        .a(color.get("a").tobyte()));
    }

}