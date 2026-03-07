package io.github.kurrycat2004.enchlib.common;

import io.github.kurrycat2004.enchlib.config.settings.ClientSettings;
import io.github.kurrycat2004.enchlib.util.RomanNumeralUtil;

public class EnchLevel {
    private static final String ENCH_LEVEL_PREFIX = "enchantment.level.";

    private EnchLevel() {}

    public static boolean canFormatEnchLevel(String key) {
        if (ClientSettings.INSTANCE.enchLevelTranslation == ClientSettings.EnchLevelTranslation.VANILLA) {
            return false;
        }
        return key.startsWith(ENCH_LEVEL_PREFIX);
    }

    public static String formatEnchLevel(String key) {
        if (!canFormatEnchLevel(key)) return null;

        // this may overflow but at that point whatever
        long level = 0;
        int start = ENCH_LEVEL_PREFIX.length();
        int end = key.length();
        for (int i = start; i < end; i++) {
            char c = key.charAt(i);
            if (c < '0' || c > '9') return null;
            level = level * 10 + (c - '0');
        }

        return switch (ClientSettings.INSTANCE.enchLevelTranslation) {
            case NUMBERS -> String.valueOf(level);
            case ROMAN_NUMERALS ->
                    level < 1 || level > 3999 ? String.valueOf(level) : RomanNumeralUtil.fromInt((int) level);
            default -> null;
        };
    }
}
