package io.github.kurrycat2004.enchlib.tile;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import io.github.kurrycat2004.enchlib.EnchLibMod;
import io.github.kurrycat2004.enchlib.block.INameable;
import io.github.kurrycat2004.enchlib.common.EnchLibData;
import io.github.kurrycat2004.enchlib.gui.GuiEnchantmentLibrary;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import io.github.kurrycat2004.enchlib.util.interfaces.INBTSerDe;
import io.github.kurrycat2004.enchlib.util.interfaces.ISavable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NonnullByDefault
public class TileEnchantmentLibrary extends TileEntity implements INBTSerDe, INameable, ISavable {
    public final EnchLibData data;
    private String customName = null;

    public TileEnchantmentLibrary() {
        this.data = new EnchLibData(this);
    }

    @Override
    public void markForSave() {
        markDirty();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.world != null && !this.world.isRemote && this.world instanceof WorldServer worldServer) {
            // will trigger onDataPacket client side
            worldServer.getPlayerChunkMap().markBlockForUpdate(this.pos);
        }
    }

    @Override
    public NBTTagCompound writeNBT(NBTTagCompound nbt) {
        return data.writeNBT(nbt);
    }

    @Override
    public boolean readNBT(NBTTagCompound nbt) {
        if (!data.readNBT(nbt)) {
            EnchLibMod.LOGGER.warn("Caused by: Error while trying to read NBT data for Enchantment Library at {}", this.pos);
            return false;
        }
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeNBT(compound);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readNBT(compound);

        if (this.world != null && this.world.isRemote) {
            this.updateGui();
        }
    }

    /**
     * Ensure the tile entity is NOT refreshed (re-created) on mere blockstate/property changes
     * like rotations; only refresh if the actual block instance changes.
     * 
     * @param world The world.
     * @param pos The block position.
     * @param oldState The old block state.
     * @param newState The new block state.
     * @return True to refresh (remove/recreate), false to keep existing tile.
     */
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @SideOnly(Side.CLIENT)
    public void updateGui() {
        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen instanceof GuiEnchantmentLibrary enchGui)) return;
        if (enchGui.getTile() != this) return;
        enchGui.updateEntries();
    }

    @Override
    public final SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, -1, this.getUpdateTag());
    }

    @Override
    public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
        if (this.world.isRemote) {
            IBlockState state = this.world.getBlockState(getPos());
            this.world.notifyBlockUpdate(this.pos, state, state, 3);
        }
    }

    @Override
    public final NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public final void handleUpdateTag(NBTTagCompound compound) {
        this.readFromNBT(compound);
    }


    @Override
    public void setCustomName(String name) {
        this.customName = name;
    }

    public boolean canPlayerUse(EntityPlayer player) {
        return !this.isInvalid() &&
               player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64 &&
               this.world.getTileEntity(this.pos) == this;
    }

    //TODO: wait for https://github.com/AE2-UEL/Applied-Energistics-2/issues/501 to be fixed
    @CapabilityInject(IItemHandler.class)
    static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;

    @CapabilityInject(IItemRepository.class)
    static Capability<IItemRepository> ITEM_REPOSITORY_CAPABILITY = null;

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == ITEM_HANDLER_CAPABILITY ||
               capability == ITEM_REPOSITORY_CAPABILITY ||
               super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == ITEM_HANDLER_CAPABILITY) return (T) data;
        if (capability == ITEM_REPOSITORY_CAPABILITY) return (T) data;
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "tile.enchlib.enchantment_library.name";
    }

    @Override
    public @NotNull ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    @Override
    public boolean canRenderBreaking() {
        return true;
    }
}

