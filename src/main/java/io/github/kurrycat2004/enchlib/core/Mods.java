package io.github.kurrycat2004.enchlib.core;

import net.minecraftforge.fml.common.Loader;

public enum Mods {
    AE2("appliedenergistics2");

    private final String modId;
    private Boolean isLoaded = null;

    Mods(String modId) {
        this.modId = modId;
    }

    public String modId() {
        return modId;
    }

    public boolean isLoaded() {
        if (isLoaded == null) isLoaded = Loader.isModLoaded(modId);
        return isLoaded;
    }
}
