package io.github.kurrycat2004.enchlib.net.packet;

import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

@NonnullByDefault
public class PacketConfirmTransaction implements IMessage {
    private static final Int2ShortMap pendingTransactions = new Int2ShortOpenHashMap();

    static {
        pendingTransactions.defaultReturnValue(Short.MIN_VALUE);
    }

    @SideOnly(Side.SERVER)
    public static void addPending(int windowId, short uid) {
        pendingTransactions.put(windowId, uid);
    }

    private int windowId;
    private short actionUid;
    private boolean accepted;

    public PacketConfirmTransaction() {}

    public PacketConfirmTransaction(int windowId, short actionUid, boolean accepted) {
        this.windowId = windowId;
        this.actionUid = actionUid;
        this.accepted = accepted;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(windowId);
        buf.writeShort(actionUid);
        buf.writeBoolean(accepted);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        windowId = buf.readInt();
        actionUid = buf.readShort();
        accepted = buf.readBoolean();
    }

    public static class Handler extends MessageHandler<PacketConfirmTransaction, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public @Nullable IMessage onCMessage(PacketConfirmTransaction msg, NetHandlerPlayClient clientHandler) {
            Container container = null;
            EntityPlayer entityplayer = Minecraft.getMinecraft().player;

            if (msg.windowId == 0) {
                container = entityplayer.inventoryContainer;
            } else if (msg.windowId == entityplayer.openContainer.windowId) {
                container = entityplayer.openContainer;
            }

            if (container != null && !msg.accepted) {
                return new PacketConfirmTransaction(msg.windowId, msg.actionUid, true);
            }
            return null;
        }

        @Override
        @SideOnly(Side.SERVER)
        public @Nullable IMessage onSMessage(PacketConfirmTransaction msg, NetHandlerPlayServer serverHandler) {
            EntityPlayer player = serverHandler.player;
            short actionUid = pendingTransactions.get(player.openContainer.windowId);
            // action already handled
            if (actionUid == Short.MIN_VALUE) return null;

            if (msg.accepted &&
                actionUid == msg.actionUid &&
                player.openContainer.windowId == msg.windowId &&
                !player.openContainer.getCanCraft(player) &&
                !player.isSpectator()
            ) {
                player.openContainer.setCanCraft(player, true);
            }
            pendingTransactions.remove(player.openContainer.windowId);
            return null;
        }
    }
}
