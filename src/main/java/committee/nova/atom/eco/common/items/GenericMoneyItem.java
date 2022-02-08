package committee.nova.atom.eco.common.items;

import committee.nova.atom.eco.api.money.Money;
import committee.nova.atom.eco.init.ModTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 10:58
 * Version: 1.0
 */
public class GenericMoneyItem extends Item implements Money.Item {

    private final Money type;


    public GenericMoneyItem(Money money) {
        super(new Properties()
                .stacksTo(50)
                .tab(ModTabs.tab)
        );
        this.type = money;
    }

    @Override
    public Money getType(){
        return type;
    }

    @Override
    public long getWorth(ItemStack stack){
        return type.getWorth()/* * stack.getCount()*/;
    }
}
