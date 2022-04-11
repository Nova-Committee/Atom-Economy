package nova.committee.atom.eco.common.item;

import net.minecraft.world.item.Item;
import nova.committee.atom.eco.api.core.money.IMoney;
import nova.committee.atom.eco.init.ModTabs;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/9 23:55
 * Version: 1.0
 */
public class MoneyItem extends Item implements IMoney.Item {

    private final IMoney type;

    public MoneyItem(IMoney money) {
        super(new Properties()
                .stacksTo(16)
                .tab(ModTabs.tab));
        this.type = money;
    }

    @Override
    public IMoney getType() {
        return type;
    }

    @Override
    public long getWorth() {
        return type.getWorth();
    }
}
