package io.github.kurrycat2004.enchlib.mixin.early;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.kurrycat2004.enchlib.config.settings.ServerSettings;
import io.github.kurrycat2004.enchlib.util.RomanNumeralUtil;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@NonnullByDefault
@Mixin(Enchantment.class)
public class EnchantmentMixin_EnchLevel {
    @WrapOperation(
            method = "getTranslatedName",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/text/translation/I18n;translateToLocal(Ljava/lang/String;)Ljava/lang/String;"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
    )
    public String enchlib$getTranslatedName(String key, Operation<String> original, int level) {
        return switch (ServerSettings.INSTANCE.enchLevelTranslation) {
            case NUMBERS -> String.valueOf(level);
            case ROMAN_NUMERALS -> level < 1 || level > 3999 ? String.valueOf(level) : RomanNumeralUtil.fromInt(level);
            default -> original.call(key);
        };
    }
}
