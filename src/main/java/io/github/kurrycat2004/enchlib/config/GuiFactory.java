package io.github.kurrycat2004.enchlib.config;

import io.github.kurrycat2004.enchlib.Tags;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@NonnullByDefault
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Tags.MODID)
public class GuiFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {}

    @Override
    public boolean hasConfigGui() {return true;}

    @Override
    public @Nullable Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {return null;}

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new ConfigGui(parentScreen);
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (!event.getModID().equals(Tags.MODID)) return;
        ConfigManager.sync();
    }
}

