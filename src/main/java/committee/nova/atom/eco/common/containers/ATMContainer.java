package committee.nova.atom.eco.common.containers;


import committee.nova.atom.eco.api.account.Account;
import committee.nova.atom.eco.api.account.AccountPermission;
import committee.nova.atom.eco.api.account.Bank;
import committee.nova.atom.eco.common.config.ConfigUtil;
import committee.nova.atom.eco.common.net.PacketHandler;
import committee.nova.atom.eco.common.net.packets.ATMOthersActionPacket;
import committee.nova.atom.eco.common.net.packets.ATMSelfActionPacket;
import committee.nova.atom.eco.common.tiles.ATMTile;
import committee.nova.atom.eco.core.AccountDataManager;
import committee.nova.atom.eco.init.ModContainers;
import committee.nova.atom.eco.init.events.ATMEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/25 10:58
 * Version: 1.0
 */
public class ATMContainer extends Container {


    public final PlayerEntity player;
    public final AccountPermission perm;
    public final Account account;
    public ArrayList<AccountPermission>  findAccount = new ArrayList<>() ;
    public ArrayList<AccountPermission>  gatherAccount = new ArrayList<>() ;
    public long inventory;
    protected ArrayList<Map.Entry<String, String>> banks;

    public ATMContainer(int id, PlayerInventory inventory, PacketBuffer buffer) {
        this(id, inventory, getTileEntity(inventory, buffer));

    }

    public ATMContainer(int id, PlayerInventory inventory, ATMTile tileEntity) {
        super(ModContainers.ATM, id);
        this.player = inventory.player;
        perm = AccountPermission.FULL;
        account = AccountDataManager.getAccount("player", player.getStringUUID(), false);
        //receiver = DataManager.getAccount("player", toId, false);

    }

    private static ATMTile getTileEntity(PlayerInventory inventory, PacketBuffer buffer) {
        final TileEntity tileAtPos = inventory.player.level.getBlockEntity(buffer.readBlockPos());
        return (ATMTile) tileAtPos;
    }

    public boolean processSelfAction(long amount, boolean deposit) {
        if (player.level.isClientSide) {
            PacketHandler.INSTANCE.sendToServer(new ATMSelfActionPacket(amount, deposit));
            return true;
        } else {
            if (amount <= 0) return false;
            String dep = deposit ? "存入" : "取款";
            Bank bank = AccountDataManager.getBank(account.getBankId(), true);
            assert bank != null;
            if (bank.processAction(deposit ? Bank.Action.DEPOSIT : Bank.Action.WITHDRAW, player, account, amount, account, false)) {
                player.sendMessage(new StringTextComponent(dep + ConfigUtil.getWorthAsString(amount, false) + " 金额执行中."), UUID.randomUUID());
                return true;
            } else {
                player.sendMessage(new StringTextComponent(dep + " 失败了."), UUID.randomUUID());
                return false;
            }
        }
    }

    public ArrayList<AccountPermission> searchAccountAction(String type, String id){
        ATMEvent.SearchAccounts event = new ATMEvent.SearchAccounts(player, type, id);
        MinecraftForge.EVENT_BUS.post(event);
        findAccount.addAll(event.getAccountsMap().values());
        return findAccount;
    }

    public ArrayList<AccountPermission> gatherAccountAction() {
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
            Account receiver = AccountDataManager.getAccount("player", id, true);
            if (amount <= 0) {
                player.sendMessage(new StringTextComponent("输入的金额不能小于等于0"), UUID.randomUUID());
                return false;
            }
            if (!perm.transfer) {
                player.sendMessage(new StringTextComponent("没有转账的权限"), UUID.randomUUID());
                return false;
            }
            if (receiver == null) {
                player.sendMessage(new StringTextComponent("请选择收款方"), UUID.randomUUID());
                return false;
            }
            Bank bank = AccountDataManager.getBank(account.getBankId(), true);
            if (bank.processAction(Bank.Action.TRANSFER, player, account, amount, receiver, false)) {
                player.sendMessage(new StringTextComponent("转账" + ConfigUtil.getWorthAsString(amount, false) + " 金额成功执行."), UUID.randomUUID());
                player.closeContainer();
                return true;
            } else {
                player.sendMessage(new StringTextComponent("转账失败"), UUID.randomUUID());
                return false;
            }
        }

    }


    @Override
    public boolean stillValid(PlayerEntity pPlayer) {
        return true;
    }
}
