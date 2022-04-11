package nova.committee.atom.eco.core;

import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import nova.committee.atom.eco.api.core.money.IMoney;
import nova.committee.atom.eco.util.text.LogUtil;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/10 0:29
 * Version: 1.0
 */
public class MoneyItemManager {

    public static long countInInventory(CommandSource sender) {
        return sender instanceof Player ? countInInventory((Player) sender) : -1;
    }

    public static long countInInventory(Player player) {
        long value = 0L;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEmpty()) continue;
            long worth = ConfigDataManager.getItemStackWorth(stack);
            LogUtil.debug(stack.toString(), stack.getItem() instanceof IMoney.Item ? ((IMoney.Item) stack.getItem()).getType().toString() : "不是合法的货币");
            value += worth * stack.getCount();
        }
        return value;
    }

    public static boolean hasSpace(Player player, boolean countMoneyItemAsSpace) {
        int i = 0;
        for (ItemStack stack : player.getInventory().items) {
            while (i >= 1) break;
            if (stack.isEmpty()) {
                i++;
            } else if (ConfigDataManager.getItemStackWorth(stack) > 0 && countMoneyItemAsSpace) {
                i++;
            }
        }
        return i > 0;
    }


    public static long addToInventory(Player player, long amount) {
        return setInInventory(player, (amount += countInInventory(player)) > Long.MAX_VALUE ? Long.MAX_VALUE : amount);
    }

    public static long removeFromInventory(Player player, long amount) {
        long old = countInInventory(player);
        old -= amount;
        if (old < 0) {
            amount += old;
            old = 0;
        }
        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEmpty()) continue;
            if (ConfigDataManager.getItemStackWorth(stack) > 0) {
                stack.shrink(64);
            }
        }
        setInInventory(player, old);
        return amount;
    }

    public static long setInInventory(Player player, long amount) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEmpty()) continue;
            if (ConfigDataManager.getItemStackWorth(stack) > 0) {
                stack.shrink(64);
            }
        }
        List<IMoney> list = getSortedMoneyList();
        IMoney money = null;
        for (IMoney iMoney : list) {
            LogUtil.debug(iMoney.getWorth(), iMoney.getRegistryName());
            while (amount - (money = iMoney).getWorth() >= 0) {
                ItemStack stack = money.getItemStack().copy();
                if (hasSpace(player, false)) {
                    player.getInventory().add(stack);
                } else {
                    player.getCommandSenderWorld().addFreshEntity(new ItemEntity(player.getCommandSenderWorld(), player.getX(), player.getY(), player.getZ(), stack));
                }
                amount -= money.getWorth();
            }
        }
        if (amount > 0) {
            player.sendMessage(new TextComponent(amount + " 不能被添加到物品栏，因为没有相应的物品"), UUID.randomUUID());
        }
        return amount;
    }

//    public static List<IMoney> getSortedMoneyList(){
//
//        // 排序，按照货币的价值排序
//        return ConfigDataManager.MONEY_ARRAY_LIST.stream().sorted(Comparator.comparingLong(IMoney::getWorth).reversed()).collect(Collectors.toList());
//
//    }

    public static List<IMoney> getSortedMoneyList() {
        return ConfigDataManager.MONEY_ARRAY_LIST.stream().sorted(new Comparator<IMoney>() {
            @Override
            public int compare(IMoney o1, IMoney o2) {
                return o1.getWorth() < o2.getWorth() ? 1 : -1;
            }
        }).collect(Collectors.toList());
    }

}
