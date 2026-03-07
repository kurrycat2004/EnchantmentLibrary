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

    public enum EnchLevelTranslation {
        NUMBERS,
        ROMAN_NUMERALS,
        VANILLA
    }

    @Config.RequiresMcRestart
    @Config.LangKey(Tags.MODID + ".config.client_settings.ench_level_translation_enabled")
    @Config.Comment("Disabling this completely disables enchantment level translation. Only set this to false when the enchantment level translation mixin causes problems with other mods.")
    public boolean enchLevelTranslationEnabled = true;

    @Config.LangKey(Tags.MODID + ".config.client_settings.ench_level_translation")
    @Config.Comment("Overwrites the enchantment level translation to fully support roman numerals (up to 3999) or use simple numbers. Can be NUMBERS, ROMAN_NUMERALS or VANILLA. Set to VANILLA to get vanilla behaviour / allow other mods to overwrite it.")
    public EnchLevelTranslation enchLevelTranslation = EnchLevelTranslation.ROMAN_NUMERALS;

    @Config.LangKey(Tags.MODID + ".config.client_settings.show_tips")
    @Config.Comment("Shows tips on how to use the enchantment library in various tooltips in the GUI")
    public boolean show_tips = true;

    @Config.LangKey(Tags.MODID + ".config.client_settings.modified_conway_wechsler")
    @Config.Comment("Use the modified Conway-Wechsler system for showing enchantment Points")
    public boolean modified_conway_wechsler = false;

    static {
        ConfigManager.register(Side.CLIENT);
    }
}
