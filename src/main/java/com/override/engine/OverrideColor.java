package com.override.engine;

import com.raylib.Raylib.Color;

public class OverrideColor {
    public Color color;

    public OverrideColor(
            int r,
            int g,
            int b,
            int a) {
        Color color = new Color()
                .r((byte) r)
                .g((byte) g)
                .b((byte) b)
                .a((byte) a);
        this.color = color;
    }

}
