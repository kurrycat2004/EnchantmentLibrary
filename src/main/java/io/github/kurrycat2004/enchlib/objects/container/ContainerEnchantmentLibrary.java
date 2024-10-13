package io.github.kurrycat2004.enchlib.objects.container;

import io.github.kurrycat2004.enchlib.config.settings.ServerSettings;
import io.github.kurrycat2004.enchlib.objects.tile.TileEnchantmentLibrary;
import io.github.kurrycat2004.enchlib.util.EnchantmentUtil;
import io.github.kurrycat2004.enchlib.util.MouseUtil;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

@NonnullByDefault
public class ContainerEnchantmentLibrary extends ContainerBase {
    public static final int PLAYER_INV_Y_OFFSET = 106;

    private final TileEnchantmentLibrary tile;

    public ContainerEnchantmentLibrary(InventoryPlayer inv, TileEntity tile) {
        this.tile = (TileEnchantmentLibrary) tile;
        addPlayerInvSlots(inv, PLAYER_INV_Y_OFFSET);
    }

    // This will break horribly if there are more than 2^16 - inventorySlots.size() enchantments stored in the library
    // But if that happens, you probably have bigger problems
    public int levelAndEnchantmentIdToSlotId(short level, int enchantmentId) {
        return level << Short.SIZE | ((enchantmentId + inventorySlots.size()) & 0x0000FFFF);
    }

    /// The return value here is only used for comparing the client and server result <br>
    /// The actual item stack is not important, only that it is the same on both sides if, and only if, the action performed was the same
    @Override
    public ItemStack slotClick(int slotId, int mouseButton, ClickType clickType, EntityPlayer player) {
        if (slotId >= inventorySlots.size()) {
            int enchantmentId = (slotId - inventorySlots.size()) & 0x0000FFFF;
            short level = (short) ((slotId - inventorySlots.size()) >>> Short.SIZE);
            return customSlotClick(enchantmentId, level, mouseButton, clickType, player);
        }
        return super.slotClick(slotId, mouseButton, clickType, player);
    }

    private ItemStack customSlotClick(int enchantmentId, short level, int mouseButton, ClickType clickType, EntityPlayer player) {
        ItemStack held = player.inventory.getItemStack();
        if (!held.isEmpty() && !tile.data.isItemValid(held)) return held;

        if (mouseButton == MouseUtil.LEFT) {
            switch (clickType) {
                case PICKUP -> {
                    Enchantment enchantment = tile.data.getEnchantment(enchantmentId);
                    if (enchantment == null) return held;

                    if (held.isEmpty()) {
                        held = tile.data.extractEnchantedBook(enchantmentId, 1, level, false);
                    } else if (EnchantmentUtil.isSingleAndMatches(held, enchantment, level) && held.getCount() < held.getMaxStackSize()) {
                        int toRemove = held.getMaxStackSize() - held.getCount();

                        int remaining = tile.data.removeN(enchantmentId, toRemove, level);
                        if (toRemove != remaining) {
                            held = held.copy();
                            held.setCount(held.getCount() + toRemove - remaining);
                        }
                    } else if (ServerSettings.INSTANCE.allowEnchantMerging) {
                        held = tile.data.extractEnchantmentOntoBook(held, enchantmentId, level);
                    }
                    player.inventory.setItemStack(held);
                    return held;
                }
                case QUICK_MOVE -> {
                    // amount is capped to stack size in extractEnchantedBook
                    ItemStack extracted = tile.data.extractEnchantedBook(enchantmentId, Integer.MAX_VALUE, level, true);
                    if (extracted.isEmpty()) return ItemStack.EMPTY;
                    int toRemove = extracted.getCount();
                    this.mergeItemStack(extracted, 0, inventorySlots.size(), false);
                    int removed = toRemove - extracted.getCount();
                    if (removed == 0) return ItemStack.EMPTY;
                    // this does the actual removal of points
                    return tile.data.extractEnchantedBook(enchantmentId, removed, level, false);
                }
                case THROW -> {
                    if (held.isEmpty()) return ItemStack.EMPTY;
                    ItemStack remaining = tile.data.insertItem(0, held, false);
                    player.inventory.setItemStack(remaining);
                    return remaining;
                }
            }
        } else if (mouseButton == MouseUtil.RIGHT) {
            switch (clickType) {
                case THROW -> {
                    if (held.isEmpty()) return ItemStack.EMPTY;
                    ItemStack insert = held.copy();
                    insert.setCount(1);
                    ItemStack remaining = tile.data.insertEnchantedBook(insert, false);
                    if (remaining.isEmpty()) held.shrink(1);
                    if (held.isEmpty()) held = ItemStack.EMPTY;
                    player.inventory.setItemStack(held);
                    return held;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        Slot slot = this.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;
        ItemStack clicked = slot.getStack();
        ItemStack itemstack = clicked.copy();

        //TODO: default shift click behaviour in inv

        clicked = tile.data.insertItem(0, clicked, false);

        if (clicked.isEmpty()) slot.putStack(ItemStack.EMPTY);
        else slot.onSlotChanged();

        if (clicked.getCount() == itemstack.getCount()) return ItemStack.EMPTY;
        return itemstack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tile.canPlayerUse(playerIn);
    }
}
