package committee.nova.atom.eco.api.common.containers.inventorys;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/10 7:42
 * Version: 1.0
 */
public class ItemStackInventory implements IInventory {
    public final NonNullList<ItemStack> slots;

    public ItemStackInventory(ItemStack stack, int size) {

        slots = NonNullList.withSize(size, ItemStack.EMPTY);

        CompoundNBT mainTag = stack.getTag();

        if (mainTag != null && mainTag.get("inv") != null) {
            CompoundNBT itemListTag = mainTag.getCompound("inv");
            ItemStackHelper.loadAllItems(itemListTag, slots);
        }
    }

    public void dump(ItemStack stack) {

        CompoundNBT mainTag = stack.getTag();

        if (mainTag == null) {
            mainTag = new CompoundNBT();
        }

        CompoundNBT itemListTag = new CompoundNBT();
        ItemStackHelper.saveAllItems(itemListTag, slots);
        mainTag.put("inv", itemListTag);
        stack.setTag(mainTag);
    }

    @Override
    public ItemStack getItem(int slot) {
        return slots.get(slot);
    }

    @Override
    public ItemStack removeItem(int index, int count) {

        ItemStack itemstack;

        if (this.slots.get(index).getCount() <= count) {
            itemstack = this.slots.get(index);
            this.slots.set(index, ItemStack.EMPTY);
        } else {
            itemstack = this.slots.get(index).split(count);
            if (slots.get(index) == ItemStack.EMPTY) {
                this.slots.set(index, ItemStack.EMPTY);
            }
        }

        return itemstack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {

        this.slots.set(slot, stack);

        if (stack.getCount() > this.getMaxStackSize()) {
            removeItem(slot, stack.getCount() - this.getMaxStackSize());
        }
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack copy = getItem(index).copy();
        setItem(index, ItemStack.EMPTY);
        return copy;
    }

    @Override
    public boolean isEmpty() {

        for (ItemStack stack : slots) {
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < getContainerSize(); i++) {
            slots.set(i, ItemStack.EMPTY);
        }
    }

    @Override
    public int getContainerSize() {
        return slots.size();
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return true;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Override
    public void setChanged() {
    }

    @Override
    public void startOpen(PlayerEntity player) {
    }

    @Override
    public void stopOpen(PlayerEntity player) {
    }
}
