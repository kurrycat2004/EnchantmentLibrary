package io.github.kurrycat2004.enchlib.util;

import io.github.kurrycat2004.enchlib.EnchLibMod;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NonnullByDefault
public class EnchantmentUtil {
    public static final String VANILLA_TAG_ENCHANTMENTS = "StoredEnchantments";
    public static final String VANILLA_TAG_ID = "id";
    public static final String VANILLA_TAG_LEVEL = "lvl";

    private EnchantmentUtil() {}

    public static ItemStack getBookProto(Enchantment enchantment, short level) {
        return getBook(enchantment, level, 1);
    }

    public static ItemStack getBook(Enchantment enchantment, short level, int count) {
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(enchantment, level));
        stack.setCount(count);
        return stack;
    }

    public static void warnUnknownEnchantment(int id) {
        EnchLibMod.LOGGER.warn("Enchantment with id {} not found", id);
    }

    public static @Nullable NBTTagList getEnchantmentTags(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) return null;
        return tag.getTagList(VANILLA_TAG_ENCHANTMENTS, Constants.NBT.TAG_COMPOUND);
    }

    public static List<EnchantmentData> getEnchantments(ItemStack stack) {
        List<EnchantmentData> enchantments = new ArrayList<>();
        NBTTagList enchantmentTags = getEnchantmentTags(stack);
        if (enchantmentTags == null) return enchantments;

        for (int i = 0; i < enchantmentTags.tagCount(); ++i) {
            NBTTagCompound enchTag = enchantmentTags.getCompoundTagAt(i);
            int id = enchTag.getInteger(VANILLA_TAG_ID);
            Enchantment enchantment = Enchantment.getEnchantmentByID(id);
            if (enchantment == null) {
                warnUnknownEnchantment(id);
                continue;
            }
            int level = enchTag.getShort(VANILLA_TAG_LEVEL);
            enchantments.add(new EnchantmentData(enchantment, level));
        }

        return enchantments;
    }

    public static boolean isSingleAndMatches(ItemStack first, Enchantment enchantment, short level) {
        NBTTagList enchantments = getEnchantmentTags(first);
        if (enchantments == null || enchantments.tagCount() != 1) return false;

        NBTTagCompound enchTag = enchantments.getCompoundTagAt(0);
        int id = enchTag.getInteger(VANILLA_TAG_ID);
        Enchantment ench = Enchantment.getEnchantmentByID(id);
        if (ench == null) {
            warnUnknownEnchantment(id);
            return false;
        }

        return ench == enchantment && enchTag.getShort(VANILLA_TAG_LEVEL) == level;
    }

    public static @Nullable NBTTagCompound findEnchTagInStack(Enchantment enchantment, ItemStack stack) {
        NBTTagList enchantmentTags = getEnchantmentTags(stack);
        if (enchantmentTags == null) return null;
        return findEnchTagInStoredEnchantments(enchantment, enchantmentTags);
    }

    public static @Nullable NBTTagCompound findEnchTagInStoredEnchantments(Enchantment enchantment, NBTTagList storedEnchantments) {
        for (int i = 0; i < storedEnchantments.tagCount(); ++i) {
            NBTTagCompound enchTag = storedEnchantments.getCompoundTagAt(i);
            int id = enchTag.getInteger(VANILLA_TAG_ID);
            Enchantment ench = Enchantment.getEnchantmentByID(id);
            if (ench == null) {
                warnUnknownEnchantment(id);
                continue;
            }

            if (ench == enchantment) return enchTag;
        }
        return null;
    }
}
