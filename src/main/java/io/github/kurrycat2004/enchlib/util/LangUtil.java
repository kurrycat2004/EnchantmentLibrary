package io.github.kurrycat2004.enchlib.util;

import io.github.kurrycat2004.enchlib.Tags;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@NonnullByDefault
public class LangUtil {
    @SideOnly(Side.CLIENT)
    public static String localize(String text, Object... params) {
        return I18n.format(text, params);
    }

    @SideOnly(Side.CLIENT)
    public static String guiInfo(String key, Object... params) {
        return localize("info." + Tags.MODID + ".gui." + key, params);
    }
}
