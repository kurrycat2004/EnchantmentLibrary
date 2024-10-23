package io.github.kurrycat2004.enchlib.net;

import io.github.kurrycat2004.enchlib.Tags;
import io.github.kurrycat2004.enchlib.net.packet.PacketConfirmTransaction;
import io.github.kurrycat2004.enchlib.net.packet.to_server.PacketClickEnchLib;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MODID);

    public static void init() {
        int id = 0;
        INSTANCE.registerMessage(PacketConfirmTransaction.Handler.class, PacketConfirmTransaction.class, id++, Side.CLIENT);
        INSTANCE.registerMessage(PacketConfirmTransaction.Handler.class, PacketConfirmTransaction.class, id++, Side.SERVER);
        INSTANCE.registerMessage(PacketClickEnchLib.Handler.class, PacketClickEnchLib.class, id++, Side.SERVER);
    }
}
