package io.github.kurrycat2004.enchlib.core;

import io.github.kurrycat2004.enchlib.config.settings.ServerSettings;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class EnchLibLate implements ILateMixinLoader {
    private static final BooleanSupplier TRUE_SUPPLIER = () -> true;
    private static final BooleanSupplier FALSE_SUPPLIER = () -> false;

    private static final Map<String, BooleanSupplier> MIXIN_CONFIGS = new HashMap<>() {
        {
            put("mixins.enchlib.ae2uel.json", () -> ServerSettings.INSTANCE.enableAE2UELHashFixMixin && Mods.AE2.isLoaded());
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
}
