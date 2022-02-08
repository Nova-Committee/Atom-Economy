package committee.nova.atom.eco.core;

import committee.nova.atom.eco.Eco;
import committee.nova.atom.eco.api.money.Money;
import committee.nova.atom.eco.common.config.ConfigUtil;
import committee.nova.atom.eco.utils.PrintUtil;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.UUID;

/**
 * Description: 实体货币管理类
 * Author: cnlimiter
 * Date: 2022/1/20 11:42
 * Version: 1.0
 */
public class MoneyItemManager {
    public static long countInInventory(ICommandSource sender) {
        return sender instanceof PlayerEntity ? countInInventory((PlayerEntity) sender) : -1;
    }

    public static long countInInventory(PlayerEntity player) {
        long value = 0L;
        for (ItemStack stack : player.inventory.items) {
            if (stack.isEmpty()) continue;
            long worth = ConfigUtil.getItemStackWorth(stack);
            PrintUtil.debug(stack.toString(), stack.getItem() instanceof Money.Item ? ((Money.Item) stack.getItem()).getType().toString() : "不是合法的货币");
            value += worth * stack.getCount();
        }
        return value;
    }

    public static boolean hasSpace(PlayerEntity player, boolean countMoneyItemAsSpace){
        int i = 0;
        for(ItemStack stack : player.inventory.items){
            while(i >= 1) break;
            if(stack.isEmpty()){
                i++;
            }
            else if(ConfigUtil.getItemStackWorth(stack) > 0 && countMoneyItemAsSpace){
                i++;
            }
            else{
                continue;
            }
        }
        return i > 0;
    }

    public static long addToInventory(PlayerEntity player, long amount){
        return setInInventory(player, (amount += countInInventory(player)) > Long.MAX_VALUE ? Long.MAX_VALUE : amount);
    }

    public static long removeFromInventory(PlayerEntity player, long amount){
        long old = countInInventory(player);
        old -= amount; if(old < 0){ amount += old; old = 0; }
        for(ItemStack stack : player.inventory.items){
            if(stack.isEmpty()) continue;
            if(ConfigUtil.getItemStackWorth(stack) > 0){
                stack.shrink(64);
            }
        }
        setInInventory(player, old);
        return amount;
    }

    public static long setInInventory(PlayerEntity player, long amount){
        for(ItemStack stack : player.inventory.items){
            if(stack.isEmpty()) continue;
            if(ConfigUtil.getItemStackWorth(stack) > 0){
                stack.shrink(64);
            }
        }
        List<Money> list = Eco.getSortedMoneyList();
        Money money = null;
        for(int i = 0; i < list.size(); i++){
            PrintUtil.debug(list.get(i).getWorth(), list.get(i).getRegistryName());
            while(amount - (money = list.get(i)).getWorth() >= 0){
                ItemStack stack = money.getItemStack().copy();
                if(hasSpace(player, false)){
                    player.inventory.add(stack);
                }
                else{
                    player.getCommandSenderWorld().addFreshEntity(new ItemEntity(player.getCommandSenderWorld(), player.getX(), player.getY(), player.getZ(), stack));
                }
                amount -= money.getWorth();
            }
            continue;
        }
        if(amount > 0){
            player.sendMessage(new StringTextComponent(ConfigUtil.getWorthAsString(amount, true, true) + " 不能被添加到物品栏，因为没有相应的物品"), UUID.randomUUID());
        }
        return amount;
    }
}
