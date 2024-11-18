package io.github.kurrycat2004.enchlib.util;

import org.jetbrains.annotations.Range;

import java.util.TreeMap;

public class RomanNumeralUtil {
    private final static TreeMap<Integer, String> map = new TreeMap<>() {
        {
            //@formatter:off
            put(1000,  "M");
            put(900 , "CM");
            put(500 ,  "D");
            put(400 , "CD");
            put(100 ,  "C");
            put(90  , "XC");
            put(50  ,  "L");
            put(40  , "XL");
            put(10  ,  "X");
            put(9   , "IX");
            put(5   ,  "V");
            put(4   , "IV");
            put(1   ,  "I");
            //@formatter:on
        }
    };

    public static String fromInt(@Range(from = 1, to = 3999) int n) {
        //noinspection ConstantValue
        if (n < 1 || n > 3999) throw new IllegalArgumentException("N<1 and N>3999 is not defined");

        int floor = map.floorKey(n);
        if (n == floor) return map.get(n);
        return map.get(floor) + fromInt(n - floor);
    }
}
