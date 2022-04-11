package nova.committee.atom.eco.api.init.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import nova.committee.atom.eco.core.AccountDataManager;
import nova.committee.atom.eco.core.model.Account;
import nova.committee.atom.eco.core.model.UseAccount;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/21 11:05
 * Version: 1.0
 */
public class ATMEvent extends Event {
    private final Player player;
    private final Account account;

    private ATMEvent(Player player) {
        this.player = player;
        this.account = AccountDataManager.getAccount(player.getStringUUID(), false);
    }

    public Player getPlayer() {
        return player;
    }

    public Account getAccount() {
        return account;
    }


    /**
     * 事件，以便其他模组可以添加账户到这个玩家可管理的列表中。
     */
    public static class GatherAccounts extends ATMEvent {

        private final ArrayList<UseAccount> accounts = new ArrayList<>();

        public GatherAccounts(Player player) {
            super(player);
        }

        public ArrayList<UseAccount> getAccountsList() {
            return accounts;
        }

    }

    /**
     * 事件，以便其他模组可以添加搜索结果
     */
    public static class SearchAccounts extends ATMEvent {

        private final HashMap<String, UseAccount> accounts = new HashMap<>();
        private final String id;

        /**
         * 搜索指定账号事件
         * 账号名 type:id
         *
         * @param player 当前玩家
         * @param id     账号id
         */
        public SearchAccounts(Player player, String id) {
            super(player);
            this.id = id;
        }

        public HashMap<String, UseAccount> getAccountsMap() {
            return accounts;
        }

        public String getSearchedId() {
            return id;
        }

    }
}
