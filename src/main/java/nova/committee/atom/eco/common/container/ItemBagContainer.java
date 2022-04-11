package nova.committee.atom.eco.common.container;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import nova.committee.atom.eco.util.NbtUtil;

import java.util.Objects;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/10 0:08
 * Version: 1.0
 */
public class ItemBagContainer extends SimpleContainer {
    private static final String TAG_ITEMS = "Items";
    private final ItemStack stack;

    public ItemBagContainer(ItemStack stack, int expectedSize) {
        super(27);
        this.stack = stack;

        ListTag lst = NbtUtil.getList(stack, TAG_ITEMS, 10, false);
        int i = 0;
        for (; i < expectedSize && i < Objects.requireNonNull(lst).size(); i++) {
            setItem(i, ItemStack.of(lst.getCompound(i)));
        }
    }

    @Override
    public boolean stillValid(Player p_19167_) {
        return !stack.isEmpty();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        ListTag list = new ListTag();
        for (int i = 0; i < getContainerSize(); i++) {
            list.add(getItem(i).save(new CompoundTag()));
        }
        NbtUtil.setList(stack, TAG_ITEMS, list);
    }
}
