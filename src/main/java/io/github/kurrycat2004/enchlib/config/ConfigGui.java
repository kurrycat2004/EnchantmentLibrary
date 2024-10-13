package io.github.kurrycat2004.enchlib.config;

import io.github.kurrycat2004.enchlib.Tags;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;

@NonnullByDefault
public class ConfigGui extends GuiConfig {
    public ConfigGui(GuiScreen parentScreen) {
        super(parentScreen,
                ConfigManager.getConfigElements(Tags.MODID, Tags.MODID + ".config.general"),
                Tags.MODID, null, false, false,
                GuiConfig.getAbridgedConfigPath(ConfigManager.configFile.getAbsolutePath()),
                null
        );

        if (Minecraft.getMinecraft().getIntegratedServer() == null)
            this.configElements.removeIf(iConfigElement -> iConfigElement.getName().equals("server_settings"));
    }
}