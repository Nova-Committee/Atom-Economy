package nova.committee.atom.eco.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import nova.committee.atom.eco.init.registry.ModBlocks;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/24 9:49
 * Version: 1.0
 */
public class ModTabs {

    public static CreativeModeTab tab = new CreativeModeTab("tabAtomEco") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModBlocks.ATM.asItem());
        }

    };
}
