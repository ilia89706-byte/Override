package com.override.engine;

import com.raylib.Raylib;
import com.raylib.Colors;
import com.raylib.Raylib.Rectangle;
import com.raylib.Raylib.Vector2;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Segment;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameObject {
    public float x = 0, y = 0;
    public float width = 0, height = 0;
    public float angle = 0;
    public float radius = 0;
    public float scale = 1.0f;
    public float anchorX = 0.5f;
    public float anchorY = 0.5f;
    public float anchorAngle = 0.0f;
    public LuaTable data = new LuaTable();

    public LuaValue onTouchListener;

    public int layer = 1;
    public boolean isVisible = true;
    public boolean debug = false;
    public boolean mirroredX = false;
    public boolean mirroredY = false;
    public LuaValue luaSelf = LuaValue.NIL;

    public String tag = "";
    public String name = "";

    public LuaTable color = new LuaTable();
    public com.raylib.Raylib.Color raylibColor = new Raylib.Color();
    public Body body;
    public Vector2 origin = new Vector2();
    public Rectangle pos = new Rectangle();
    public Rectangle renderRect = new Rectangle();
    private final Map<String, List<LuaValue>> eventListeners = new HashMap<>();

    public GameObject parent = null;
    public List<GameObject> childrens = new ArrayList<>();

    public AABB aabb = new AABB(x, y, width, height);

    public GameObject() {
        color.set("r", 255);
        color.set("g", 255);
        color.set("b", 255);
        color.set("a", 255);
    }

    public GameObject setRenderRect(float x, float y, float width, float height) {
        this.renderRect.x(x);
        this.renderRect.y(y);
        this.renderRect.width(width);
        this.renderRect.height(height);
        return this;
    }

    public GameObject on(String eventType, LuaValue callback) {
        if (callback == null || !callback.isfunction())
            return this;

        this.eventListeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(callback);

        Events.registerObjectToEvent(eventType, this);
        return this;
    }

    public void emit(String eventType, LuaValue data) {
        List<LuaValue> listeners = eventListeners.get(eventType);
        if (listeners != null) {
            for (LuaValue callback : listeners) {
                try {
                    if (this.luaSelf != null && !this.luaSelf.isnil()) {
                        callback.call(this.luaSelf, data);
                    } else {
                        callback.call(data);
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void remove() {
        if (this.body != null) {
            Physics.world.removeBody(this.body);
        }
        Display.displayObjects.remove(this);
        if (parent != null) {
            parent.childrens.remove(this);
        }
        for (int i = childrens.size() - 1; i >= 0; i--) {
            childrens.get(i).remove();
        }
        Events.unregisterObjectFromAll(this);
    }

    public float getWorldX() {
        if (this.body != null) {
            return (float) (this.body.getTransform().getTranslationX() * Physics.SCALE);
        }
        if (parent != null) {
            double parentAngleRad = Math.toRadians(parent.getWorldAngle());
            double cosA = Math.cos(parentAngleRad);
            double sinA = Math.sin(parentAngleRad);
            return parent.getWorldX() + (float) (this.x * cosA - this.y * sinA);
        }
        return this.x;
    }

    public float getWorldY() {
        if (this.body != null) {
            return (float) (this.body.getTransform().getTranslationY() * Physics.SCALE);
        }
        if (parent != null) {
            double parentAngleRad = Math.toRadians(parent.getWorldAngle());
            double cosA = Math.cos(parentAngleRad);
            double sinA = Math.sin(parentAngleRad);
            return parent.getWorldY() + (float) (this.x * sinA + this.y * cosA);
        }
        return this.y;
    }

    public float getWorldAngle() {
        if (this.body != null) {
            return (float) Math.toDegrees(this.body.getTransform().getRotationAngle());
        }
        if (parent != null) {
            return parent.getWorldAngle() + this.angle;
        }
        return this.angle;
    }

    public GameObject setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        if (this.body != null) {
            this.body.getTransform().setTranslation(x / Physics.SCALE, y / Physics.SCALE);
        }
        return this;
    }

    public GameObject setWidth(float val) {
        this.width = val;
        return this;
    }

    public GameObject setHeight(float val) {
        this.height = val;
        return this;
    }

    public GameObject rotate(float angleOffset) {
        this.angle += angleOffset;
        if (this.body != null) {
            this.body.getTransform().setRotation(Math.toRadians(this.angle));
        }
        return this;
    }

    public GameObject setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public GameObject lookAt(float targetX, float targetY) {
        double angleRad = Math.atan2(targetY - this.y, targetX - this.x);
        this.angle = (float) Math.toDegrees(angleRad);
        if (this.body != null) {
            this.body.getTransform().setRotation(angleRad);
        }
        return this;
    }

    public GameObject insert(GameObject child) {
        if (child != null && child != this) {
            if (child.parent != null) {
                child.parent.childrens.remove(child);
            }
            child.parent = this;
            this.childrens.add(child);
        }
        return this;
    }

    public GameObject clear() {
        for (int i = childrens.size() - 1; i >= 0; i--) {
            childrens.get(i).remove();
        }
        this.childrens.clear();
        return this;
    }

    public GameObject find(String nameOrTag) {
        for (GameObject child : childrens) {
            if (child.name.equals(nameOrTag) || child.tag.equals(nameOrTag)) {
                return child;
            }
            GameObject found = child.find(nameOrTag);
            if (found != null)
                return found;
        }
        return null;
    }

    public GameObject toFront() {
        this.layer = Integer.MAX_VALUE;
        return this;
    }

    public GameObject toBack() {
        this.layer = Integer.MIN_VALUE;
        return this;
    }

    public void setPhysBody(String type) {
        if (this.body == null)
            this.body = new Body();

        if (type.equalsIgnoreCase("dynamic")) {
            this.body.setMass(org.dyn4j.geometry.MassType.NORMAL);
        } else if (type.equalsIgnoreCase("static")) {
            this.body.setMass(org.dyn4j.geometry.MassType.INFINITE);
        } else if (type.equalsIgnoreCase("kinematic")) {
            this.body.setMass(org.dyn4j.geometry.MassType.INFINITE);
        }

        if (!Physics.world.containsBody(this.body)) {
            Physics.world.addBody(this.body);
        }
        setPosition(this.x, this.y);
    }

    public GameObject setFixedRotation(boolean fixed) {
        if (this.body != null) {
            this.body.updateMass();
            if (fixed && !this.body.isStatic() && !this.body.isKinematic()) {
                this.body.setMass(org.dyn4j.geometry.MassType.FIXED_ANGULAR_VELOCITY);
            }
        }
        return this;
    }

    public GameObject setLinearVelocity(float vx, float vy) {
        if (this.body != null) {
            this.body.setAtRest(false);
            this.body.getLinearVelocity().set(vx, vy);
        }
        return this;
    }

    public GameObject applyLinearImpulse(float ix, float iy) {
        if (this.body != null) {
            this.body.setAtRest(false);
            this.body.applyImpulse(new org.dyn4j.geometry.Vector2(ix, iy));
        }
        return this;
    }

    public void draw() {

        for (GameObject child : childrens) {
            if (child.isVisible)
                child.draw();
        }
        if (debug)
            drawDebug();
    }

    public GameObject setColor(int r, int g, int b, int a) {
        this.color.set("r", r);
        this.color.set("g", g);
        this.color.set("b", b);
        this.color.set("a", a);

        return this;
    }

    public GameObject setColor(int r, int g, int b) {
        this.setColor(r, g, b, color.get("a").toint());
        return this;
    }

    public GameObject setColor(String hex) {
        if (hex.startsWith("#"))
            hex = hex.substring(1);
        if (hex.length() == 6)
            hex += "FF";
        try {
            long colorLong = Long.parseLong(hex, 16);
            int r = (int) ((colorLong >> 24) & 0xFF);
            int g = (int) ((colorLong >> 16) & 0xFF);
            int b = (int) ((colorLong >> 8) & 0xFF);
            int a = (int) (colorLong & 0xFF);
            this.setColor(r, g, b, a);
        } catch (NumberFormatException e) {
            System.err.println(hex);
        }
        return this;
    }

    public GameObject setDebug(boolean debugMode) {
        this.debug = debugMode;
        return this;
    }

    public void drawDebug() {
        if (body == null || body.getFixtures().isEmpty())
            return;

        Raylib.Color color;
        if (body.isStatic()) {
            color = Raylib.GetColor(0x00F0F5C8);
        } else if (body.isAtRest()) {
            color = Raylib.GetColor(0x96969696);
        } else {
            color = Raylib.GetColor(0x00FF00C8);
        }

        int sx = Global.windowWidth / 2 + (int) (this.x);
        int yInvert = Global.windowHeight / 2 - (int) (this.y);

        double pAngle = body.getTransform().getRotationAngle();
        double cosA = Math.cos(pAngle);
        double sinA = Math.sin(pAngle);

        for (BodyFixture fixture : body.getFixtures()) {
            Convex shape = fixture.getShape();

            if (shape instanceof Circle) {
                Circle circle = (Circle) shape;
                int r = (int) (circle.getRadius() * Physics.SCALE);
                double ox = circle.getCenter().x * Physics.SCALE;
                double oy = circle.getCenter().y * Physics.SCALE;

                int cx = (int) (sx + (ox * cosA - oy * sinA));
                int cy = (int) (yInvert - (ox * sinA + oy * cosA));

                Raylib.DrawCircleLines(cx, cy, r, color);
            }

            else if (shape instanceof Polygon) {
                Polygon poly = (Polygon) shape;
                org.dyn4j.geometry.Vector2[] vertices = poly.getVertices();
                Vector2[] worldVerts = new Vector2[vertices.length];

                for (int i = 0; i < vertices.length; i++) {
                    double vx = vertices[i].x * Physics.SCALE;
                    double vy = vertices[i].y * Physics.SCALE;
                    float rx = (float) (sx + (vx * cosA - vy * sinA));
                    float ry = (float) (yInvert - (vx * sinA + vy * cosA));
                    worldVerts[i] = new Vector2().x(rx).y(ry);
                }

                int count = worldVerts.length;
                for (int i = 0; i < count; i++) {
                    Raylib.DrawLineEx(worldVerts[i], worldVerts[(i + 1) % count], 1.5f, color);
                }
            }

            else if (shape instanceof Segment) {
                Segment segment = (Segment) shape;
                org.dyn4j.geometry.Vector2 a = segment.getPoint1();
                org.dyn4j.geometry.Vector2 b = segment.getPoint2();
                float ax = (float) (sx + (a.x * Physics.SCALE * cosA - a.y * Physics.SCALE * sinA));
                float ay = (float) (yInvert - (a.x * Physics.SCALE * sinA + a.y * Physics.SCALE * cosA));
                float bx = (float) (sx + (b.x * Physics.SCALE * cosA - b.y * Physics.SCALE * sinA));
                float by = (float) (yInvert - (b.x * Physics.SCALE * sinA + b.y * Physics.SCALE * cosA));
                Raylib.DrawLineEx(new Vector2().x(ax).y(ay), new Vector2().x(bx).y(by), 1.5f, color);
            }
        }
        Raylib.DrawLineEx(new Vector2().x(sx - 4).y(yInvert), new Vector2().x(sx + 4).y(yInvert), 1.0f, Colors.RED);
        Raylib.DrawLineEx(new Vector2().x(sx).y(yInvert - 4), new Vector2().x(sx).y(yInvert + 4), 1.0f, Colors.RED);
        double vx = body.getLinearVelocity().x * 0.1 * Physics.SCALE;
        double vy = -body.getLinearVelocity().y * 0.1 * Physics.SCALE;
        if (Math.abs(vx) > 1 || Math.abs(vy) > 1) {
            float startX = sx;
            float startY = yInvert;
            float endX = (float) (sx + vx);
            float endY = (float) (yInvert + vy);
            Vector2 startVec = new Vector2().x(startX).y(startY);
            Vector2 endVec = new Vector2().x(endX).y(endY);
            Raylib.DrawLineEx(startVec, endVec, 1.5f, Colors.RED);
            double angleVel = Math.atan2(vy, vx);
            float arrowLength = 6.0f;
            float arrowAngle = 0.45f;
            float arrow1X = (float) (endX - arrowLength * Math.cos(angleVel - arrowAngle));
            float arrow1Y = (float) (endY - arrowLength * Math.sin(angleVel - arrowAngle));
            float arrow2X = (float) (endX - arrowLength * Math.cos(angleVel + arrowAngle));
            float arrow2Y = (float) (endY - arrowLength * Math.sin(angleVel + arrowAngle));
            Raylib.DrawLineEx(endVec, new Vector2().x(arrow1X).y(arrow1Y), 1.2f, Colors.RED);
            Raylib.DrawLineEx(endVec, new Vector2().x(arrow2X).y(arrow2Y), 1.2f, Colors.RED);
        }
    }
}