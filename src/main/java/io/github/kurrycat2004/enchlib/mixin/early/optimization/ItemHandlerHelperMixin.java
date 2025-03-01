package io.github.kurrycat2004.enchlib.mixin.early.optimization;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.kurrycat2004.enchlib.common.ISlotlessItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ItemHandlerHelper.class, remap = false)
public abstract class ItemHandlerHelperMixin {
    @ModifyExpressionValue(method = "insertItem", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/IItemHandler;getSlots()I"))
    private static int enchlib$shortCircuitSlotless(int slots, IItemHandler inv) {
        return inv instanceof ISlotlessItemHandler ? 1 : slots;
    }

    @ModifyExpressionValue(method = "insertItemStacked", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isStackable()Z", remap = true))
    private static boolean enchlib$shortCircuitSlotless(boolean stackable, IItemHandler inv) {
        return !(inv instanceof ISlotlessItemHandler) && stackable;
    }
}
