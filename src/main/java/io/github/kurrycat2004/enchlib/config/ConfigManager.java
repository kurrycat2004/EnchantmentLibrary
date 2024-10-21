package io.github.kurrycat2004.enchlib.config;

import io.github.kurrycat2004.enchlib.EnchLibMod;
import io.github.kurrycat2004.enchlib.Tags;
import io.github.kurrycat2004.enchlib.config.settings.ClientSettings;
import io.github.kurrycat2004.enchlib.config.settings.ServerSettings;
import io.github.kurrycat2004.enchlib.util.MethodLookupUtil;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.client.resources.I18n;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Config manager using forge Config annotations, while also allowing top level instance configs. <br>
 * Configs are split into Client and Server side. <br>
 * Implementation is greatly inspired by <a href="https://github.com/CleanroomMC/ConfigAnytime">ConfigAnytime</a>
 */
@NonnullByDefault
public class ConfigManager {
    public static Configuration cfg;
    public static File configFile;

    private static final MethodHandle CONFIGMANAGER$SYNC;

    static {
        try {
            Class.forName("net.minecraftforge.common.config.ConfigManager", true, Launch.classLoader);
            MethodHandles.Lookup lookup = MethodLookupUtil.lookup(net.minecraftforge.common.config.ConfigManager.class);
            CONFIGMANAGER$SYNC = lookup.findStatic(net.minecraftforge.common.config.ConfigManager.class, "sync", MethodType.methodType(void.class, Configuration.class, Class.class, String.class, String.class, boolean.class, Object.class));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void register() {
        if (cfg != null) return;
        init();
    }

    public static void init() {
        configFile = new File(getConfigDir(), Tags.MODID + ".cfg");
        cfg = new Configuration(configFile);
        cfg.load();

        sync(true);
    }

    public static void save() {
        cfg.save();
    }

    private static File getConfigDir() {
        File configDir = new File(Launch.minecraftHome, "config");
        try {
            configDir = configDir.getCanonicalFile();
        } catch (IOException e) {
            EnchLibMod.LOGGER.fatal("Failed to resolve config directory", e);
            throw new RuntimeException(e);
        }
        if (!configDir.exists() && !configDir.mkdirs()) {
            EnchLibMod.LOGGER.fatal("Failed to create missing config directory: {}", configDir);
            throw new RuntimeException("Failed to create missing config directory: " + configDir);
        }
        return configDir;
    }

    public static List<IConfigElement> getConfigElements(String name, String langKey) {
        ConfigCategory category = cfg.getCategory("general");
        DummyConfigElement.DummyCategoryElement element = new DummyConfigElement.DummyCategoryElement(name, langKey, new ConfigElement(category).getChildElements());
        element.setRequiresMcRestart(category.requiresMcRestart());
        element.setRequiresWorldRestart(category.requiresWorldRestart());

        List<IConfigElement> elements = element.getChildElements();
        elements.sort(Comparator.comparing(e -> I18n.format(e.getLanguageKey())));
        return elements;
    }

    public static void sync() {
        sync(false);
    }

    private static void sync(boolean init) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            ConfigManager.sync(
                    ClientSettings.INSTANCE, "client_settings",
                    "These options are client-side only",
                    Tags.MODID + ".config.client_settings",
                    false, false,
                    init
            );
        }
        ConfigManager.sync(
                ServerSettings.INSTANCE, "server_settings",
                "These options require a server restart on dedicated servers",
                Tags.MODID + ".config.server_settings",
                false, false,
                init
        );

        ConfigManager.save();
    }

    public static <T> void sync(T configInstance, String category, String comment, String langKey, boolean requiresMcRestart, boolean requiresWorldRestart, boolean init) {
        String sub = "general" + Configuration.CATEGORY_SPLITTER + category.toLowerCase(Locale.ENGLISH);
        ConfigCategory confCat = cfg.getCategory(sub);
        confCat.setComment(comment);
        confCat.setLanguageKey(langKey);
        confCat.setRequiresMcRestart(requiresMcRestart);
        confCat.setRequiresWorldRestart(requiresWorldRestart);

        sync(cfg, configInstance.getClass(), Tags.MODID, sub, init, configInstance);
    }

    @SuppressWarnings("SameParameterValue")
    private static void sync(Configuration cfg, Class<?> cls, String modid, String category, boolean loading, Object instance) {
        try {
            CONFIGMANAGER$SYNC.invokeExact(cfg, cls, modid, category, loading, instance);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
