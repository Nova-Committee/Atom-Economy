package nova.committee.atom.eco.api.init.event;

import net.minecraftforge.eventbus.api.Event;
import nova.committee.atom.eco.core.model.Account;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/10 9:48
 * Version: 1.0
 */
public class AccountEvent extends Event {
    private final Account account;

    private AccountEvent(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public static class BalanceUpdated extends AccountEvent {

        private final long old_balance;
        private final long new_balance;

        public BalanceUpdated(Account account, long oldbal, long newbal) {
            super(account);
            old_balance = oldbal;
            new_balance = newbal;
        }

        public long getOldBalance() {
            return old_balance;
        }

        public long getNewBalance() {
            return new_balance;
        }

    }

}
