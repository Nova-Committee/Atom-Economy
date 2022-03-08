package committee.nova.atom.eco.api.common.containers.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/10 7:20
 * Version: 1.0
 */
public class SlotIInventoryFilter extends Slot {

    private final List<Item> itemFilters;

    public SlotIInventoryFilter(IInventory tileEntity, int id, int x, int y, Item... filters) {
        super(tileEntity, id, x, y);
        this.itemFilters = new ArrayList<>();
        this.itemFilters.addAll(Arrays.asList(filters));
    }


    @Override
    public boolean mayPlace(ItemStack stack) {
        return isFilter(stack);
    }

    private boolean isFilter(ItemStack stack) {

        if (this.itemFilters != null) {
            for (Item itemFilter : this.itemFilters) {
                if (itemFilter == stack.getItem()) return true;
            }
        }

        return false;
    }
}
