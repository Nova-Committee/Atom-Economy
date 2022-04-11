package nova.committee.atom.eco.core.model;

import com.google.gson.JsonObject;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import nova.committee.atom.eco.api.core.account.Manageable;
import nova.committee.atom.eco.api.init.event.AccountEvent;
import nova.committee.atom.eco.core.MoneyItemManager;
import nova.committee.atom.eco.util.text.LogUtil;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/10 9:46
 * Version: 1.0
 */
public class Account implements Manageable {

    private final String id; //玩家 uuid
    private final String type; // 种类
    private String name; // 名字
    private long balance; // 银行余额
    private JsonObject additionaldata; // 附加数据

    /**
     * 解析json文件
     */
    public Account(JsonObject obj) {
        id = obj.get("id").getAsString();
        type = obj.get("type").getAsString();
        balance = obj.get("balance").getAsLong();
        additionaldata = obj.has("data") ? obj.get("data").getAsJsonObject() : null;
        name = obj.has("name") ? obj.get("name").getAsString() : null;
    }

    /**
     * 正常构造
     */
    public Account(String id, String type, long balance, JsonObject data) {
        this.id = id;
        this.type = type;
        this.balance = balance;
        this.additionaldata = data;
    }

    /**
     * 账户的uuid.
     */
    public String getId() {
        return id;
    }

    /**
     * 身上余额
     */
    public long getBalance() {
        return balance;
    }

    /**
     * 设置身上余额
     *
     * @param rpl 这个玩家身上的新的余额
     * @return 新的余额
     */
    public long setBalance(long rpl) {
        MinecraftForge.EVENT_BUS.post(new AccountEvent.BalanceUpdated(this, balance, rpl));
        return balance = rpl;
    }


    /**
     * 此帐号的类型，普通/op.
     */
    public String getType() {
        return type;
    }

    @Nullable
    public JsonObject getData() {
        return additionaldata;
    }

    public void setData(JsonObject obj) {
        additionaldata = obj;
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", id);
        obj.addProperty("type", type);
        obj.addProperty("balance", balance);
        if (additionaldata != null) {
            obj.add("data", additionaldata);
        }
        if (name != null) obj.addProperty("name", name);
        return obj;
    }

    @Override
    public boolean processBankAction(Action action, CommandSource log, Account sender, Account receiver, long amount) {
        Player player;
        player = (Player) log;
        long total;
        switch (action) {
            case DEPOSIT: {
                if (receiver == null) {
                    log.sendMessage(new TextComponent("存款失败! 账户不存在."), UUID.randomUUID());

                    LogUtil.debug(((ServerPlayer) log).getDisplayName() + "账户不存在");
                    return false;
                }
                if (amount <= 0) {
                    log.sendMessage(new TextComponent("存款失败! 金额是空的或是负的. (T:" + amount + " || I:" + MoneyItemManager.countInInventory(log) + ");"), UUID.randomUUID());
                    LogUtil.debug(((ServerPlayer) log).getDisplayName() + " 正在存入空的或是负的数额!");
                    return false;
                }

                total = amount;
                if (MoneyItemManager.countInInventory(player) - total >= 0) {
                    MoneyItemManager.removeFromInventory(player, total);
                    receiver.modifyBalance(Action.SELF_ADD, amount, player);
                    //log.sendMessage(new TextComponent(ChatFormatting.BLUE +"存入 " + ChatFormatting.RED + amount + " " + ChatFormatting.GREEN + "成功!"), UUID.randomUUID());
                    return true;
                } else {
                    log.sendMessage(new TextComponent("存款失败! 背包里没有足够的钱. (D:" + amount + " || B:" + receiver.getBalance() + ");"), UUID.randomUUID());
                    LogUtil.log("存款失败!" + ((ServerPlayer) log).getDisplayName() + " 背包里没有足够的钱. (D:" + amount + " || B:" + receiver.getBalance() + ");");
                    return false;
                }
            }
            case WITHDRAW: {
                if (sender == null) {
                    log.sendMessage(new TextComponent("取款失败! 账户不存在."), UUID.randomUUID());
                    LogUtil.debug(((ServerPlayer) log).getDisplayName() + "账户不存在.");
                    return false;
                }
                if (amount <= 0) {
                    log.sendMessage(new TextComponent("取款失败! 金额是空的或是负的. (T:" + amount + " || B:" + sender.getBalance() + ");"), UUID.randomUUID());
                    LogUtil.debug(((ServerPlayer) log).getDisplayName() + " 正在取出空的或是负的数额!");
                    return false;
                }

                total = amount;
                if (sender.getBalance() - total >= 0) {
                    sender.modifyBalance(Action.SELF_SUB, total, player);
                    MoneyItemManager.addToInventory(player, amount);
                    //log.sendMessage(new TextComponent(ChatFormatting.BLUE +"取出 " + ChatFormatting.RED + amount + " " + ChatFormatting.GREEN + "成功!"), UUID.randomUUID());
                    return true;
                } else {
                    log.sendMessage(new TextComponent("取款失败! 银行没有足够的钱. (W:" + amount + " || B:" + sender.getBalance() + ");"), UUID.randomUUID());
                    LogUtil.debug(" : 取款失败! 玩家没有足够的钱. (T:" + amount + ");");
                    return false;
                }
            }
            case TRANSFER: {
                if (sender == null) {
                    log.sendMessage(new TextComponent("转账失败! 发送方不存在."), UUID.randomUUID());
                    LogUtil.debug(((ServerPlayer) log).getDisplayName() + " -> 发送方不存在.");
                    return false;
                }
                if (receiver == null) {
                    log.sendMessage(new TextComponent("转账失败! 收款方不存在."), UUID.randomUUID());
                    LogUtil.debug(((ServerPlayer) log).getDisplayName() + " -> 收款方不存在.");
                    return false;
                }
                if (amount <= 0) {
                    log.sendMessage(new TextComponent("转账失败! 金额是空的或是负的. (T:" + amount + ");"), UUID.randomUUID());
                    LogUtil.debug(((ServerPlayer) log).getDisplayName() + " 正在转空的或是负的数额给 " + receiver.getId().toString() + "!");
                    return false;
                }
                total = amount;
                if (sender.getBalance() - total >= 0) {
                    sender.modifyBalance(Action.SELF_SUB, total, log);
                    receiver.modifyBalance(Action.SELF_ADD, amount, log);
                    //log.sendMessage(new TextComponent(ChatFormatting.BLUE + "转账 " + ChatFormatting.RED + amount + "" + ChatFormatting.DARK_PURPLE + "金额成功执行."), UUID.randomUUID());
                    return true;
                } else {
                    log.sendMessage(new TextComponent("转账失败! 没有足够的钱."), UUID.randomUUID());
                    LogUtil.debug(sender.getId().toString() + " -> " + receiver.getId().toString() + " : 转账失败! 发送方没有足够的钱. (T:" + amount + ");");
                    return false;
                }

            }
            default:
                log.sendMessage(new TextComponent("无操作"), UUID.randomUUID());
                return false;
        }
    }

    @Override
    public boolean modifyBalance(Action action, long amount, CommandSource source) {
        switch (action) {
            case SELF_SET: {
                MinecraftForge.EVENT_BUS.post(new AccountEvent.BalanceUpdated(this, balance, amount));
                balance = amount;
                return true;
            }
            case SELF_SUB: {
                if (balance - amount >= 0) {
                    MinecraftForge.EVENT_BUS.post(new AccountEvent.BalanceUpdated(this, balance, balance -= amount));
                    return true;
                } else {
                    source.sendMessage(new TextComponent("没有足够的金额来扣除! (B:" + balance + " - S:" + amount + ")"), UUID.randomUUID());
                    return false;
                }
            }
            case SELF_ADD: {
                if (balance + amount >= Long.MAX_VALUE) {
                    source.sendMessage(new TextComponent("达到了最大值.要溢出来了qwq"), UUID.randomUUID());
                    return false;
                } else {
                    MinecraftForge.EVENT_BUS.post(new AccountEvent.BalanceUpdated(this, balance, balance += amount));
                    return true;
                }
            }

            default:
                source.sendMessage(new TextComponent("无操作"), UUID.randomUUID());
                return false;

        }
    }
}
