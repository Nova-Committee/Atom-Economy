package nova.committee.atom.eco.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import nova.committee.atom.eco.common.item.WalletItem;
import nova.committee.atom.eco.init.ModConfig;
import nova.committee.atom.eco.util.text.StringUtil;

import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/10 14:46
 * Version: 1.0
 */
public class WalletUtil {

    /**
     * 用于寻找玩家的钱包. 如果有多个，按照先后顺序操作
     */
    public static ItemStack getCurrentWalletStack(Player player) {

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
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {

            ItemStack stack = player.getInventory().getItem(i);

            if (stack.getItem() instanceof WalletItem) {
                return stack;
            }
        }

        //没有钱包
        return ItemStack.EMPTY;
    }

    public static String printCurrency(long amount) {
        return StringUtil.printCommas(amount) + ModConfig.COMMON.currencySign.get();
    }

    public static void addCurrencyLore(List<Component> tooltip, long currentCurrency, long maxCurrency) {
        tooltip.add(new TextComponent(ChatFormatting.GRAY + "余额: " + ChatFormatting.GOLD + printCurrency(currentCurrency) + (maxCurrency != 0 ? (" / " + printCurrency(maxCurrency)) : "")));
    }


}
