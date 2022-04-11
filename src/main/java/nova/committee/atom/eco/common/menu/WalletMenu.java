package nova.committee.atom.eco.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import nova.committee.atom.eco.api.core.money.IMoney;
import nova.committee.atom.eco.common.item.WalletItem;
import nova.committee.atom.eco.core.ConfigDataManager;
import nova.committee.atom.eco.init.registry.ModMenus;
import nova.committee.atom.eco.util.WalletUtil;
import nova.committee.atom.eco.util.text.LogUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/10 0:00
 * Version: 1.0
 */
public class WalletMenu extends AbstractContainerMenu {

    private final Inventory playerInventory;
    private final Container walletContainer;

    private final int SLOT_LENGTH = 18, START_X = 8;

    protected WalletMenu(int id, Inventory playerInventory, Container walletContainer) {
        super(ModMenus.WALLET, id);
        this.playerInventory = playerInventory;
        this.walletContainer = walletContainer;
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(walletContainer, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 161));
        }
    }


    public static WalletMenu getClientSideInstance(int id, Inventory playerInventory, FriendlyByteBuf data) {
        return new WalletMenu(id, playerInventory, new SimpleContainer(27));
    }

    public static WalletMenu getServerSideInstance(int id, Inventory playerInventory, ItemStack stack) {
        return new WalletMenu(id, playerInventory, WalletItem.getInventory(stack));
    }


    private ItemStack getCurrentWalletStack() {
        return WalletUtil.getCurrentWalletStack(playerInventory.player);
    }

    public long countInWallet() {
        long value = 0L;
        for (int i = 0; i <= walletContainer.getContainerSize(); i++) {
            if (walletContainer.getItem(i).isEmpty()) continue;
            long worth = ConfigDataManager.getItemStackWorth(walletContainer.getItem(i));
            LogUtil.debug(walletContainer.getItem(i).toString(), walletContainer.getItem(i).getItem() instanceof IMoney.Item ? ((IMoney.Item) walletContainer.getItem(i).getItem()).getType().toString() : "不是合法的货币");
            value += worth * walletContainer.getItem(i).getCount();
        }
        return value;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 27) {
                if (!this.moveItemStackTo(itemstack1, 27, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 27, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }


}
