package committee.nova.atom.eco.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 10:37
 * Version: 1.0
 */
public interface Money extends IForgeRegistryEntry<Money> {

    public long getWorth();

    public ItemStack getItemStack();



    public static interface Item {

        public Money getType();

        /** 不要乘以小数! **/
        public long getWorth(ItemStack stack);

    }
}
