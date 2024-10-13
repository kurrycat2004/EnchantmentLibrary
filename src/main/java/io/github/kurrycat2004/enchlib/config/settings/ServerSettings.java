package io.github.kurrycat2004.enchlib.config.settings;

import io.github.kurrycat2004.enchlib.Tags;
import net.minecraftforge.common.config.Config;

public class ServerSettings {
    @Config.Ignore
    public static final ServerSettings INSTANCE = new ServerSettings();

    @Config.LangKey(Tags.MODID + ".config.server_settings.allow_enchant_merging")
    @Config.Comment("Allow merging different enchantments in the enchantment library")
    public boolean allowEnchantMerging = true;
}
