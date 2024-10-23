package io.github.kurrycat2004.enchlib.net.packet.to_server;

import io.github.kurrycat2004.enchlib.container.ContainerEnchantmentLibrary;
import io.github.kurrycat2004.enchlib.net.packet.MessageHandler;
import io.github.kurrycat2004.enchlib.net.packet.PacketConfirmTransaction;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

@NonnullByDefault
public class PacketClickEnchLib implements IMessage {
    private short actionUid;
    private int enchantmentId;
    private short enchantmentLevel;
    private byte mouseButton;
    private ClickType clickType;
    private ItemStack clientItemStack;

    public PacketClickEnchLib() {}

    public PacketClickEnchLib(short actionUid, int enchantmentId, short enchantmentLevel, byte mouseButton, ClickType clickType, ItemStack clientItemStack) {
        this.actionUid = actionUid;
        this.enchantmentId = enchantmentId;
        this.enchantmentLevel = enchantmentLevel;
        this.mouseButton = mouseButton;
        this.clickType = clickType;
        this.clientItemStack = clientItemStack;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeShort(actionUid);
        buf.writeInt(enchantmentId);
        buf.writeShort(enchantmentLevel);
        buf.writeByte(mouseButton);
        buf.writeByte(clickType.ordinal());
        ByteBufUtils.writeItemStack(buf, clientItemStack);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        actionUid = buf.readShort();
        enchantmentId = buf.readInt();
        enchantmentLevel = buf.readShort();
        mouseButton = buf.readByte();
        clickType = ClickType.values()[buf.readByte() % ClickType.values().length];
        clientItemStack = ByteBufUtils.readItemStack(buf);
    }

    public static class Handler extends MessageHandler<PacketClickEnchLib, IMessage> {
        @Override
        @SideOnly(Side.SERVER)
        public @Nullable IMessage onSMessage(PacketClickEnchLib msg, NetHandlerPlayServer serverHandler) {
            if (serverHandler.player == null) return null;
            EntityPlayerMP player = serverHandler.player;

            if (!(player.openContainer instanceof ContainerEnchantmentLibrary enchLib)) return null;
            if (!enchLib.getCanCraft(player)) return null;

            ItemStack serverItemStack = enchLib.enchLibClick(
                    msg.enchantmentId,
                    msg.enchantmentLevel,
                    msg.mouseButton,
                    msg.clickType,
                    player
            );

            if (ItemStack.areItemStacksEqualUsingNBTShareTag(msg.clientItemStack, serverItemStack)) {
                return new PacketConfirmTransaction(enchLib.windowId, msg.actionUid, true);
            }

            PacketConfirmTransaction.addPending(player.openContainer.windowId, msg.actionUid);
            player.openContainer.setCanCraft(player, false);

            NonNullList<ItemStack> itemsList = NonNullList.create();

            for (int j = 0; j < player.openContainer.inventorySlots.size(); ++j) {
                ItemStack itemstack = player.openContainer.inventorySlots.get(j).getStack();
                itemsList.add(itemstack.isEmpty() ? ItemStack.EMPTY : itemstack);
            }

            player.sendAllContents(player.openContainer, itemsList);
            return new PacketConfirmTransaction(enchLib.windowId, msg.actionUid, false);
        }
    }
}