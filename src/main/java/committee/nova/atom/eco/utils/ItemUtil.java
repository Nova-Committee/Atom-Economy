package committee.nova.atom.eco.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import committee.nova.atom.eco.Eco;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 10:54
 * Version: 1.0
 */
public class ItemUtil {

    public static Item getByNameOrId(String nameId)
    {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(nameId));

        if (item == null)
        {
            try
            {
                return Item.byId(Integer.parseInt(nameId));
            } catch (NumberFormatException var3) {
                Eco.LOGGER.info(var3.getMessage());
            }
        }

        return item;
    }

    public static boolean TagEquals(ItemStack stack1, ItemStack stack2) {
        return stack1.hasTag() == stack2.hasTag() && (!stack1.hasTag() && !stack2.hasTag() || stack1.getTag().equals(stack2.getTag()));
    }

    public static void attachNBTFromString(ItemStack stack, String nbtString) {

        try {
            stack.setTag(JsonToNBT.parseTag(nbtString));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }
}
