package io.github.kurrycat2004.enchlib.objects.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class ItemBlockBase extends ItemBlock {
    public ItemBlockBase(Block block) {
        super(block);
        this.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return stack.hasTagCompound() ? 1 : super.getItemStackLimit(stack);
    }

    @Override
    public boolean isDamageable() {
        return false;
    }
}
