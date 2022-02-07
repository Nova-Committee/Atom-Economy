package committee.nova.atom.eco.init.events;

import committee.nova.atom.eco.api.Account;
import net.minecraftforge.eventbus.api.Event;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 9:51
 * Version: 1.0
 */
public class AccountEvent extends Event {
    private final Account account;

    private AccountEvent(Account account){
        this.account = account;
    }

    public Account getAccount(){
        return account;
    }

    public static class BalanceUpdated extends AccountEvent {

        private long old_balance, new_balance;

        public BalanceUpdated(Account account, long oldbal, long newbal){
            super(account); old_balance = oldbal; new_balance = newbal;
        }

        public long getOldBalance(){
            return old_balance;
        }

        public long getNewBalance(){
            return new_balance;
        }

    }
}
