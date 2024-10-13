package io.github.kurrycat2004.enchlib.util;

import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

@NonnullByDefault
public class ItemUtil {
    public static boolean isEmpty(@Nullable ItemStack itemStack) {
        return itemStack == null || itemStack.isEmpty();
    }
}
