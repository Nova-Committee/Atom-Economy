package nova.committee.atom.eco.api.core.account;

import net.minecraft.commands.CommandSource;
import nova.committee.atom.eco.core.model.Account;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 9:23
 * Version: 1.0
 */
public interface Manageable {
    public boolean modifyBalance(Action action, long amount, CommandSource source);

    public boolean processBankAction(Action action, CommandSource log, Account sender, Account receiver, long amount);

    public static enum Action {
        SELF_SET, SELF_ADD, SELF_SUB,
        DEPOSIT, WITHDRAW, TRANSFER
    }
}
