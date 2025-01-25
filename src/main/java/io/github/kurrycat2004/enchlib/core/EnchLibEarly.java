package io.github.kurrycat2004.enchlib.core;

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
    private static final BooleanSupplier TRUE_SUPPLIER = () -> true;
    private static final BooleanSupplier FALSE_SUPPLIER = () -> false;

    private static final Map<String, BooleanSupplier> MIXIN_CONFIGS = new HashMap<>() {
        {
            put("mixins.enchlib.early.tooltip.json", TRUE_SUPPLIER);
            put("mixins.enchlib.early.enchlevel.json", () -> ServerSettings.INSTANCE.enchLevelTranslation != ServerSettings.EnchLevelTranslation.VANILLA);
        }
    };

    @Override
    public List<String> getMixinConfigs() {
        return new ArrayList<>(MIXIN_CONFIGS.keySet());
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        return MIXIN_CONFIGS.getOrDefault(mixinConfig, FALSE_SUPPLIER).getAsBoolean();
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
