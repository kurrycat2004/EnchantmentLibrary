package io.github.kurrycat2004.enchlib.item;

import io.github.kurrycat2004.enchlib.config.settings.ServerSettings;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;
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

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        boolean enabled = ServerSettings.INSTANCE.autoPickupEnchantedBooks;
        String stateKey = enabled ? "tooltip.enchlib.enabled" : "tooltip.enchlib.disabled";
        TextFormatting stateColor = enabled ? TextFormatting.GREEN : TextFormatting.RED;
        tooltip.add(
            TextFormatting.DARK_AQUA + I18n.format("tooltip.enchlib.autopickup") +
            stateColor + I18n.format(stateKey) +
            TextFormatting.DARK_AQUA + I18n.format(stateKey + ".suffix")
        );
    }
}
