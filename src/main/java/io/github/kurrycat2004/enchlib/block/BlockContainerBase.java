package io.github.kurrycat2004.enchlib.block;

import io.github.kurrycat2004.enchlib.Tags;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import io.github.kurrycat2004.enchlib.util.interfaces.INBTDe;
import io.github.kurrycat2004.enchlib.util.interfaces.INBTSer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IWorldNameable;
import org.jetbrains.annotations.Nullable;

@NonnullByDefault
public abstract class BlockContainerBase extends BlockContainer {
    public static final String TAG_DATA = "data";

    protected BlockContainerBase(Material materialIn) {
        super(materialIn);
    }

    protected BlockContainerBase(Material materialIn, MapColor color) {
        super(materialIn, color);
    }

    protected void storeTEInItemStack(ItemStack stack, @Nullable TileEntity tile) {
        if (tile instanceof INBTSer ser) {
            NBTTagCompound data = new NBTTagCompound();
            ser.writeNBT(data);
            if (!data.isEmpty()) stack.setTagInfo(TAG_DATA, data);
        }
        if (tile instanceof IWorldNameable nameable && nameable.hasCustomName()) {
            stack.setStackDisplayName(nameable.getName());
        }
    }

    protected void loadTEFromItemStack(ItemStack stack, @Nullable TileEntity tile) {
        if (tile instanceof INameable nameable && stack.hasDisplayName()) {
            nameable.setCustomName(stack.getDisplayName());
        }
        if (tile instanceof INBTDe de) {
            NBTTagCompound data = stack.getSubCompound(TAG_DATA);
            if (data == null) return;
            de.readNBT(data);
        }
    }

    @Override
    public Block setTranslationKey(String key) {
        return super.setTranslationKey(Tags.MODID + "." + key);
    }
}
