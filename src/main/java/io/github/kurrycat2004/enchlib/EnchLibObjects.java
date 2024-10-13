package io.github.kurrycat2004.enchlib;

import io.github.kurrycat2004.enchlib.objects.block.BlockEnchantmentLibrary;
import io.github.kurrycat2004.enchlib.objects.item.ItemBlockBase;
import io.github.kurrycat2004.enchlib.objects.tile.TileEnchantmentLibrary;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;

@SuppressWarnings("DataFlowIssue")
@NonnullByDefault
@Mod.EventBusSubscriber(modid = Tags.MODID)
@GameRegistry.ObjectHolder(Tags.MODID)
public class EnchLibObjects {
    @GameRegistry.ObjectHolder(BlockEnchantmentLibrary.REGISTRY_NAME)
    public static final BlockEnchantmentLibrary ENCHANTMENT_LIBRARY = null;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(new BlockEnchantmentLibrary());
        GameRegistry.registerTileEntity(TileEnchantmentLibrary.class, BlockEnchantmentLibrary.RESOURCE_LOCATION);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new ItemBlockBase(ENCHANTMENT_LIBRARY));
    }

    @NonnullByDefault
    @Mod.EventBusSubscriber(modid = Tags.MODID, value = Side.CLIENT)
    public static class Client {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent evt) {
            registerCustomModel(new ItemStack(ENCHANTMENT_LIBRARY), ENCHANTMENT_LIBRARY.getRegistryName(), "inventory");
        }

        static void registerCustomModel(ItemStack stack, ResourceLocation location, String variant) {
            ModelLoader.setCustomModelResourceLocation(stack.getItem(), stack.getMetadata(), new ModelResourceLocation(location, variant));
        }
    }
}
