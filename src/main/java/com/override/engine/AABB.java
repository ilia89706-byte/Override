package com.override.engine;

import com.raylib.Raylib;
import com.raylib.Colors;

public class AABB {
    private float minX, minY;
    private float maxX, maxY;
    private float anchorX = 0.5f;
    private float anchorY = 0.5f;
    private float scale = 1.0f;
    private float rawWidth = 0.0f;
    private float rawHeight = 0.0f;

    public AABB() {
        reset();
    }

    public AABB(float centerX, float centerY, float width, float height) {
        updateBounds(centerX, centerY, width, height);
    }

    public void reset() {
        this.minX = Float.POSITIVE_INFINITY;
        this.minY = Float.POSITIVE_INFINITY;
        this.maxX = Float.NEGATIVE_INFINITY;
        this.maxY = Float.NEGATIVE_INFINITY;
        this.rawWidth = 0.0f;
        this.rawHeight = 0.0f;
    }

    public void updateBounds(float centerX, float centerY, float width, float height) {
        this.rawWidth = width;
        this.rawHeight = height;

        float scaledHalfW = (width * scale) * anchorX;
        float scaledHalfH = (height * scale) * anchorY;

        this.minX = centerX - scaledHalfW;
        this.maxX = centerX + ((width * scale) - scaledHalfW);

        this.minY = centerY - scaledHalfH;
        this.maxY = centerY + ((height * scale) - scaledHalfH);
    }

    public void setPosition(float centerX, float centerY) {
        float scaledHalfW = (rawWidth * scale) * anchorX;
        float scaledHalfH = (rawHeight * scale) * anchorY;

        this.minX = centerX - scaledHalfW;
        this.maxX = centerX + ((rawWidth * scale) - scaledHalfW);

        this.minY = centerY - scaledHalfH;
        this.maxY = centerY + ((rawHeight * scale) - scaledHalfH);
    }

    public void setX(float centerX) {
        float scaledHalfW = (rawWidth * scale) * anchorX;
        this.minX = centerX - scaledHalfW;
        this.maxX = centerX + ((rawWidth * scale) - scaledHalfW);
    }

    public void setY(float centerY) {
        float scaledHalfH = (rawHeight * scale) * anchorY;
        this.minY = centerY - scaledHalfH;
        this.maxY = centerY + ((rawHeight * scale) - scaledHalfH);
    }

    public void setWidth(float width) {
        float centerX = getCenterX();
        float centerY = getCenterY();
        updateBounds(centerX, centerY, width, this.rawHeight);
    }

    public void setHeight(float height) {
        float centerX = getCenterX();
        float centerY = getCenterY();
        updateBounds(centerX, centerY, this.rawWidth, height);
    }

    public void drawDebug() {
        float screenCenterX = Global.windowWidth / 2.0f;
        float screenCenterY = Global.windowHeight / 2.0f;

        float rectWidth = maxX - minX;
        float rectHeight = maxY - minY;

        float screenX = screenCenterX + minX;
        float screenY = screenCenterY - maxY;

        Raylib.Rectangle rect = new Raylib.Rectangle();
        rect.x(screenX);
        rect.y(screenY);
        rect.width(rectWidth);
        rect.height(rectHeight);

        Raylib.DrawRectangleLinesEx(rect, 2.0f, Colors.LIME);
    }

    public boolean collide(AABB other) {
        return this.minX < other.maxX &&
                this.maxX > other.minX &&
                this.minY < other.maxY &&
                this.maxY > other.minY;
    }

    public boolean hasInRect(float pointX, float pointY) {
        return pointX >= this.minX &&
                pointX <= this.maxX &&
                pointY >= this.minY &&
                pointY <= this.maxY;
    }

    public void encircle(AABB other) {
        this.minX = Math.min(this.minX, other.minX);
        this.minY = Math.min(this.minY, other.minY);
        this.maxX = Math.max(this.maxX, other.maxX);
        this.maxY = Math.max(this.maxY, other.maxY);
        this.rawWidth = (maxX - minX) / scale;
        this.rawHeight = (maxY - minY) / scale;
    }

    public void addPoint(float pointX, float pointY) {
        this.minX = Math.min(this.minX, pointX);
        this.minY = Math.min(this.minY, pointY);
        this.maxX = Math.max(this.maxX, pointX);
        this.maxY = Math.max(this.maxY, pointY);
        this.rawWidth = (maxX - minX) / scale;
        this.rawHeight = (maxY - minY) / scale;
    }

    public void translate(float dx, float dy) {
        this.minX += dx;
        this.maxX += dx;
        this.minY += dy;
        this.maxY += dy;
    }

    public float getMinX() {
        return minX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public float getWidth() {
        return maxX - minX;
    }

    public float getHeight() {
        return maxY - minY;
    }

    public float getRawWidth() {
        return rawWidth;
    }

    public float getRawHeight() {
        return rawHeight;
    }

    public float getCenterX() {
        return minX + (getWidth() / 2);
    }

    public float getCenterY() {
        return minY + (getHeight() / 2);
    }

    public float getAnchorX() {
        return anchorX;
    }

    public void setAnchorX(float anchorX) {
        float centerX = getCenterX();
        float centerY = getCenterY();
        this.anchorX = anchorX;
        updateBounds(centerX, centerY, this.rawWidth, this.rawHeight);
    }

    public float getAnchorY() {
        return anchorY;
    }

    public void setAnchorY(float anchorY) {
        float centerX = getCenterX();
        float centerY = getCenterY();
        this.anchorY = anchorY;
        updateBounds(centerX, centerY, this.rawWidth, this.rawHeight);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        float centerX = getCenterX();
        float centerY = getCenterY();
        this.scale = scale;
        updateBounds(centerX, centerY, this.rawWidth, this.rawHeight);
    }
}
