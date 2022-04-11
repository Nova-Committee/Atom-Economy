package nova.committee.atom.eco.api.core.money;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IMoney extends IForgeRegistryEntry<IMoney> {
    long getWorth();

    ItemStack getItemStack();

    public static interface Item {
        IMoney getType();

        long getWorth();
    }
}
