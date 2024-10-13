package io.github.kurrycat2004.enchlib.config;

import io.github.kurrycat2004.enchlib.Tags;
import io.github.kurrycat2004.enchlib.config.settings.ClientSettings;
import io.github.kurrycat2004.enchlib.config.settings.ServerSettings;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.client.resources.I18n;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@NonnullByDefault
public class ConfigManager {
    public static Configuration cfg;
    public static File configFile;

    // The following code is taken from the ConfigAnytime project, which is licensed under the MIT License:
    // https://github.com/CleanroomMC/ConfigAnytime/blob/765cc801b7b3da597cf8c6eb1f6594fd0adb2e71/src/main/java/com/cleanroommc/configanytime/ConfigAnytime.java#L25
    private static final MethodHandle CONFIGMANAGER$SYNC;

    static {
        try {
            Class.forName("net.minecraftforge.common.config.ConfigManager", true, Launch.classLoader); // Init first
            // Max privilege
            Field lookup$impl_lookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            lookup$impl_lookup.setAccessible(true);
            MethodHandles.Lookup lookup = ((MethodHandles.Lookup) lookup$impl_lookup.get(null)).in(net.minecraftforge.common.config.ConfigManager.class);
            CONFIGMANAGER$SYNC = lookup.findStatic(net.minecraftforge.common.config.ConfigManager.class, "sync", MethodType.methodType(void.class, Configuration.class, Class.class, String.class, String.class, boolean.class, Object.class));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
    // End of ConfigAnytime code

    public static void preInit(FMLPreInitializationEvent event) {
        configFile = new File(event.getModConfigurationDirectory(), Tags.MODID + ".cfg");
        cfg = new Configuration(configFile);
        cfg.load();

        sync(true);
    }

    public static void save() {
        cfg.save();
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
