package io.github.kurrycat2004.enchlib.gui.components;

import io.github.kurrycat2004.enchlib.util.LangUtil;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.Nullable;

@NonnullByDefault
public class EnchTooltip {
    private static Enchantment enchantToAdd = null;
    private static boolean hasEnchant = false;
    private static short levelToAdd = 0;

    public static void preTooltip(Enchantment enchant, short level) {
        enchantToAdd = enchant;
        hasEnchant = false;
        levelToAdd = level;
    }

    public static void postTooltip() {
        enchantToAdd = null;
        hasEnchant = false;
        levelToAdd = 0;
    }

    public static void setHasEnchant(boolean hasEnchant) {
        EnchTooltip.hasEnchant = hasEnchant;
    }

    public static boolean hasEnchant() {
        return hasEnchant;
    }

    public static @Nullable Enchantment getEnchantToAdd() {
        return enchantToAdd;
    }

    public static short getLevelToAdd() {
        return levelToAdd;
    }

    public static String getAddLine() {
        return TextFormatting.GREEN + "+ " + enchantToAdd.getTranslatedName(levelToAdd);
    }

    public static String getDiffLevelLine(int originalLevel) {
        String s = LangUtil.localize(enchantToAdd.getName());
        if (enchantToAdd.isCurse()) s = TextFormatting.RED + s;

        if (originalLevel == 1 && enchantToAdd.getMaxLevel() == 1) return s;
        return s + " " +
               TextFormatting.RED + TextFormatting.STRIKETHROUGH + localizeEnchLevel(originalLevel) +
               TextFormatting.RESET + " " + TextFormatting.GREEN + localizeEnchLevel(levelToAdd);
    }

    public static String localizeEnchLevel(int level) {
        return LangUtil.localize("enchantment.level." + level);
    }
}
