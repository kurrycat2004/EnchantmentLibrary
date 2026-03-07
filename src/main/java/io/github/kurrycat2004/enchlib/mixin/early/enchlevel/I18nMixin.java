package io.github.kurrycat2004.enchlib.mixin.early.enchlevel;

import io.github.kurrycat2004.enchlib.common.EnchLevel;
import net.minecraft.util.text.translation.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("deprecation")
@Mixin(I18n.class)
public class I18nMixin {
    @Inject(method = "translateToLocal", at = @At("HEAD"), cancellable = true)
    private static void modifyTranslateToLocal(String key, CallbackInfoReturnable<String> cir) {
        String modified = EnchLevel.formatEnchLevel(key);
        if (modified != null) cir.setReturnValue(modified);
    }

    @Inject(method = "translateToLocalFormatted", at = @At("HEAD"), cancellable = true)
    private static void modifyTranslateToLocalFormatted(String key, Object[] format, CallbackInfoReturnable<String> cir) {
        String modified = EnchLevel.formatEnchLevel(key);
        if (modified != null) cir.setReturnValue(modified);
    }

    @Inject(method = "canTranslate", at = @At("HEAD"), cancellable = true)
    private static void modifyCanTranslate(String key, CallbackInfoReturnable<Boolean> cir) {
        if (EnchLevel.canFormatEnchLevel(key)) cir.setReturnValue(true);
    }
}
