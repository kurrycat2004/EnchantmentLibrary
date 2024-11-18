package io.github.kurrycat2004.enchlib.util;

import org.jetbrains.annotations.Range;

public class MathUtil {
    public static double uLongToDouble(long value) {
        double result = value & Long.MAX_VALUE;
        if ((value & ~Long.MAX_VALUE) != 0) {
            result += 0x4000000000000000L;
            result += 0x4000000000000000L;
        }
        return result;
    }

    public static int clampLongToInt(long value) {
        if (value > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (value < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        return (int) value;
    }

    /**
     * @param value The value to clamp
     * @return A signed int which is equal to the value clamped to the range of an unsigned int
     */
    @Range(from = 0, to = Integer.MAX_VALUE)
    public static int clampULongToInt(long value) {
        if (Long.numberOfLeadingZeros(value) < Integer.SIZE + 1) return Integer.MAX_VALUE;
        return (int) value;
    }

    public static short clampIntToShort(int value) {
        if (value > Short.MAX_VALUE) return Short.MAX_VALUE;
        if (value < Short.MIN_VALUE) return Short.MIN_VALUE;
        return (short) value;
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int mapClamp(int value, int start1, int stop1, int start2, int stop2) {
        return clamp(map(value, start1, stop1, start2, stop2), start2, stop2);
    }

    public static int map(int value, int start1, int stop1, int start2, int stop2) {
        return (int) Math.round(map((double) value, start1, stop1, start2, stop2));
    }

    public static double map(double value, double start1, double stop1, double start2, double stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }

    public static int ceilDiv(int v, int d) {
        return (v + d - 1) / d;
    }
}
