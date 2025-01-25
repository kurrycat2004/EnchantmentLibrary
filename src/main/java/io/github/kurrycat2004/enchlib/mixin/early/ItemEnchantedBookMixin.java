package io.github.kurrycat2004.enchlib.mixin.early;

import io.github.kurrycat2004.enchlib.gui.components.EnchTooltip;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@NonnullByDefault
@Mixin(value = ItemEnchantedBook.class)
public class ItemEnchantedBookMixin {
    @Inject(method = "addInformation", at = @At("TAIL"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemEnchantedBook;getEnchantments(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/nbt/NBTTagList;")))
    private void enchlib$addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn, CallbackInfo ci) {
        if (EnchTooltip.getEnchantToAdd() != null && !EnchTooltip.hasEnchant()) tooltip.add(EnchTooltip.getAddLine());
    }
}
