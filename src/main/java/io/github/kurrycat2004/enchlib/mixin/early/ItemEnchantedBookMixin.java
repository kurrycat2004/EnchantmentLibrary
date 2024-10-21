package io.github.kurrycat2004.enchlib.mixin.early;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import io.github.kurrycat2004.enchlib.gui.components.EnchTooltip;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@NonnullByDefault
@Mixin(value = ItemEnchantedBook.class)
public class ItemEnchantedBookMixin {
    @Inject(method = "addInformation", at = @At("TAIL"))
    private void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn, CallbackInfo ci, @Share("hasEnchant") LocalBooleanRef hasEnchant) {
        if (EnchTooltip.getEnchantToAdd() != null && !hasEnchant.get()) tooltip.add(EnchTooltip.getAddLine());
    }

    @WrapOperation(method = "addInformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getTranslatedName(I)Ljava/lang/String;"))
    private String addInformationWrap(Enchantment instance, int level, Operation<String> original, @Share("hasEnchant") LocalBooleanRef hasEnchant) {
        String originalLine = original.call(instance, level);
        if (instance != EnchTooltip.getEnchantToAdd()) return originalLine;
        hasEnchant.set(true);

        if (level >= EnchTooltip.getLevelToAdd()) return originalLine;
        return EnchTooltip.getDiffLevelLine(level);
    }
}
