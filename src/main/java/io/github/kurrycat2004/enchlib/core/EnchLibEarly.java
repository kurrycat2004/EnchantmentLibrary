package io.github.kurrycat2004.enchlib.core;

import io.github.kurrycat2004.enchlib.config.settings.ClientSettings;
import io.github.kurrycat2004.enchlib.config.settings.ServerSettings;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class EnchLibEarly implements IFMLLoadingPlugin, IEarlyMixinLoader {
    private static final Map<String, BooleanSupplier> MIXIN_CONFIGS = new HashMap<>() {
        {
            put("mixins.enchlib.early.enchlevel.json", () -> ClientSettings.INSTANCE.enchLevelTranslationEnabled);
            put("mixins.enchlib.early.optimization.json", () -> ServerSettings.INSTANCE.optimizeItemHandler);
        }
    };

    @Override
    public List<String> getMixinConfigs() {
        return new ArrayList<>(MIXIN_CONFIGS.keySet());
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        return MIXIN_CONFIGS.getOrDefault(mixinConfig, () -> false).getAsBoolean();
    }

    @Override
    public @Nullable String[] getASMTransformerClass() {return null;}

    @Override
    public @Nullable String getModContainerClass() {return null;}

    @Override
    public @Nullable String getSetupClass() {return null;}

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public @Nullable String getAccessTransformerClass() {return null;}
}
