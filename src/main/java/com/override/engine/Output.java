package com.override.engine;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;

public class Output extends OneArgFunction {

    @Override
    public LuaValue call(LuaValue arg) {
        if (arg.istable()) {
            System.out.println(formatTable((LuaTable) arg, ""));
        } else if (arg.isnil()) {
            System.out.println("nil");
        } else {
            System.out.println(arg.tojstring());
        }
        return LuaValue.NIL;
    }

    private String formatTable(LuaTable table, String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        String nextIndent = indent + "    ";
        LuaValue key = LuaValue.NIL;

        Varargs next;
        while (!(next = table.next(key)).arg1().isnil()) {
            key = next.arg1();
            LuaValue value = next.arg(2);

            sb.append(nextIndent);

            if (key.isnumber() || key.isstring()) {
                sb.append("[").append(key.tojstring()).append("]");
            } else {
                sb.append("[").append(key.typename()).append("]");
            }

            sb.append(" = ");

            if (value.istable()) {
                sb.append(formatTable((LuaTable) value, nextIndent));
            } else if (value.isstring()) {
                sb.append("\"").append(value.tojstring()).append("\"");
            } else {
                sb.append(value.tojstring());
            }

            sb.append(",\n");
        }

        sb.append(indent).append("}");
        return sb.toString();
    }
}
