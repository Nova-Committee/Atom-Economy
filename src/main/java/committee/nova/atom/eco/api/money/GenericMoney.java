package committee.nova.atom.eco.api.money;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import committee.nova.atom.eco.Eco;
import committee.nova.atom.eco.Static;
import committee.nova.atom.eco.utils.ItemUtil;
import committee.nova.atom.eco.utils.JsonUtil;
import committee.nova.atom.eco.utils.math.Time;
import committee.nova.atom.eco.utils.text.LogUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 10:50
 * Version: 1.0
 */
public class GenericMoney implements Money {
    private ResourceLocation regname;
    private ItemStack stack;
    private long worth;

    public GenericMoney(JsonObject obj, boolean internal){
        regname = new ResourceLocation((internal ? Eco.MOD_ID + ":" : "") + JsonUtil.getIfExists(obj, "id", "invalid_" + obj.toString() + "_" + Time.getDate()));
        worth = JsonUtil.getIfExists(obj, "worth", -1).longValue();
        if(!internal){
            stackload(null, obj, internal);
        }
    }

    public void stackload(net.minecraft.item.Item item, JsonObject obj, boolean internal){
        if(item == null || !internal){
            String id = JsonUtil.getIfExists(obj, "id", "invalid_" + obj.toString() + "_" + Time.getDate());
            item = ItemUtil.getByNameOrId(internal ? Eco.MOD_ID + ":" + id : id);
            if(item == null){
                LogUtil.log("[ATOME] ERROR - External Item with ID '" + regname.toString() + "' couldn't be found! This is bad!");
                Static.halt();
            }
        }
        CompoundNBT compound = null;
        if(obj.has("nbt")){
            try{
                compound = JsonToNBT.parseTag(obj.get("nbt").getAsString());
            }
            catch(CommandSyntaxException e){
                LogUtil.log("[ATOME] ERROR - Could not load NBT from config of '" + regname.toString() + "'! This is bad!");
                Static.halt();
            }
        }

        stack = new ItemStack(item, 1, compound);
    }

    @Override
    public Money setRegistryName(ResourceLocation name){
        regname = name;
        return this;
    }

    @Override
    public ResourceLocation getRegistryName(){
        return regname;
    }

    @Override
    public Class<Money> getRegistryType(){
        return Money.class;
    }

    @Override
    public long getWorth(){
        return worth;
    }

    @Override
    public String toString(){
        return super.toString() + "#" + this.getWorth();
    }

    @Override
    public ItemStack getItemStack(){
        return stack;
    }

}
