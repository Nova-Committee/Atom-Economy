package committee.nova.atom.eco.api.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/9 21:43
 * Version: 1.0
 */
public class BaseItem extends Item {

    private Rarity rarity = Rarity.COMMON;
    private boolean hasEffect = false;

    public BaseItem() {
        this(new Properties());
    }

    public BaseItem(ItemGroup tab) {
        this(new Properties().tab(tab));
    }

    public BaseItem(Properties properties) {
        super(properties);
    }

    public BaseItem setEffect() {
        hasEffect = true;
        return this;
    }

    public BaseItem setRarity(Rarity rarity) {
        this.rarity = rarity;
        return this;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return rarity;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return hasEffect;
    }
}
