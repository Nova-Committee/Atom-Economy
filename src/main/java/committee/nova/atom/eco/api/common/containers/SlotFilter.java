package committee.nova.atom.eco.api.common.containers;

import committee.nova.atom.eco.api.common.tiles.AtomItemHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/9 22:00
 * Version: 1.0
 */
public class SlotFilter extends SlotItemHandler {
    private final List<Item> itemFilters;

    public SlotFilter(AtomItemHandler itemHandler, int id, int x, int y, Item... filters) {
        super(itemHandler, id, x, y);
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
