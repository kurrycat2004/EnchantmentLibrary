package io.github.kurrycat2004.enchlib.common;

import io.github.kurrycat2004.enchlib.EnchLibMod;
import io.github.kurrycat2004.enchlib.EnchLibObjects;
import io.github.kurrycat2004.enchlib.config.settings.ServerSettings;
import io.github.kurrycat2004.enchlib.tile.TileEnchantmentLibrary;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listens for player item pickups and, when enabled in config and an Enchantment Library
 * item is present in the player's inventory, diverts enchanted books directly into a
 * temporary TileEnchantmentLibrary reconstructed from the item's NBT. This mirrors the
 * insertion logic used by the placed block so behaviour stays consistent.
 */
@Mod.EventBusSubscriber(modid = io.github.kurrycat2004.enchlib.Tags.MODID)
public final class AutoPickupHandler {
    private static final Map<UUID, Integer> CACHED_LIB_SLOT = new HashMap<>();

    private AutoPickupHandler() {}

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        if (!ServerSettings.INSTANCE.autoPickupEnchantedBooks) return;

        EntityPlayer player = event.getEntityPlayer();
        if (player == null) return;

        World world = player.world;
        if (world == null || world.isRemote) return;

        EntityItem entityItem = event.getItem();
        if (entityItem == null) return;

        ItemStack picked = entityItem.getItem();
        if (picked == null || picked.isEmpty() || !(picked.getItem() instanceof ItemEnchantedBook)) return;

        int slot = getLibrarySlotCached(player);
        if (slot < 0) return;

        ItemStack libraryStack = player.inventory.getStackInSlot(slot);
        if (libraryStack == null || libraryStack.isEmpty() || !isLibraryItem(libraryStack)) {
            invalidateCache(player);

            slot = getLibrarySlotCached(player);
            if (slot < 0) return;

            libraryStack = player.inventory.getStackInSlot(slot);
            if (libraryStack == null || libraryStack.isEmpty() || !isLibraryItem(libraryStack)) return;
        }

        int accepted = addToLibraryItem(libraryStack, picked, world);
        if (accepted <= 0) return;

        if (accepted >= picked.getCount()) {
            entityItem.setDead();
            event.setCanceled(true);
        } else {
            picked.shrink(accepted);
            entityItem.setItem(picked);
        }
    }

    private static boolean isLibraryItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        Item libraryItem = Item.getItemFromBlock(EnchLibObjects.ENCHANTMENT_LIBRARY);
        return libraryItem != null && stack.getItem() == libraryItem;
    }

    private static int getLibrarySlotCached(EntityPlayer player) {
        UUID id = player.getUniqueID();
        Integer cached = CACHED_LIB_SLOT.get(id);

        if (cached != null && cached >= 0) {
            ItemStack stack = player.inventory.getStackInSlot(cached);
            if (isLibraryItem(stack)) return cached;
        }

        int slot = findLibrarySlot(player.inventory);
        CACHED_LIB_SLOT.put(id, slot);

        return slot;
    }

    private static void invalidateCache(EntityPlayer player) {
        CACHED_LIB_SLOT.remove(player.getUniqueID());
    }

    private static int findLibrarySlot(InventoryPlayer inv) {
        if (inv == null) return -1;

        Item libraryItem = Item.getItemFromBlock(EnchLibObjects.ENCHANTMENT_LIBRARY);
        if (libraryItem == null) return -1;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack s = inv.getStackInSlot(i);
            if (s != null && !s.isEmpty() && s.getItem() == libraryItem) return i;
        }

        return -1;
    }

    /**
     * Inserts as many enchanted books from {@code bookStack} into the library represented by
     * {@code libraryStack} as possible, by reconstructing a TileEnchantmentLibrary from its NBT.
     *
     * @param libraryStack The carried library item stack (ItemBlock of ENCHANTMENT_LIBRARY).
     * @param bookStack The enchanted book stack that was picked up.
     * @param world A server world reference used only for creating a temporary tile if needed.
     * @return Number of books that were accepted from {@code bookStack}.
     */
    private static int addToLibraryItem(ItemStack libraryStack, ItemStack bookStack, World world) {
        if (libraryStack == null || libraryStack.isEmpty() || bookStack == null || bookStack.isEmpty()) return 0;

        // reconstruct temporary tile entity from NBT
        TileEnchantmentLibrary tile = new TileEnchantmentLibrary();

        NBTTagCompound root = libraryStack.getTagCompound();
        NBTTagCompound beTag;
        EnchLibMod.LOGGER.info("NBT Data: " + root);
        if (root != null && root.hasKey("data")) {
            beTag = root.getCompoundTag("data");
        } else {
            beTag = new NBTTagCompound();
        }

        tile.readFromNBT(beTag);

        // attempt to insert the book(s)
        int before = bookStack.getCount();
        ItemStack remaining = tile.data.insertItem(0, bookStack, false);
        int after = remaining == null || remaining.isEmpty() ? 0 : remaining.getCount();
        int accepted = before - after;

        // write back updated tile NBT to the library item stack if any books were accepted
        if (accepted > 0) {
            NBTTagCompound newRoot = root != null ? root.copy() : new NBTTagCompound();

            NBTTagCompound newBeTag = tile.writeToNBT(new NBTTagCompound());
            newRoot.setTag("data", newBeTag);
            libraryStack.setTagCompound(newRoot);
        }

        return accepted;
    }
}
