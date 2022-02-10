package committee.nova.atom.eco.api.common.containers.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/8 9:49
 * Version: 1.0
 */
public class DisplaySlot extends Slot {

    public DisplaySlot(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }


    @Override
    public boolean mayPlace(ItemStack pStack) {
        return false;
    }

    @Override
    public boolean mayPickup(PlayerEntity pPlayer) {
        return false;
    }

    @Override
    public void set(ItemStack pStack) {
        super.set(pStack);
    }

    @Override
    public ItemStack remove(int pAmount) {
        return ItemStack.EMPTY;
    }


}
