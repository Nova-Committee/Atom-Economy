package committee.nova.atom.eco.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/24 9:49
 * Version: 1.0
 */
public class ModTabs {

    public static ItemGroup tab = new ItemGroup("tabAtomEco") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.DIRT);
        }

    };
}
