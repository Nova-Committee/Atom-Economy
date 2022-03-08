package committee.nova.atom.eco.api.common.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/9 21:53
 * Version: 1.0
 */
public abstract class BaseInventoryTile extends MoneyTile implements ITileEntityGuiHandler, INamedContainerProvider {
    public final List<Slot> containerSlots = new ArrayList<>();
    private final AtomItemHandler inventory;
    private ITextComponent customName;

    public BaseInventoryTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);

        this.inventory = new AtomItemHandler(getSizeInventory(), containerSlots);

        for (int i = 0; i < inventory.getSlots(); i++) {
            containerSlots.add(null);
        }
    }

    public abstract ITextComponent getDefaultName();

    public abstract int getSizeInventory();

    public AtomItemHandler getInventory() {
        return this.inventory;
    }

    public void setCustomName(ITextComponent name) {
        this.customName = name;
    }

    public ITextComponent getCurrentName() {
        return customName != null ? customName : getDefaultName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return getCurrentName();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this.inventory));
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
        return getTileContainer(id, playerInv);
    }

    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return containerSlots.get(index) != null && containerSlots.get(index).mayPlace(stack);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);

        if (nbt.contains("CustomName", Constants.NBT.TAG_STRING)) {
            this.customName = ITextComponent.Serializer.fromJson(nbt.getString("CustomName"));
        }

        NonNullList<ItemStack> inv = NonNullList.withSize(this.inventory.getSlots(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbt, inv);
        this.inventory.setNonNullList(inv);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {

        if (this.customName != null) {
            nbt.putString("CustomName", ITextComponent.Serializer.toJson(customName));
        }

        ItemStackHelper.saveAllItems(nbt, this.inventory.toNonNullList());
        return super.save(nbt);
    }
}
