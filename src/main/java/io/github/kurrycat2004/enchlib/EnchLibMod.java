package io.github.kurrycat2004.enchlib;

import io.github.kurrycat2004.enchlib.config.ConfigManager;
import io.github.kurrycat2004.enchlib.objects.GuiHandler;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = Tags.MODID,
        version = Tags.VERSION,
        name = Tags.MODNAME,
        acceptedMinecraftVersions = "[1.12.2]",
        guiFactory = "io.github.kurrycat2004.enchlib.config.GuiFactory"
)
public class EnchLibMod {
    public static final Logger LOGGER = LogManager.getLogger(Tags.MODID);

    @Mod.Instance
    public static EnchLibMod INSTANCE;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Items.ENCHANTED_BOOK.setMaxStackSize(64);
        ConfigManager.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GuiHandler.init();
    }
}
