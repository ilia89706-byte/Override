package com.override.engine.GameObjects;

import static com.raylib.Raylib.DrawCircle;

import com.override.engine.GameObject;
import com.override.engine.Global;
import com.raylib.Raylib;

public class Circle extends GameObject {
    public float radius = 0.0f;

    public Circle(float x, float y, float radius) {
        super();
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    @Override
    public void draw() {
        pos
                .x(Global.windowWidth / 2f + this.getWorldX())
                .y(Global.windowHeight / 2f - this.getWorldY())
                .width(this.width * this.scale)
                .height(this.height * this.scale);

        origin.x(pos.width() * anchorX).y(pos.height() * anchorY);
        DrawCircle((int) pos.x(), (int) pos.y(), this.radius,
                new Raylib.Color().r(color.get("r").tobyte()).g(color.get("g").tobyte()).b(color.get("b").tobyte())
                        .a(color.get("a").tobyte()));
    }

}