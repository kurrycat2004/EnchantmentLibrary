package io.github.kurrycat2004.enchlib.config.settings;

import io.github.kurrycat2004.enchlib.Tags;
import io.github.kurrycat2004.enchlib.config.ConfigManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientSettings {
    @Config.Ignore
    public static final ClientSettings INSTANCE = new ClientSettings();

    @Config.LangKey(Tags.MODID + ".config.client_settings.modified_conway_wechsler")
    @Config.Comment("Use the modified Conway-Wechsler system for showing enchantment Points")
    public boolean modified_conway_wechsler = false;

    static {
        ConfigManager.register(Side.CLIENT);
    }
}
