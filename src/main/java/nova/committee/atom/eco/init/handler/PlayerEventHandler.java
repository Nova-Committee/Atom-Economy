package nova.committee.atom.eco.init.handler;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.atom.eco.core.AccountDataManager;
import nova.committee.atom.eco.core.ConfigDataManager;
import nova.committee.atom.eco.core.MoneyItemManager;
import nova.committee.atom.eco.core.model.Account;
import nova.committee.atom.eco.init.ModConfig;
import nova.committee.atom.eco.util.text.FormatUtil;
import nova.committee.atom.eco.util.text.LogUtil;

import java.util.UUID;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 11:50
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEventHandler {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {

        if (event.getPlayer().getCommandSenderWorld().isClientSide) {
            return;
        }
        LogUtil.debug("加载 " + event.getPlayer().getName() + " 的账户|| " + event.getPlayer().getGameProfile().getId().toString());
        Account account = AccountDataManager.getAccount(event.getPlayer().getStringUUID(), true);
        if (ModConfig.COMMON.notifyBalanceOnJoin.get() && account != null) {
            event.getPlayer().sendMessage(new TextComponent("银行余额: " + account.getBalance()).withStyle(ChatFormatting.BLUE), UUID.randomUUID());
            event.getPlayer().sendMessage(new TextComponent("身上余额: " + MoneyItemManager.countInInventory(event.getPlayer())).withStyle(ChatFormatting.DARK_GREEN), UUID.randomUUID());

        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        LogUtil.debug("保存 " + event.getPlayer().getName() + "的账户|| " + event.getPlayer().getGameProfile().getId().toString());
        AccountDataManager.unloadAccount(event.getPlayer().getGameProfile().getId().toString());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!ModConfig.COMMON.showItemWorthInTooltip.get()) return;
        long worth = ConfigDataManager.getItemStackWorth(event.getItemStack());
        if (worth <= 0) return;
        String str = "&9单价:&7" + worth;
        if (event.getItemStack().getCount() > 1) {
            str += " &8(&6总价值: &7" + worth * event.getItemStack().getCount() + "&8)";
        }
        event.getToolTip().add(new TextComponent(FormatUtil.format(str)));
    }
}
