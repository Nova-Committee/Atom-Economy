package committee.nova.atom.eco.api;

import com.google.gson.JsonObject;
import committee.nova.atom.eco.init.events.AccountEvent;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 9:40
 * Version: 1.0
 */
public class Account  implements Manageable{
    private final String id;
    private final String type;
    private String bank;
    private String name;
    private long balance;
    private JsonObject additionaldata;

    /** 解析json文件 */
    public Account(JsonObject obj){
        id = obj.get("id").getAsString();
        type = obj.get("type").getAsString();
        bank = obj.get("bank").getAsString();
        balance = obj.get("balance").getAsLong();
        additionaldata = obj.has("data") ? obj.get("data").getAsJsonObject() : null;
        name = obj.has("name") ? obj.get("name").getAsString() : null;
    }

    /** 正常构造 */
    public Account(String id, String type, long balance, String bank, JsonObject data){
        this.id = id;
        this.type = type;
        this.balance = balance;
        this.bank = bank;
        this.additionaldata = data;
    }

    /** 账户的uuid. */
    public String getId(){ return id; }

    /** 账户余额 (1000 = 1 currency unit, usually) */
    public long getBalance(){
        //this.updateLastAccess();
        return balance;
    }

    /** 设置余额 (1000 = 1 currency unit, usually)
     * @param rpl 这个账户的新的余额
     * @return 新的余额 */
    public long setBalance(long rpl){
        MinecraftForge.EVENT_BUS.post(new AccountEvent.BalanceUpdated(this, balance, rpl));
        return balance = rpl;
    }

    /** 获取账户对应银行的id. */
    public String getBankId(){ return bank; }

    /** 设置账户对应银行的id */
    public boolean setBankId(String id){
        return bank.equals(id) ? false : (bank = id).equals(id);
    }

    /** 此帐号的类型，因为不仅玩家可以有帐号. */
    public String getType(){ return type; }

    public ResourceLocation getAsResourceLocation(){
        return new ResourceLocation(this.getType(), this.getId());
    }

    public String getTypeAndId(){
        return this.getType() + ":" + this.getId();
    }

    @Nullable
    public JsonObject getData(){
        return additionaldata;
    }

    public void setData(JsonObject obj){
        additionaldata = obj;
    }

    @Nullable
    public String getName(){
        return name == null ? id : name;
    }

    public Account setName(String name){
        this.name = name;
        return this;
    }


    /** json保存. */
    public JsonObject toJson(){
        JsonObject obj = new JsonObject();
        obj.addProperty("id", id);
        obj.addProperty("type", type);
        obj.addProperty("bank", bank);
        obj.addProperty("balance", balance);
        if(additionaldata != null){
            obj.add("data", additionaldata);
        }
        if(name != null) obj.addProperty("name", name);
        return obj;
    }

    @Override
    public void modifyBalance(Action action, long amount, ICommandSource source) {
        switch(action){
            case SET :{
                MinecraftForge.EVENT_BUS.post(new AccountEvent.BalanceUpdated(this, balance, amount));
                balance = amount;
                return;
            }
            case SUB :{
                if(balance - amount >= 0){
                    MinecraftForge.EVENT_BUS.post(new AccountEvent.BalanceUpdated(this, balance, balance -= amount));
                }
                else{
                    source.sendMessage(new StringTextComponent("没有足够的金额来扣除! (B:" + (balance / 1000) + " - S:" + (amount / 1000) + ")"), UUID.randomUUID());
                }
                return;
            }
            case ADD:{
                if(balance + amount >= Long.MAX_VALUE){
                    source.sendMessage(new StringTextComponent("达到了最大值.要溢出来了qwq"), UUID.randomUUID());
                }
                else{ MinecraftForge.EVENT_BUS.post(new AccountEvent.BalanceUpdated(this, balance, balance += amount)); }
            }
            default: return;
        }
    }

}
