package committee.nova.atom.eco.init.events;

import committee.nova.atom.eco.api.account.Account;
import committee.nova.atom.eco.api.account.AccountPermission;
import committee.nova.atom.eco.api.account.Bank;
import committee.nova.atom.eco.core.AccountDataManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/21 11:05
 * Version: 1.0
 */
public class ATMEvent extends Event {
    private final PlayerEntity player;
    private final Account account;
    private Bank bank;

    private ATMEvent(PlayerEntity player){
        this.player = player;
        this.account = AccountDataManager.getAccount("player", player.getStringUUID(), false);
    }

    public PlayerEntity getPlayer(){
        return player;
    }

    public Account getAccount(){
        return account;
    }

    public Bank getBank(){
        if (bank == null) bank = AccountDataManager.getBank(account.getBankId(), true);
        return bank;
    }

    /** 事件，以便其他模组可以添加账户到这个玩家可管理的列表中。 */
    public static class GatherAccounts extends ATMEvent {

        private ArrayList<AccountPermission> accounts = new ArrayList<>();

        public GatherAccounts(PlayerEntity player){
            super(player);
        }

        public ArrayList<AccountPermission> getAccountsList(){
            return accounts;
        }

    }

    /** 事件，以便其他模组可以添加搜索结果 */
    public static class SearchAccounts extends ATMEvent {

        private HashMap<String, AccountPermission> accounts = new HashMap<>();
        private String type, id;

        /**
         * 搜索指定账号事件
         * 账号名 type:id
         * @param player 当前玩家
         * @param type 需要搜索的账号类型
         * @param id 账号id
         */
        public SearchAccounts(PlayerEntity player, String type, String id) {
            super(player);
            this.type = type;
            this.id = id;
        }

        public HashMap<String, AccountPermission> getAccountsMap() {
            return accounts;
        }

        public String getSearchedType() {
            return type;
        }

        public String getSearchedId() {
            return id;
        }

    }
}
