package committee.nova.atom.eco.core;

import com.google.gson.JsonObject;
import committee.nova.atom.eco.Eco;
import committee.nova.atom.eco.api.Account;
import committee.nova.atom.eco.api.Bank;
import committee.nova.atom.eco.api.Manageable;
import committee.nova.atom.eco.common.items.ItemManager;
import committee.nova.atom.eco.utils.Print;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.TreeMap;
import java.util.UUID;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 10:09
 * Version: 1.0
 */
public class GenericBank extends Bank {
    public GenericBank(JsonObject obj){
        super(obj);
    }

    public GenericBank(String id, String name, long balance, JsonObject data, TreeMap<String, String> map){
        super(id, name, balance, data, map);
    }

    public static long parseFee(String fee, long amount){
        if(fee == null){
            return 0;
        }
        long result = 0;
        if(fee.endsWith("%")){
            float pc = Float.parseFloat(fee.replace("%", ""));
            if(pc < 0) return 0;
            if(pc > 100) pc = 100;
            result = (long)((amount / 100) * pc);
        }
        else{
            result = Long.parseLong(fee);
            result = result < 0 ? 0 : /*result > amount ? amount :*/ result;
        }
        return result;
    }

    @Override
    public boolean processAction(Action action, ICommandSource log, Account sender, long amount, Account receiver, boolean included) {
        PlayerEntity player;
        long fee = 0, total;
        switch(action){
            case WITHDRAW:{
                if(sender == null){
                    log.sendMessage(new StringTextComponent("取款失败! 账户不存在."), UUID.randomUUID());

                    Print.debug(log + "账户不存在.");
                    return false;
                }
                if(amount <= 0){
                    log.sendMessage(new StringTextComponent("取款失败! 金额是空的或是负的. (T:" + amount + " || B:" + sender.getBalance() + ");"), UUID.randomUUID());
                    Print.debug(((CommandSource)log).getDisplayName() + " 正在取出空的或是负的数额!");
                    return false;
                }
                player = (PlayerEntity) log;
                if(fees != null){
                    String feestr = fees.get(sender.getType() + ":self");
                    fee = parseFee(feestr, amount);
                }
                total = amount + (included ? 0 : fee);
                if(sender.getBalance() - total >= 0){
                    sender.modifyBalance(Manageable.Action.SUB, total, player);
                    ItemManager.addToInventory(player, amount - (included ? fee : 0));
                    log(player, action, amount, fee, total, included, sender, receiver);
                    return true;
                }
                log.sendMessage(new StringTextComponent("取款失败! 银行没有足够的钱. (W:" + amount + " || B:" + sender.getBalance() + ");"), UUID.randomUUID());

                Print.debug(sender.getAsResourceLocation().toString() + " : 取款失败! 玩家没有足够的钱. (T:" + amount + " || F:" + fee + ");");
                return false;
            }
            case DEPOSIT:{
                if(receiver == null){
                    log.sendMessage(new StringTextComponent("存款失败! 账户不存在."), UUID.randomUUID());

                    Print.debug(((CommandSource)log).getDisplayName() + " -> player account is null.");
                    return false;
                }
                if(amount <= 0){
                    log.sendMessage(new StringTextComponent("存款失败! 金额是空的或是负的. (T:" + amount + " || I:" + ItemManager.countInInventory(log) + ");"), UUID.randomUUID());
                    Print.debug(((CommandSource)log).getDisplayName() + " 正在存入空的或是负的数额!");
                    return false;
                }
                player = (PlayerEntity) log;
                if(receiver.getBalance() + amount <= Long.MAX_VALUE){
                    fee = fees == null ? 0 : parseFee(fees.get("self:" + receiver.getType()), amount);
                    total = amount + (included ? 0 : fee);
                    if(ItemManager.countInInventory(player) - total >= 0){
                        ItemManager.removeFromInventory(player, total);
                        receiver.modifyBalance(Manageable.Action.ADD, amount - (included ? fee : 0), player);
                        log(player, action, amount, fee, total, included, sender, receiver);
                        return true;
                    }
                    else{
                        log.sendMessage(new StringTextComponent("存款失败! 背包里没有足够的钱. (D:" + amount + " || B:" + receiver.getBalance() + ");"), UUID.randomUUID());
                        Print.log(receiver.getAsResourceLocation().toString() + ": 存款失败! 背包里没有足够的钱. (D:" + amount + " || B:" + receiver.getBalance() + ");");
                        return false;
                    }
                }
                log.sendMessage(new StringTextComponent("存款失败! 存款超出了上限.. (D:" + amount + " || B:" + receiver.getBalance() + ");"), UUID.randomUUID());
                Print.log(receiver.getAsResourceLocation().toString() + " : 存款失败! 存款超出了上限. (D:" + amount + " || B:" + receiver.getBalance() + ");");
                return false;
            }
            case TRANSFER:{
                if(sender == null){
                    log.sendMessage(new StringTextComponent("转账失败! 发送方不存在."), UUID.randomUUID());
                    Print.debug(((CommandSource)log).getDisplayName() + " -> 发送方不存在.");
                    return false;
                }
                if(receiver == null){
                    log.sendMessage(new StringTextComponent("转账失败! 收款方不存在."), UUID.randomUUID());
                    Print.debug(((CommandSource)log).getDisplayName() + " -> 收款方不存在.");
                    return false;
                }
                if(amount <= 0){
                    log.sendMessage(new StringTextComponent("转账失败! 金额是空的或是负的. (T:" + amount + ");"), UUID.randomUUID());
                    Print.debug(((CommandSource)log).getDisplayName() + " 正在转空的或是负的数额给 " + receiver.getAsResourceLocation().toString() + "!");
                    return false;
                }
                fee = fees == null ? 0 : parseFee(fees.get(sender.getType() + ":" + receiver.getType()), amount);
                total = amount + (included ? 0 : fee);
                if(sender.getBalance() - total >= 0){
                    sender.modifyBalance(Manageable.Action.SUB, total, log);
                    receiver.modifyBalance(Manageable.Action.ADD, amount - (included ? fee : 0), log);
                    log(null, action, amount, fee, total, included, sender, receiver);
                    return true;
                }
                log.sendMessage(new StringTextComponent("转账失败! 没有足够的钱."), UUID.randomUUID());

                Print.debug(sender.getAsResourceLocation().toString() + " -> " + sender.getAsResourceLocation().toString() + " : 转账失败! 发送方没有足够的钱. (T:" + amount + " || F:" + fee + ");");
                return false;
            }
            default:{
                log.sendMessage(new StringTextComponent("错误的操作. " + action.name() + " || " + ((CommandSource)log).getDisplayName() + " || "
                        + (sender == null ? "null" : sender.getAsResourceLocation().toString()) + " || " + amount + " || " + (receiver == null ? "null" : receiver.getAsResourceLocation().toString())), UUID.randomUUID());

                return false;
            }
        }
    }
    private void log(PlayerEntity player, Action action, long amount, long fee, long total, boolean included, Account sender, Account receiver){
        String s, r;
        switch(action){
            case DEPOSIT:
                s = sender.getTypeAndId();
                r = player.getName().toString();
                break;
            case WITHDRAW:
                s = player.getName().toString();
                r = receiver.getTypeAndId();
                break;
            case TRANSFER:
                s = sender.getTypeAndId();
                r = receiver.getTypeAndId();
                break;
            default:
                s = "INVALID";
                r = "ACTION";
                break;
        }
        String str = s + " -> [A: " + amount + "] + [F: " + fee + (included ? "i" : "e") + "] == [R: " + total + "] -> " + r;
        Eco.LOGGER.info(str);
        Print.debug(str);

    }
    @Override
    public boolean isNull() {
        return false;
    }
}
