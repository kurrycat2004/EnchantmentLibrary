package io.github.kurrycat2004.enchlib.objects.block;

import net.minecraft.world.IWorldNameable;

public interface INameable extends IWorldNameable {
    void setCustomName(String name);
}
