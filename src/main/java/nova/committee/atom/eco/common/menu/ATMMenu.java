package nova.committee.atom.eco.common.menu;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.common.MinecraftForge;
import nova.committee.atom.eco.api.core.account.Manageable;
import nova.committee.atom.eco.api.init.event.ATMEvent;
import nova.committee.atom.eco.common.net.ATMOthersActionPacket;
import nova.committee.atom.eco.common.net.ATMSelfActionPacket;
import nova.committee.atom.eco.core.AccountDataManager;
import nova.committee.atom.eco.core.model.Account;
import nova.committee.atom.eco.core.model.UseAccount;
import nova.committee.atom.eco.init.handler.PacketHandler;
import nova.committee.atom.eco.init.registry.ModMenus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/10 16:29
 * Version: 1.0
 */
public class ATMMenu extends AbstractContainerMenu {

    public final Player player;
    public final UseAccount useAccount;
    public final Account account;
    public ArrayList<UseAccount> findAccount = new ArrayList<>();
    public ArrayList<UseAccount> gatherAccount = new ArrayList<>();

    protected ATMMenu(int id, Inventory playerInv) {
        super(ModMenus.ATM, id);
        this.player = playerInv.player;
        useAccount = UseAccount.FULL;
        account = AccountDataManager.getAccount(player.getStringUUID(), true);
    }

    public static ATMMenu getClientSideInstance(int id, Inventory playerInventory, FriendlyByteBuf data) {
        return new ATMMenu(id, playerInventory);
    }

    public static ATMMenu getServerSideInstance(int id, Inventory playerInventory) {
        return new ATMMenu(id, playerInventory);
    }

    public boolean processSelfAction(long amount, boolean deposit) {
        if (player.level.isClientSide) {
            PacketHandler.INSTANCE.sendToServer(new ATMSelfActionPacket(amount, deposit));
            return true;
        } else {
            if (amount <= 0) return false;
            String dep = deposit ? "存入" : "取款";
            if (account.processBankAction(deposit ? Manageable.Action.DEPOSIT : Manageable.Action.WITHDRAW, player, account, account, amount)) {
                player.sendMessage(new TextComponent(ChatFormatting.BLUE + dep + " " + ChatFormatting.RED + amount + "" + ChatFormatting.GREEN + "金额成功."), UUID.randomUUID());
                return true;
            } else {
                player.sendMessage(new TextComponent(ChatFormatting.BLUE + dep + " " + ChatFormatting.DARK_GRAY + "失败了."), UUID.randomUUID());
                return false;
            }
        }
    }

    public ArrayList<UseAccount> searchAccountAction(String id) {
        ATMEvent.SearchAccounts event = new ATMEvent.SearchAccounts(player, id);
        MinecraftForge.EVENT_BUS.post(event);
        findAccount.addAll(event.getAccountsMap().values());
        return findAccount;
    }

    public UseAccount getAccountAction(String id) {
        ATMEvent.SearchAccounts event = new ATMEvent.SearchAccounts(player, id);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getAccountsMap().get(id);
    }

    public ArrayList<UseAccount> gatherAccountAction() {
        ATMEvent.GatherAccounts event = new ATMEvent.GatherAccounts(player);
        MinecraftForge.EVENT_BUS.post(event);
        gatherAccount.addAll(event.getAccountsList());
        return gatherAccount;
    }

    public boolean processOthersAction(String id, long amount) {
        if (player.level.isClientSide) {
            PacketHandler.INSTANCE.sendToServer(new ATMOthersActionPacket(id, amount));
            return true;
        } else {
            Account receiver = AccountDataManager.getAccount(id, true);
            if (amount <= 0) {
                player.sendMessage(new TextComponent("输入的金额不能小于等于0"), UUID.randomUUID());
                return false;
            }

            if (receiver == null) {
                player.sendMessage(new TextComponent("请选择收款方"), UUID.randomUUID());
                return false;
            }
            if (account.processBankAction(Manageable.Action.TRANSFER, player, account, receiver, amount)
            ) {
                player.sendMessage(new TextComponent(ChatFormatting.BLUE + "转账 " + ChatFormatting.RED + amount + "" + ChatFormatting.GREEN + "金额成功."), UUID.randomUUID());
                player.closeContainer();
                return true;
            } else {
                player.sendMessage(new TextComponent(ChatFormatting.BLUE + "转账 " + ChatFormatting.DARK_GRAY + "失败了."), UUID.randomUUID());
                return false;
            }
        }

    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}

