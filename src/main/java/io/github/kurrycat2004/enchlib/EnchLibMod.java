package io.github.kurrycat2004.enchlib;

import io.github.kurrycat2004.enchlib.config.settings.ServerSettings;
import io.github.kurrycat2004.enchlib.gui.GuiHandler;
import io.github.kurrycat2004.enchlib.net.PacketHandler;
import io.github.kurrycat2004.enchlib.proxy.IProxy;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = Tags.MODID,
        version = Tags.VERSION,
        name = Tags.MODNAME,
        acceptedMinecraftVersions = "[1.12.2]",
        guiFactory = Tags.MODGROUP + ".config.GuiFactory"
)
public class EnchLibMod {
    public static final Logger LOGGER = LogManager.getLogger(Tags.MODID);

    @Mod.Instance
    public static EnchLibMod INSTANCE;

    @SidedProxy(clientSide = Tags.MODGROUP + ".proxy.ClientProxy", serverSide = Tags.MODGROUP + ".proxy.ServerProxy")
    public static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (ServerSettings.INSTANCE.enchantedBookStackSize != 0) {
            Items.ENCHANTED_BOOK.setMaxStackSize(ServerSettings.INSTANCE.enchantedBookStackSize);
        }

        PacketHandler.init();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GuiHandler.init();
    }
}
