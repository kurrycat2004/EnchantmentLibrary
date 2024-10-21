package io.github.kurrycat2004.enchlib.core;

import io.github.kurrycat2004.enchlib.EnchLibMod;
import io.github.kurrycat2004.enchlib.config.settings.ServerSettings;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

public class EnchLibLate implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.enchlib.ae2uel.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        return ServerSettings.INSTANCE.enableAE2UELHashFixMixin && Loader.isModLoaded("appliedenergistics2");
    }

    @Override
    public void onMixinConfigQueued(String mixinConfig) {
        EnchLibMod.LOGGER.info("AE2UEL hash fix mixin queued");
    }
}
