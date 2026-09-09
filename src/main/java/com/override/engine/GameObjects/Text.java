package com.override.engine.GameObjects;

import static com.raylib.Raylib.DrawTextPro;

import java.util.ArrayList;
import java.util.List;

import com.override.engine.AssetManager;
import com.override.engine.GameObject;
import com.override.engine.Global;
import com.raylib.Raylib;

public class Text extends GameObject {
    public static int SCALE_SMOOTH;

    public enum AlignmentEnum {
        LEFT, CENTER, RIGHT
    }

    public String text;
    public int fontSize = 30;
    public int fontSpacing = 1;
    public Raylib.Font font;

    public String aligment = "left";

    private AlignmentEnum currentAlignment = AlignmentEnum.LEFT;

    private final List<String> lines = new ArrayList<>();
    private String lastText = "";

    private final Raylib.Vector2 textPosition = new Raylib.Vector2();
    private final Raylib.Vector2 blockOrigin = new Raylib.Vector2();

    public Text(String text, float x, float y, String fontPath, int fontSize) {
        super();
        this.x = x;
        this.y = y;
        this.text = text;
        this.fontSize = fontSize;
        this.font = AssetManager.pickAsset(Global.projectPath + "\\" + fontPath, "font");
    }

    @Override
    public void draw() {
        if (text == null || text.isEmpty())
            return;

        updateAlignmentFromLua();

        float currentSize = fontSize * scale;

        if (!text.equals(lastText)) {
            lines.clear();
            String[] split = text.split("\n");
            for (String s : split) {
                lines.add(s);
            }
            lastText = text;
        }

        float maxLineWidth = 0;
        for (String line : lines) {
            Raylib.Vector2 size = Raylib.MeasureTextEx(font, line, currentSize, fontSpacing);
            if (size.x() > maxLineWidth) {
                maxLineWidth = size.x();
            }
        }
        float totalHeight = lines.size() * currentSize;

        this.width = maxLineWidth / (scale != 0 ? scale : 1);
        this.height = totalHeight / (scale != 0 ? scale : 1);

        float worldX = Global.windowWidth / 2f + this.getWorldX();
        float worldY = Global.windowHeight / 2f - this.getWorldY();

        blockOrigin.x(maxLineWidth * anchorX).y(totalHeight * anchorY);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            Raylib.Vector2 size = Raylib.MeasureTextEx(font, line, currentSize, fontSpacing);
            float lineWidth = size.x();

            float alignOffsetX = 0;
            if (currentAlignment == AlignmentEnum.CENTER) {
                alignOffsetX = (maxLineWidth - lineWidth) / 2f;
            } else if (currentAlignment == AlignmentEnum.RIGHT) {
                alignOffsetX = maxLineWidth - lineWidth;
            }

            textPosition.x(worldX).y(worldY);

            float lineLocalY = i * currentSize;

            Raylib.Vector2 currentLineOrigin = new Raylib.Vector2()
                    .x(blockOrigin.x() - alignOffsetX)
                    .y(blockOrigin.y() - lineLocalY);

            DrawTextPro(font, line, textPosition, currentLineOrigin, this.getWorldAngle(),
                    currentSize, fontSpacing,
                    new Raylib.Color().r(color.get("r").tobyte()).g(color.get("g").tobyte()).b(color.get("b").tobyte())
                            .a(color.get("a").tobyte()));
        }
    }

    private void updateAlignmentFromLua() {
        if (aligment == null) {
            currentAlignment = AlignmentEnum.LEFT;
            return;
        }

        switch (aligment.toLowerCase()) {
            case "center":
                currentAlignment = AlignmentEnum.CENTER;
                break;
            case "right":
                currentAlignment = AlignmentEnum.RIGHT;
                break;
            case "left":
            default:
                currentAlignment = AlignmentEnum.LEFT;
                break;
        }
    }
}