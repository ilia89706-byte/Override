package com.override;

import java.io.FileWriter;

import com.override.editor.EditorMain;
import com.override.engine.FolderWatcher;
import com.override.engine.Global;
import com.override.engine.Menu;

public class Main {
    public static void main(String[] args) {
        // Menu menu = new Menu();
        // menu.showMenu();
        // FolderWatcher.start(args);
        Override.start(false);

    }
}
