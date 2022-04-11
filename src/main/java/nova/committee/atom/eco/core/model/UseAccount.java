package nova.committee.atom.eco.core.model;

import net.minecraft.nbt.CompoundTag;
import nova.committee.atom.eco.core.AccountDataManager;
import nova.committee.atom.eco.util.JsonUtil;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/10 16:35
 * Version: 1.0
 */
public class UseAccount {

    public static final UseAccount FULL = new UseAccount((String) null);
    public final String account_id;
    //public final boolean withdraw, deposit, transfer, manage;
    protected Account account;
    //public final long limit;

    public UseAccount(String accid) {
        this.account_id = accid;
    }

    public UseAccount(Account account) {
        this(account.getId());
        this.account = account;
    }

    public UseAccount(CompoundTag compound) {
        account = new Account(JsonUtil.getObjectFromString(compound.getString("account")));
        account_id = account.getId();
    }

    public Account getAccount() {
        if (account == null) {
            account = AccountDataManager.getAccount(account_id, true);
        }
        return account;
    }

    public CompoundTag toNBT() {
        CompoundTag com = new CompoundTag();
        com.putString("account", getAccount().toJson().toString());

        return com;
    }

    public String getId() {
        return account == null ? account_id : account.getId();
    }
}
