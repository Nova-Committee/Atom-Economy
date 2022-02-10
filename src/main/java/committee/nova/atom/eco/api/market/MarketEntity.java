package committee.nova.atom.eco.api.market;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundNBT;

/**
 * Description:市场物品的实体类
 * Author: cnlimiter
 * Date: 2022/2/8 15:48
 * Version: 1.0
 */
public class MarketEntity {


    public int index;
    public int amount;
    public int value;
    String displayName;
    String registryName;
    String stackNBT;
    String ownerUUID;
    boolean sell;


    public MarketEntity(JsonObject json) {
        this.index = json.get("index").getAsInt();
        this.displayName = json.get("displayName").getAsString();
        this.registryName = json.get("registryName").getAsString();
        this.stackNBT = json.get("stackNBT").getAsString();
        this.ownerUUID = json.get("ownerUUID").getAsString();
        this.sell = json.get("sell").getAsBoolean();
        this.amount = json.get("amount").getAsInt();
        this.value = json.get("value").getAsInt();
    }

    public MarketEntity(int index, String displayName, String registryName, String stackNBT, String ownerUUID, boolean sell, int amount, int value) {
        this.index = index;
        this.displayName = displayName;
        this.registryName = registryName;
        this.stackNBT = stackNBT;
        this.ownerUUID = ownerUUID;
        this.sell = sell;
        this.amount = amount;
        this.value = value;
    }

    public static MarketEntity readFromNBT(CompoundNBT nbt) {
        String stackNBT = nbt.getString("stackNBT");
        return new MarketEntity(nbt.getInt("index"), nbt.getString("displayName"), nbt.getString("registryName"), stackNBT, nbt.getString("ownerUUID"), nbt.getBoolean("sell"), nbt.getInt("amount"), nbt.getInt("value"));
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public void setStackNBT(String stackNBT) {
        this.stackNBT = stackNBT;
    }

    public void setOwnerUUID(String ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public void setSell(boolean sell) {
        this.sell = sell;
    }

    public int getIndex() {
        return index;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRegistryName() {
        return registryName;
    }

    public String getStackNBT() {
        return stackNBT;
    }

    public String getOwnerUUID() {
        return ownerUUID;
    }

    public boolean isSell() {
        return sell;
    }

    public int getAmount() {
        return amount;
    }

    public int getValue() {
        return value;
    }

    public CompoundNBT writeToNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("index", index);
        nbt.putString("displayName", displayName);
        nbt.putString("registryName", registryName);
        nbt.putString("stackNBT", stackNBT);
        nbt.putString("ownerUUID", ownerUUID);
        nbt.putBoolean("sell", sell);
        nbt.putInt("amount", amount);
        nbt.putInt("value", value);
        return nbt;
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("index", index);
        obj.addProperty("displayName", displayName);
        obj.addProperty("registryName", registryName);
        obj.addProperty("stackNBT", sell);
        obj.addProperty("ownerUUID", ownerUUID);
        obj.addProperty("sell", sell);
        obj.addProperty("amount", amount);
        obj.addProperty("value", value);
        return obj;
    }

    @Override
    public String toString() {
        return "MarketEntity[序号=" + index + ", 显示名字=" + displayName + ", 物品注册名=" + registryName + ", 物品nbt=" + stackNBT + ", 拥有者UUID=" + ownerUUID + ", 类型=" + (sell ? "售出" : "收购") + ", 数量=" + amount + ", 单个价值=" + value + "]";
    }
}
