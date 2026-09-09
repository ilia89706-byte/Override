package com.override.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Render {
    private static final List<GameObject> rootObjects = new ArrayList<>();
    private static final Comparator<GameObject> layerComparator = (o1, o2) -> Integer.compare(o1.layer, o2.layer);

    public static void draw() {
        float halfW = Global.windowWidth / 2.0f;
        float halfH = Global.windowHeight / 2.0f;

        rootObjects.clear();
        for (int i = 0; i < Display.displayObjects.size(); i++) {
            GameObject obj = Display.displayObjects.get(i);
            if (obj.parent == null) {
                rootObjects.add(obj);
            }
        }

        rootObjects.sort(layerComparator);

        for (int i = 0; i < rootObjects.size(); i++) {
            renderObject(rootObjects.get(i), -halfW, halfW, -halfH, halfH);
        }
    }

    private static void renderObject(GameObject obj, float camMinX, float camMaxX, float camMinY, float camMaxY) {
        if (!obj.isVisible)
            return;

        if (obj.aabb != null) {
            boolean isOutside = obj.aabb.getMaxX() < camMinX ||
                    obj.aabb.getMinX() > camMaxX ||
                    obj.aabb.getMaxY() < camMinY ||
                    obj.aabb.getMinY() > camMaxY;
            if (isOutside) {
                return;
            }
        }

        obj.draw();

        if (obj.debug) {
            obj.drawDebug();
            if (obj.aabb != null) {
                obj.aabb.drawDebug();
            }
        }

        if (!obj.childrens.isEmpty()) {
            obj.childrens.sort(layerComparator);
            for (int i = 0; i < obj.childrens.size(); i++) {
                renderObject(obj.childrens.get(i), camMinX, camMaxX, camMinY, camMaxY);
            }
        }
    }
}
