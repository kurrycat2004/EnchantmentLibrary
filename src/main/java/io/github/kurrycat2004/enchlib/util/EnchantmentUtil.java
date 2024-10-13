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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NonnullByDefault
public class EnchantmentUtil {
    public static final String VANILLA_TAG_ENCHANTMENTS = "StoredEnchantments";
    public static final String VANILLA_TAG_ID = "id";
    public static final String VANILLA_TAG_LEVEL = "lvl";

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

    public static List<EnchantmentData> getEnchantments(ItemStack stack) {
        NBTTagList nbttaglist = ItemEnchantedBook.getEnchantments(stack);
        List<EnchantmentData> enchantments = new ArrayList<>();

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int id = nbttagcompound.getShort(VANILLA_TAG_ID);
            Enchantment enchantment = Enchantment.getEnchantmentByID(id);
            if (enchantment == null) {
                warnUnknownEnchantment(id);
                continue;
            }
            int level = nbttagcompound.getShort(VANILLA_TAG_LEVEL);
            enchantments.add(new EnchantmentData(enchantment, level));
        }

        return enchantments;
    }

    public static boolean isSingleAndMatches(ItemStack first, Enchantment enchantment, short level) {
        NBTTagList enchantments = ItemEnchantedBook.getEnchantments(first);
        if (enchantments.tagCount() != 1) return false;
        NBTTagCompound nbttagcompound = enchantments.getCompoundTagAt(0);
        int id = nbttagcompound.getShort(VANILLA_TAG_ID);
        Enchantment ench = Enchantment.getEnchantmentByID(id);
        if (ench == null) {
            warnUnknownEnchantment(id);
            return false;
        }
        if (ench != enchantment) return false;
        int lvl = nbttagcompound.getShort(VANILLA_TAG_LEVEL);
        return lvl == level;
    }

    public static @Nullable NBTTagCompound findEnchTagInStoredEnchantments(Enchantment enchantment, NBTTagList storedEnchantments) {
        NBTTagCompound found = null;
        for (int i = 0; i < storedEnchantments.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = storedEnchantments.getCompoundTagAt(i);
            int id = nbttagcompound.getShort(EnchantmentUtil.VANILLA_TAG_ID);
            Enchantment ench = Enchantment.getEnchantmentByID(id);
            if (ench == null) {
                EnchantmentUtil.warnUnknownEnchantment(id);
                continue;
            }
            if (ench == enchantment) {
                found = nbttagcompound;
                break;
            }
        }
        return found;
    }
}
