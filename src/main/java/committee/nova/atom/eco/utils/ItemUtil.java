package committee.nova.atom.eco.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import committee.nova.atom.eco.Eco;
import committee.nova.atom.eco.utils.math.Location;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 10:54
 * Version: 1.0
 */
public class ItemUtil {

    private static final Random rand = new Random();


    public static Item getByNameOrId(String nameId) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(nameId));

        if (item == null) {
            try {
                return Item.byId(Integer.parseInt(nameId));
            } catch (NumberFormatException var3) {
                Eco.LOGGER.info(var3.getMessage());
            }
        }

        return item;
    }

    public static CompoundNBT getNBT(ItemStack is) {

        if (is.getTag() == null) {
            is.setTag(new CompoundNBT());
        }

        return is.getTag();
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


    public static ItemEntity spawnStackAtLocation(World world, Location location, ItemStack stack) {
        return spawnStack(world, location.x + 0.5F, location.y + 0.5F, location.z + 0.5F, stack);
    }

    public static ItemEntity spawnStackAtEntity(World world, Entity entity, ItemStack stack) {
        return spawnStack(world, (float) entity.blockPosition().getX() + 0.5F, (float) entity.blockPosition().getY() + 0.5F, (float) entity.blockPosition().getZ() + 0.5F, stack);
    }

    private static ItemEntity spawnStack(World world, float x, float y, float z, ItemStack stack) {
        ItemEntity item = new ItemEntity(world, x, y, z, stack);
        item.setNoPickUpDelay();
        item.moveTo(-0.05F + rand.nextFloat() * 0.1F, -0.05F + rand.nextFloat() * 0.1F, -0.05F + rand.nextFloat() * 0.1F);
        world.addFreshEntity(item);
        return item;
    }
}
