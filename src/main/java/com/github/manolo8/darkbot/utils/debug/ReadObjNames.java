package com.github.manolo8.darkbot.utils.debug;

import static com.github.manolo8.darkbot.Main.API;

/**
 * Reads object names from memory.
 * Also reads strings on that address.
 */
public class ReadObjNames {

    public static void of(long address) {
        of(address, 0x400);
    }

    public static void of(long address, int maxOffset) {
        System.out.println("==========BEGIN==========\n");

        for (int offset = 0; offset < maxOffset; offset++) {
            long addr = API.readMemoryLong(address + offset);
            //prints string at offset
            print(addr, offset, false);

            if (isInvalid(addr)) continue;
            addr = API.readMemoryLong(addr + 0x10);
            if (isInvalid(addr)) continue;
            addr = API.readMemoryLong(addr + 0x28);
            if (isInvalid(addr)) continue;

            //prints object name at offset
            print(API.readMemoryLong(addr + 0x90), offset, true);
        }
    }

    private static boolean isInvalid(long address) {
        return (address < 0xFF00000000L || address > 0xF000000000000L);
    }

    private static void print(long address, int offset, boolean isObject) {
        String result = API.readMemoryString(address);

        //result = replace(result.trim());

        if (!result.trim().isEmpty() && !result.trim().equals("ERROR"))
            System.out.println(formatString(result, offset, isObject));
    }

    private static String formatString(String s, int offset, boolean isObject) {
        return  (isObject ? "\u001B[34mOBJ" : "\u001B[32mSTR") +  "[" + offset + "]\u001B[0m " + s;
    }

    private static String replace(String s) {
        switch (s) {
            case "_-y4r":
                s = "IChangeListener";
                break;
            case "_-q2d":
                s = "BooleanListener";
                break;
            case "_-Ma":
                s = "Location";
                break;
            case "_-z35":
                s = "LocationInfo";
                break;
            case "_-w6":
                s = "Entity";
                break;
            case "_-v4d":
                s = "Main";
        }

        return s;
    }
}
