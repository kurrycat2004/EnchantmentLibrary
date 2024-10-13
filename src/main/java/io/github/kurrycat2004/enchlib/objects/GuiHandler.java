package io.github.kurrycat2004.enchlib.objects;

import io.github.kurrycat2004.enchlib.EnchLibMod;
import io.github.kurrycat2004.enchlib.objects.container.ContainerEnchantmentLibrary;
import io.github.kurrycat2004.enchlib.objects.gui.GuiEnchantmentLibrary;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.jetbrains.annotations.Nullable;

@NonnullByDefault
public class GuiHandler implements IGuiHandler {
    public static void init() {
        EnchLibMod.LOGGER.info("Initializing GuiHandler...");
        NetworkRegistry.INSTANCE.registerGuiHandler(EnchLibMod.INSTANCE, new GuiHandler());
    }

    public enum GuiTypes {
        ENCHANTMENT_LIBRARY
    }

    @Override
    public @Nullable Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if (tile == null) return null;

        return switch (GuiTypes.values()[id]) {
            case ENCHANTMENT_LIBRARY -> new ContainerEnchantmentLibrary(player.inventory, tile);
        };
    }

    @Override
    public @Nullable Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if (tile == null) return null;

        return switch (GuiTypes.values()[id]) {
            case ENCHANTMENT_LIBRARY -> new GuiEnchantmentLibrary(player.inventory, tile);
        };
    }
}
