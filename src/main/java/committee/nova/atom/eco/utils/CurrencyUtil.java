package committee.nova.atom.eco.utils;

import committee.nova.atom.eco.common.config.ModConfig;
import committee.nova.atom.eco.common.items.WalletItem;
import committee.nova.atom.eco.utils.text.StringUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/10 7:27
 * Version: 1.0
 */
public class CurrencyUtil {
    /**
     * 用于寻找玩家的钱包. 如果有多个，按照先后顺序操作
     */
    public static ItemStack getCurrentWalletStack(PlayerEntity player) {

        //级别 #1 - Held mainhand.
        if (player.getMainHandItem().getItem() instanceof WalletItem) {
            return player.getMainHandItem();
        }

        //级别 #2 - Held offhand.
        if (player.getOffhandItem().getItem() instanceof WalletItem) {
            return player.getOffhandItem();
        }

        //级别 #3 - Curios slot.
//        if (CalemiUtils.curiosLoaded) {
//
//            if (CuriosApi.getCuriosHelper().findEquippedCurio(ModItems.WALLET.get(), player).isPresent()) {
//                return CuriosApi.getCuriosHelper().findEquippedCurio(ModItems.WALLET.get(), player).get().right;
//            }
//        }

        //级别 #4 - Inventory (lowest slot id wins).
        for (int i = 0; i < player.inventory.getContainerSize(); i++) {

            ItemStack stack = player.inventory.getItem(i);

            if (stack.getItem() instanceof WalletItem) {
                return stack;
            }
        }

        //没有钱包
        return ItemStack.EMPTY;
    }


    /**
     * 钱包的操作
     */

    public static boolean canDepositToWallet(ItemStack walletStack, int depositAmount) {

        if (walletStack.getItem() instanceof WalletItem) {
            return WalletItem.getBalance(walletStack) + depositAmount <= ModConfig.COMMON.walletCurrencyCapacity.get();
        }

        return false;
    }

    public static boolean canWithdrawFromWallet(ItemStack walletStack, int withdrawAmount) {

        if (walletStack.getItem() instanceof WalletItem) {
            return WalletItem.getBalance(walletStack) >= withdrawAmount;
        }

        return false;
    }

    public static void depositToWallet(ItemStack walletStack, int depositAmount) {

        if (walletStack.getItem() instanceof WalletItem) {
            WalletItem.depositCurrency(walletStack, depositAmount);
        }
    }

    public static void withdrawFromWallet(ItemStack walletStack, int withdrawAmount) {

        if (walletStack.getItem() instanceof WalletItem) {
            WalletItem.withdrawCurrency(walletStack, withdrawAmount);
        }
    }

    public static String printCurrency(int amount) {
        return StringUtil.printCommas(amount) + ModConfig.COMMON.currencySign.get();
    }

    public static void addCurrencyLore(List<ITextComponent> tooltip, int currentCurrency) {
        addCurrencyLore(tooltip, currentCurrency, 0);
    }

    public static void addCurrencyLore(List<ITextComponent> tooltip, int currentCurrency, int maxCurrency) {
        tooltip.add(new StringTextComponent(TextFormatting.GRAY + "余额: " + TextFormatting.GOLD + printCurrency(currentCurrency) + (maxCurrency != 0 ? (" / " + printCurrency(maxCurrency)) : "")));
    }
}
