package io.github.kurrycat2004.enchlib.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public abstract class ContainerBase extends Container {
    private static final int PLAYER_INV_X_SLOTS = 9;
    private static final int PLAYER_INV_Y_SLOTS = 3;

    private static final int SLOT_SIZE = 18;
    private static final int SLOT_GROUP_MARGIN = 4;

    protected void addPlayerInvSlots(InventoryPlayer inv, int yOffset) {
        for (int row = 0; row < PLAYER_INV_Y_SLOTS; ++row) {
            for (int column = 0; column < PLAYER_INV_X_SLOTS; ++column) {
                this.addSlotToContainer(new Slot(inv,
                        column + (row + 1) * PLAYER_INV_X_SLOTS,
                        8 + column * SLOT_SIZE,
                        row * SLOT_SIZE + yOffset
                ));
            }
        }

        for (int column = 0; column < PLAYER_INV_X_SLOTS; ++column) {
            this.addSlotToContainer(new Slot(inv,
                    column,
                    8 + column * SLOT_SIZE,
                    SLOT_SIZE * PLAYER_INV_Y_SLOTS + SLOT_GROUP_MARGIN + yOffset
            ));
        }
    }
}
