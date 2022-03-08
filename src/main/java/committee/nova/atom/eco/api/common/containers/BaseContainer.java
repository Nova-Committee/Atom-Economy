package committee.nova.atom.eco.api.common.containers;

import committee.nova.atom.eco.api.common.tiles.AtomItemHandler;
import committee.nova.atom.eco.api.common.tiles.BaseInventoryTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Objects;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/9 21:52
 * Version: 1.0
 */
public class BaseContainer extends Container {
    public final BaseInventoryTile tileEntity;
    protected final PlayerInventory playerInventory;
    protected int size;

    protected boolean isItemContainer;

    protected BaseContainer(ContainerType<?> type, int windowId, PlayerInventory playerInventory, BaseInventoryTile tileEntity, int x, int y) {
        super(type, windowId);

        this.playerInventory = playerInventory;
        this.tileEntity = tileEntity;

        addPlayerInv(x, y);
        addPlayerHotbar(x, y + 58);

        //升级
//        if (tileEntity instanceof TileEntityUpgradable) {
//
//            TileEntityUpgradable tileEntityProgress = (TileEntityUpgradable) tileEntity;
//            trackInt(new FunctionalIntReferenceHolder(tileEntityProgress::getCurrentProgress, tileEntityProgress::setCurrentProgress));
//        }
    }

    /**
     * 获取绑定的tile
     */
    protected static BaseInventoryTile getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {

        Objects.requireNonNull(playerInventory, "玩家背包不能为空");
        Objects.requireNonNull(data, "数据不能为空");

        final TileEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());

        if (tileAtPos instanceof BaseInventoryTile) {
            return (BaseInventoryTile) tileAtPos;
        }

        throw new IllegalStateException("实体错误!" + tileAtPos);
    }

    /**
     * 获取tile的格子数量
     */
    private int getTileEntitySlotAmount() {
        return isItemContainer ? size : tileEntity.getSizeInventory();
    }

    /**
     * 添加玩家背包格子
     */
    private void addPlayerInv(int x, int y) {
        addStorageInv(playerInventory, 9, x, y, 3);
    }

    /**
     * 添加玩家的槽
     */
    private void addPlayerHotbar(int x, int y) {
        addStorageInv(playerInventory, 0, x, y, 1);
    }

    /**
     * 用以给 addPlayerInv & addPlayerHotbar 添加插槽
     */
    private void addStorageInv(IInventory inv, int idOffset, int x, int y, int height) {

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inv, j + i * 9 + idOffset, x + (j * 18), y + (i * 18)));
            }
        }
    }

    /**
     * 用来添加实体可以储存的格子
     */
    protected void addTileEntityStorageInv(AtomItemHandler inv, int idOffset, int x, int y, int height) {

        int id = idOffset;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new SlotItemHandler(inv, id, x + (j * 18), y + (i * 18)));
                id++;
            }
        }
    }


    /**
     * 处理物品栈当按下shift时的行为
     */
    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {

        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        //检查是否已经有同样的物品
        if (slot != null && slot.hasItem()) {

            ItemStack itemStack1 = slot.getItem();
            itemstack = itemStack1.copy();

            //检查inventory是否有格子
            if (getTileEntitySlotAmount() > 0) {

                //传输: 玩家背包到新的inventory.
                if (index <= 35) {

                    if (mergeIfPossible(slot, itemStack1, itemstack, 36, 36 + getTileEntitySlotAmount())) {

                        if (mergeInvHotbarIfPossible(slot, itemStack1, itemstack, index)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }

                //传输:新的inventory到玩家背包.
                else {

                    if (mergeIfPossible(slot, itemStack1, itemstack, 0, 35)) {
                        return ItemStack.EMPTY;
                    }

                    slot.onQuickCraft(itemStack1, itemstack);
                }
            } else {

                if (mergeInvHotbarIfPossible(slot, itemStack1, itemstack, index)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemStack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else slot.setChanged();

            if (itemStack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerInventory.player, itemStack1);
        }

        return itemstack;
    }

    /**
     * 检查合并物品栈是否可能
     */
    private boolean mergeIfPossible(Slot slot, ItemStack is, ItemStack is2, int id, int maxId) {

        if (!this.moveItemStackTo(is, id, maxId, false)) {
            return true;
        }

        slot.onQuickCraft(is, is2);
        return false;
    }

    /**
     * 处理传输: 玩家背包 <-> 快捷栏.
     */
    private boolean mergeInvHotbarIfPossible(Slot slot, ItemStack is, ItemStack is2, int id) {

        //传输:玩家背包 -> 快捷栏.
        if (id < 27) {

            if (mergeIfPossible(slot, is, is2, 27, 35)) {
                return true;
            }
        }

        //传输: 快捷栏 -> 玩家背包.
        else {

            if (mergeIfPossible(slot, is, is2, 0, 26)) {
                return true;
            }
        }

        slot.onQuickCraft(is, is2);
        return false;
    }


    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return true;
    }
}
