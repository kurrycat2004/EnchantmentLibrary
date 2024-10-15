package io.github.kurrycat2004.enchlib.proxy;

import io.github.kurrycat2004.enchlib.EnchLibObjects;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements IProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        EnchLibObjects.Client.registerTESRs();
    }
}
