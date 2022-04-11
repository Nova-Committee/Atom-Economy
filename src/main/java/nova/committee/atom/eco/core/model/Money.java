package nova.committee.atom.eco.core.model;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import nova.committee.atom.eco.Static;
import nova.committee.atom.eco.api.core.money.IMoney;
import nova.committee.atom.eco.util.DateUtil;
import nova.committee.atom.eco.util.ItemUtil;
import nova.committee.atom.eco.util.JsonUtil;
import nova.committee.atom.eco.util.text.LogUtil;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/9 23:18
 * Version: 1.0
 */
public class Money implements IMoney {
    private final long worth; //价值
    private ResourceLocation regName; //注册名
    private ItemStack stack; // 物品

    public Money(JsonObject obj, boolean mod) {
        regName = new ResourceLocation((mod ? Static.MOD_ID + ":" : "") + JsonUtil.getIfExists(obj, "id", "invalid_" + obj + "_" + DateUtil.getDate()));
        worth = JsonUtil.getIfExists(obj, "worth", -1).longValue();
        stackLoad(obj, mod);
    }

    public void stackLoad(JsonObject obj, boolean mod) {
        net.minecraft.world.item.Item item = null;
        if (!mod) {
            String id = JsonUtil.getIfExists(obj, "id", "invalid_" + obj.toString() + "_" + DateUtil.getDate());
            item = ItemUtil.getByNameOrId(id);
            if (item == null) {
                LogUtil.log("[ATOMECO] 错误 - ID为 '" + regName.toString() + "' 的物品不能找到!");
            }
        } else {
            String id = JsonUtil.getIfExists(obj, "id", "invalid_" + obj.toString() + "_" + DateUtil.getDate());
            item = ItemUtil.getByNameOrId(Static.MOD_ID + ":" + id);
            if (item == null) {
                LogUtil.log("[ATOMECO] 错误 - ID为 '" + regName.toString() + "' 的物品不能找到!");
            }
        }
        CompoundTag compound = null;
        if (obj.has("nbt")) {
            try {
                compound = TagParser.parseTag(obj.get("nbt").getAsString());
            } catch (CommandSyntaxException e) {
                LogUtil.log("[ATOMECO] 错误 - 加载nbt失败 '" + regName.toString());
            }
        }

        stack = new ItemStack(item, 1, compound);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return regName;
    }

    @Override
    public Money setRegistryName(ResourceLocation name) {
        regName = name;
        return this;
    }

    @Override
    public Class<IMoney> getRegistryType() {
        return IMoney.class;
    }

    @Override
    public long getWorth() {
        return worth;
    }

    @Override
    public String toString() {
        return super.toString() + "#" + this.getWorth();
    }

    @Override
    public ItemStack getItemStack() {
        return stack;
    }
}
