package io.github.kurrycat2004.enchlib.config.settings;

import io.github.kurrycat2004.enchlib.Tags;
import io.github.kurrycat2004.enchlib.config.ConfigManager;
import net.minecraftforge.common.config.Config;

public class ServerSettings {
    @Config.Ignore
    public static final ServerSettings INSTANCE = new ServerSettings();

    @Config.LangKey(Tags.MODID + ".config.server_settings.allow_enchant_merging")
    @Config.Comment("Allow merging different enchantments in the enchantment library")
    public boolean allowEnchantMerging = true;

    @Config.LangKey(Tags.MODID + ".config.server_settings.allow_enchant_splitting")
    @Config.Comment("Allow splitting enchanted books with multiple enchantments when inserting into the enchantment library")
    public boolean allowEnchantSplitting = true;

    @Config.RequiresMcRestart
    @Config.LangKey(Tags.MODID + ".config.server_settings.enable_ae2uel_hash_fix_mixin")
    @Config.Comment("Attempts to fix AE2UELs hash collisions in the ItemStack cache, by replacing the NBT hashCode functions")
    public boolean enableAE2UELHashFixMixin = true;

    @Config.RequiresMcRestart
    @Config.LangKey(Tags.MODID + ".config.server_settings.enchanted_book_stack_size")
    @Config.Comment("The stack size of enchanted books. Set to 0 to not overwrite it at all. Values over 64 will not really work.")
    @Config.RangeInt(min = 0)
    public int enchantedBookStackSize = 0;

    static {
        ConfigManager.register();
    }
}
