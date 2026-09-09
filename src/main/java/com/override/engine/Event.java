package com.override.engine;

import java.util.ArrayList;
import java.util.List;
import org.luaj.vm2.LuaValue;

class EventObj {
    public LuaValue callback;
    public String name;

    public EventObj(String name) {
        this.name = name;
    }

    public void add(GameObject connectable, LuaValue listener) {
    }

    public void remove(LuaValue listener) {
    }
}

public class Event {

    // public static EventObj create() {
    // EventObj eo = new EventObj();
    // return eo;
    // }
}
