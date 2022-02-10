package committee.nova.atom.eco.common.containers;

import committee.nova.atom.eco.api.common.containers.BaseContainer;
import committee.nova.atom.eco.api.common.containers.slots.SlotIInventoryFilter;
import committee.nova.atom.eco.common.config.ConfigUtil;
import committee.nova.atom.eco.common.items.GenericMoneyItem;
import committee.nova.atom.eco.init.ModContainers;
import committee.nova.atom.eco.utils.CurrencyUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/10 7:15
 * Version: 1.0
 */
public class WalletContainer extends BaseContainer {
    private final IInventory stackInv;

    public WalletContainer(final int windowID, final PlayerInventory playerInventory, IInventory stackInv) {
        super(ModContainers.WALLET, windowID, playerInventory, null, 8, 94);

        isItemContainer = true;
        size = 1;

        this.stackInv = stackInv;

        //New Inventory
        addSlot(new SlotIInventoryFilter(stackInv, 0, 17, 42, ConfigUtil.MONEY));
    }

    //注册用
    public static WalletContainer createClientWallet(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        data.readVarInt();
        return new WalletContainer(windowId, playerInventory, new Inventory(1));
    }

    private ItemStack getCurrentWalletStack() {
        return CurrencyUtil.getCurrentWalletStack(playerInventory.player);
    }

    /**
     * 当个子被触发时调用
     * 用于给钱包添加钱
     */
    @Override
    public ItemStack clicked(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {

        ItemStack returnStack = super.clicked(slotId, dragType, clickTypeIn, player);
        ItemStack stackInInv = stackInv.getItem(0);
        ItemStack walletStack = getCurrentWalletStack();

        //检测在钱包格子中的物品
        if (stackInInv.getItem() instanceof GenericMoneyItem) {

            GenericMoneyItem currency = ((GenericMoneyItem) stackInInv.getItem());

            int amountToAdd = 0;
            int stacksToRemove = 0;

            //遍历堆栈数。例如：32 的堆栈将迭代 32 次。
            for (int i = 0; i < stackInInv.getCount(); i++) {

                //检测是否能处理
                if (CurrencyUtil.canDepositToWallet(walletStack, (int) currency.getWorth())) {
                    amountToAdd += currency.getWorth();
                    stacksToRemove++;
                } else break;
            }

            CurrencyUtil.depositToWallet(walletStack, amountToAdd);
            stackInv.removeItem(0, stacksToRemove);
        }

        return returnStack;
    }


    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
    }

    @OnlyIn(Dist.CLIENT)
    public int getSize() {
        return size;
    }
}
