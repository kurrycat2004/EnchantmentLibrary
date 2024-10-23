package io.github.kurrycat2004.enchlib.net.packet;

import io.github.kurrycat2004.enchlib.EnchLibMod;
import io.github.kurrycat2004.enchlib.net.PacketHandler;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

@NonnullByDefault
public abstract class MessageHandler<REQ extends IMessage, REPLY extends IMessage> implements IMessageHandler<REQ, REPLY> {
    @Override
    public @Nullable REPLY onMessage(REQ message, MessageContext ctx) {
        switch (ctx.side) {
            case CLIENT -> Minecraft.getMinecraft().addScheduledTask(() -> {
                IMessage response = this.onCMessage(message, ctx.getClientHandler());
                if (response != null) PacketHandler.INSTANCE.sendToServer(response);
            });
            case SERVER -> FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                IMessage response = this.onSMessage(message, ctx.getServerHandler());
                if (response != null) PacketHandler.INSTANCE.sendTo(response, ctx.getServerHandler().player);
            });
        }
        return null;
    }

    /**
     * Called when the message is received on the client side. <br>
     * This will ONLY be called on the client side. <br>
     * It is guaranteed to be called on the client thread. <br>
     * <br>
     * ONLY SEND THIS PACKET TO THE CLIENT IF YOU ACTUALLY IMPLEMENTED THIS METHOD
     *
     * @param msg           Message received
     * @param clientHandler Client handler
     * @return Response message
     */
    public @Nullable REPLY onCMessage(REQ msg, NetHandlerPlayClient clientHandler) {
        EnchLibMod.LOGGER.warn("Received {} on client side. This should never happen and will be ignored.", msg.getClass());
        return null;
    }

    /**
     * Called when the message is received on the server side. <br>
     * This will ONLY be called on the server side. <br>
     * It is guaranteed to be called on the server thread. <br>
     * <br>
     * ONLY SEND THIS PACKET TO THE SERVER IF YOU ACTUALLY IMPLEMENTED THIS METHOD
     *
     * @param msg           Message received
     * @param serverHandler Server handler
     * @return Response message
     */
    public @Nullable REPLY onSMessage(REQ msg, NetHandlerPlayServer serverHandler) {
        EnchLibMod.LOGGER.warn("Received {} on server side. This should never happen and will be ignored.", msg.getClass());
        return null;
    }
}
