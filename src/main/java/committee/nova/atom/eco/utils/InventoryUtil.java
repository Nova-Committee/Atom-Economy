package committee.nova.atom.eco.utils;

import net.minecraft.item.ItemStack;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/8 9:21
 * Version: 1.0
 */
public class InventoryUtil {
    public static boolean ItemMatches(ItemStack stack1, ItemStack stack2) {
        if (stack1.getItem() == stack2.getItem())
            return ItemUtil.TagEquals(stack1, stack2);
        return false;
    }
}
