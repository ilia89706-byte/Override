package com.override.engine.GameObjects;

import static com.raylib.Raylib.DrawRectanglePro;
import static com.raylib.Raylib.DrawTexturePro;
import static com.raylib.Raylib.SetTextureFilter;
import static com.raylib.Raylib.SetTextureWrap;

import java.nio.file.Paths;

import com.override.engine.AssetManager;
import com.override.engine.GameObject;
import com.override.engine.Global;
import com.raylib.Raylib;

public class Image extends GameObject {
    public static int filter;
    public Raylib.Rectangle source = new Raylib.Rectangle().x(0).y(0).width(0).height(0);
    public Raylib.Texture texture;

    public void setFilter(int filterType) {
        if (filterType == 1) {
            SetTextureFilter(texture, Raylib.TEXTURE_FILTER_BILINEAR);
        } else {
            SetTextureFilter(texture, Raylib.TEXTURE_FILTER_POINT);
        }
    }

    public void setWrap(String wrapMode) {
    }

    public void setTexture(String path) {
        this.texture = AssetManager.pickAsset(
                Paths.get(Global.projectPath, path.replace("\\", "/").replaceAll("//+", "/")).toString(), "image");
    }

    public Image(String path, float x, float y, float width, float height) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = AssetManager.pickAsset(
                Paths.get(Global.projectPath, path.replace("\\", "/").replaceAll("//+", "/")).toString(), "image");
    }

    @Override
    public void draw() {
        pos
                .x(Global.windowWidth / 2f + this.getWorldX())
                .y(Global.windowHeight / 2f - this.getWorldY())
                .width(this.width * this.scale)
                .height(this.height * this.scale);

        origin.x(pos.width() * anchorX).y(pos.height() * anchorY);

        if (texture == null) {
            DrawRectanglePro(
                    pos,
                    origin,
                    -this.getWorldAngle(),
                    new Raylib.Color().r((byte) 255).g((byte) 0).b((byte) 255).a((byte) 255));
            return;
        }

        source.width(mirroredX ? -texture.width() : texture.width());
        source.height(mirroredY ? -texture.height() : texture.height());

        DrawTexturePro(texture, source, pos, origin, -this.getWorldAngle(),
                new Raylib.Color().r(color.get("r").tobyte()).g(color.get("g").tobyte()).b(color.get("b").tobyte())
                        .a(color.get("a").tobyte()));
    }

}
