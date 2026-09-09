package com.override.engine;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import java.io.IOException;
import com.override.*;
import com.override.Override;

public class FolderWatcher {

    private static long lastEventTime = 0;
    private static final long DEBOUNCE_DELAY_MS = 100;

    public static void start(String[] args) {
        Path dir = Path.of(Global.projectPath);

        new Thread(() -> {
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                dir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

                while (true) {
                    WatchKey key = watchService.take();

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        Path fileName = (Path) event.context();

                        long currentTime = System.currentTimeMillis();
                        if ((currentTime - lastEventTime) > DEBOUNCE_DELAY_MS) {

                            onFileChanged(kind, fileName);

                            lastEventTime = currentTime;
                        }
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void onFileChanged(WatchEvent.Kind<?> eventKind, Path fileName) {
        if (fileName.toString().endsWith(".lua")) {
            System.out.println("[Override] " + eventKind.name() + " -> File: " + fileName);

            Override.loadScene("main.lua");
        }
    }
}
