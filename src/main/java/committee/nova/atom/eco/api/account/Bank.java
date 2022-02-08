package committee.nova.atom.eco.api.account;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import committee.nova.atom.eco.core.AccountDataManager;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 9:56
 * Version: 1.0
 */
public abstract class Bank  implements Manageable {
    private final String id;//uuid
    protected String name;//
    protected long balance;//余额
    protected TreeMap<String, String> fees; //利率
    protected ArrayList<String> status = new ArrayList<>();//状态
    private JsonObject additionaldata;//附加数据

    /** 解析json文件 */
    public Bank(JsonObject obj){
        id = obj.get("uuid").getAsString();
        name = obj.get("name").getAsString();
        balance = obj.has("balance") ? obj.get("balance").getAsLong() : 0;
        additionaldata = obj.has("data") ? obj.get("data").getAsJsonObject() : null;
        if(obj.has("fees")){
            fees = new TreeMap<>();
            obj.get("fees").getAsJsonObject().entrySet().forEach(entry -> {
                try{
                    fees.put(entry.getKey(), entry.getValue().getAsString());
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            });
        }
        if(obj.has("status")){
            obj.get("status").getAsJsonArray().forEach(elm -> status.add(elm.getAsString()));
        }
        AccountDataManager.getBankNameCache().put(id, name);
    }

    /** 正常构造 */
    public Bank(String id, String name, long balance, JsonObject data, TreeMap<String, String> map){
        this.id = id; this.name = name; this.balance = balance;
        this.fees = map; this.additionaldata = data;
    }

    /** 银行的uuid. */
    public String getId(){ return id; }

    /** 银行的名字. */
    public String getName(){ return name; }

    /** 获取银行的名字. */
    public boolean setName(String name){
        return this.name.equals(name) ? false : (this.name = name).equals(name);
    }

    /** 银行的最新余额  */
    public long getBalance(){
        return balance;
    }

    /** 设置余额
     * @param rpl 账户的新余额
     * @return 新的金额 */
    public long setBalance(long rpl){
        return balance = rpl;
    }

    @Nullable
    public JsonObject getData(){
        return additionaldata;
    }

    public void setData(JsonObject obj){
        additionaldata = obj;
    }

    @Nullable
    public TreeMap<String, String> getFees(){
        return fees;
    }

    /** 覆写. */
    public abstract boolean processAction(Action action, ICommandSource log, Account sender, long amount, Account receiver, boolean fee_included);

    public boolean processAction(Action action, ICommandSource log, Account sender, long amount, Account receiver){
        return processAction(action, log, sender, amount, receiver, true);
    }

    /** json保存. */
    public JsonObject toJson(){
        JsonObject obj = new JsonObject();
        obj.addProperty("uuid", id);
        obj.addProperty("name", name);
        obj.addProperty("balance", balance);
        if(fees != null){
            JsonObject of = new JsonObject();
            for(Map.Entry<String, String> entry : fees.entrySet()){
                of.addProperty(entry.getKey(), entry.getValue());
            }
            obj.add("fees", of);
        }
        if(additionaldata != null){
            obj.add("data", additionaldata);
        }
        if(!status.isEmpty()){
            JsonArray array = new JsonArray();
            for(String str : status) array.add(str);
            obj.add("status", array);
        }
        return obj;
    }

    @Override
    public void modifyBalance(Manageable.Action action, long amount, ICommandSource log){
        switch(action){
            case SET :{ balance = amount; return; }
            case SUB :{
                if(balance - amount >= 0){ balance -= amount; }
                else{
                    log.sendMessage(new StringTextComponent("没有足够的金额来扣除! (B:" + (balance / 1000) + " - S:" + (amount / 1000) + ")"), UUID.randomUUID());

                }
                return;
            }
            case ADD:{
                if(balance + amount >= Long.MAX_VALUE){
                    log.sendMessage(new StringTextComponent("达到了最大值.要溢出来了qwq"), UUID.randomUUID());
                }
                else{ balance += amount; }
            }
            default: return;
        }
    }

    public abstract boolean isNull();

    public ArrayList<String> getStatus(){
        return status;
    }

    public boolean hasFee(String fee_id){
        return fees != null && fees.containsKey(fee_id);
    }

    public static enum Action { TRANSFER, WITHDRAW, DEPOSIT }

}
