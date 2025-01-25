package io.github.kurrycat2004.enchlib.mixin.ae2uel;

import io.github.kurrycat2004.enchlib.util.NBTHashUtil;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@NonnullByDefault
@Mixin(targets = "appeng.util.item.ItemStackHashStrategy$ItemStackHashStrategyBuilder$1", remap = false)
public class ItemHashStrategyMixin {
    @ModifyArg(method = "hashCode(Lnet/minecraft/item/ItemStack;)I", at = @At(value = "INVOKE", target = "Ljava/util/Objects;hash([Ljava/lang/Object;)I"), index = 0, remap = false)
    public Object[] enchlib$tagCompoundHash(Object[] objects) {
        // we can simply change the NBTTagCompound to an int by hashing it here,
        // because the Integer.hashCode() method that will be called in Objects.hash() is a NOOP
        if (objects[2] != null) objects[2] = NBTHashUtil.getFNVHash((NBTTagCompound) objects[2]);
        return objects;
    }
}
