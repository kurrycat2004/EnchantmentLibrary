package io.github.kurrycat2004.enchlib.mixin.early;

import io.github.kurrycat2004.enchlib.gui.components.EnchTooltip;
import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Inject(method = "getTranslatedName", at = @At("HEAD"), cancellable = true)
    private void enchlib$injectGuiInfo(int level, CallbackInfoReturnable<String> cir) {
        if ((Object) this != EnchTooltip.getEnchantToAdd()) return;
        EnchTooltip.setHasEnchant(true);

        if (level >= EnchTooltip.getLevelToAdd()) return;
        cir.setReturnValue(EnchTooltip.getDiffLevelLine(level));
    }
}
