package nova.committee.atom.eco.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import nova.committee.atom.eco.Static;

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
                Static.LOGGER.info(var3.getMessage());
            }
        }

        return item;
    }

    public static CompoundTag getNBT(ItemStack is) {

        if (is.getTag() == null) {
            is.setTag(new CompoundTag());
        }

        return is.getTag();
    }

    public static boolean TagEquals(ItemStack stack1, ItemStack stack2) {
        return stack1.hasTag() == stack2.hasTag() && (!stack1.hasTag() && !stack2.hasTag() || stack1.getTag().equals(stack2.getTag()));
    }

    public static void attachNBTFromString(ItemStack stack, String nbtString) {

        try {
            stack.setTag(TagParser.parseTag(nbtString));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }


//    public static ItemEntity spawnStackAtLocation(Level world, Location location, ItemStack stack) {
//        return spawnStack(world, location.x + 0.5F, location.y + 0.5F, location.z + 0.5F, stack);
//    }

    public static ItemEntity spawnStackAtEntity(Level world, Entity entity, ItemStack stack) {
        return spawnStack(world, (float) entity.blockPosition().getX() + 0.5F, (float) entity.blockPosition().getY() + 0.5F, (float) entity.blockPosition().getZ() + 0.5F, stack);
    }

    private static ItemEntity spawnStack(Level world, float x, float y, float z, ItemStack stack) {
        ItemEntity item = new ItemEntity(world, x, y, z, stack);
        item.setNoPickUpDelay();
        item.moveTo(-0.05F + rand.nextFloat() * 0.1F, -0.05F + rand.nextFloat() * 0.1F, -0.05F + rand.nextFloat() * 0.1F);
        world.addFreshEntity(item);
        return item;
    }
}
