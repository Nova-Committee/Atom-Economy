package nova.committee.atom.eco.util;

import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/10 14:12
 * Version: 1.0
 */
public class NbtUtil {

    public static ListTag getList(ItemStack stack, String tag, int objtype, boolean nullifyOnFail) {
        return verifyExistance(stack, tag) ? stack.getOrCreateTag().getList(tag, objtype) : nullifyOnFail ? null : new ListTag();
    }


    public static void setList(ItemStack stack, String tag, ListTag list) {
        stack.getOrCreateTag().put(tag, list);
    }


    public static boolean verifyExistance(ItemStack stack, String tag) {
        return !stack.isEmpty() && stack.getOrCreateTag().contains(tag);
    }
}
