package com.override.engine;

import com.raylib.Raylib;
import com.raylib.Raylib.Texture;
import com.raylib.Raylib.Sound;
import com.raylib.Raylib.Font;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class AssetManager {

    private static final Map<String, Map<String, Object>> assetsCache = new HashMap<>();

    private static final Map<String, Function<String, Object>> loaders = new HashMap<>();

    static {
        assetsCache.put("image", new HashMap<>());
        assetsCache.put("sound", new HashMap<>());
        assetsCache.put("font", new HashMap<>());

        loaders.put("image", Raylib::LoadTexture);
        loaders.put("sound", Raylib::LoadSound);
        loaders.put("font", Raylib::LoadFont);
    }

    /**
     * @param assetPath
     * @param assetType
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public static <T> T pickAsset(String assetPath, String assetType) {
        if (!assetsCache.containsKey(assetType)) {
            System.out.println("[Override] Unknown asset type: " + assetType);
            return null;
        }

        Map<String, Object> targetCache = assetsCache.get(assetType);

        if (targetCache.containsKey(assetPath)) {
            return (T) targetCache.get(assetPath);
        }

        try {
            Function<String, Object> loaderFunc = loaders.get(assetType);
            Object loadedAsset = loaderFunc.apply(assetPath);

            if (loadedAsset == null || !isValid(loadedAsset)) {
                System.out.println("[Override] ErrorLoading: " + assetPath);
                return null;
            }

            targetCache.put(assetPath, loadedAsset);
            return (T) loadedAsset;

        } catch (Exception e) {
            System.out.println("[Override] noFile: " + assetPath + ". Details: " + e.getMessage());
            return null;
        }
    }

    private static boolean isValid(Object asset) {
        if (asset instanceof Texture) {
            return ((Texture) asset).id() != 0;
        }
        if (asset instanceof Sound) {
            return ((Sound) asset).stream() != null;
        }
        if (asset instanceof Font) {
            return ((Font) asset).texture().id() != 0;
        }
        return true;
    }

    public static void unloadAll() {
        for (Object texture : assetsCache.get("image").values()) {
            Raylib.UnloadTexture((Texture) texture);
        }
        for (Object sound : assetsCache.get("sound").values()) {
            Raylib.UnloadSound((Sound) sound);
        }
        for (Object font : assetsCache.get("font").values()) {
            Raylib.UnloadFont((Font) font);
        }

        assetsCache.get("image").clear();
        assetsCache.get("sound").clear();
        assetsCache.get("font").clear();
    }
}
