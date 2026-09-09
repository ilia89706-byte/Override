package com.override.engine.GameObjects;

import static com.raylib.Raylib.DrawTexturePro;

import com.override.engine.Global;
import com.raylib.Raylib;

public class Tile extends Image {
    public Tile(String path, float x, float y, float width, float height,
            float srcX, float srcY, float srcW, float srcH) {
        super(path, x, y, width, height);

        this.source.x(srcX).y(srcY).width(srcW).height(srcH);
    }

    @Override
    public void draw() {
        pos
                .x(Global.windowWidth / 2f + this.getWorldX())
                .y(Global.windowHeight / 2f - this.getWorldY())
                .width(this.width * this.scale)
                .height(this.height * this.scale);

        origin.x(pos.width() * anchorX).y(pos.height() * anchorY);

        float finalSrcW = mirroredX ? -Math.abs(source.width()) : Math.abs(source.width());
        float finalSrcH = mirroredY ? -Math.abs(source.height()) : Math.abs(source.height());

        float currentX = source.x();
        float currentY = source.y();

        Raylib.Rectangle renderSource = new Raylib.Rectangle()
                .x(currentX).y(currentY)
                .width(finalSrcW).height(finalSrcH);

        DrawTexturePro(texture, renderSource, pos, origin, -this.getWorldAngle(),
                new Raylib.Color().r(color.get("r").tobyte()).g(color.get("g").tobyte()).b(color.get("b").tobyte())
                        .a(color.get("a").tobyte()));
    }
}
