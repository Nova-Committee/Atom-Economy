package committee.nova.atom.eco.api;


import committee.nova.atom.eco.data.DataManager;
import committee.nova.atom.eco.utils.JsonUtil;
import net.minecraft.nbt.CompoundNBT;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 10:48
 * Version: 1.0
 */
public class AccountPermission {
    public static final AccountPermission FULL = new AccountPermission((String)null, true, true, true, true);
    public final String account_id;
    public final boolean withdraw, deposit, transfer, manage;
    protected Account account;
    //public final long limit;

    public AccountPermission(String accid, boolean wd, boolean dp, boolean tr, boolean mg){
        this.account_id = accid;
        this.withdraw = wd;
        this.deposit = dp;
        this.transfer = tr;
        this.manage = mg;
    }

    public AccountPermission(Account account, boolean wd, boolean dp, boolean tr, boolean mg){
        this(account.getId(), wd, dp, tr, mg);
        this.account = account;
    }

    public AccountPermission(String accid){
        this(accid, false, false, false, false);
    }

    public AccountPermission(Account account){
        this(account.getId());
        this.account = account;
    }

    public AccountPermission(CompoundNBT compound){
        account = new Account(JsonUtil.getObjectFromString(compound.getString("a")));
        account_id = account.getId();
        withdraw = compound.getBoolean("w");
        deposit = compound.getBoolean("d");
        transfer = compound.getBoolean("t");
        manage = compound.getBoolean("m");
    }

    public Account getAccount(){
        if(account == null){
            account = DataManager.getAccount(account_id.split(":")[0], account_id.split(":")[1], true);
        }
        return account;
    }

    public CompoundNBT toNBT(){
        CompoundNBT com = new CompoundNBT();
        com.putString("a", getAccount().toJson().toString());
        com.putBoolean("w", withdraw);
        com.putBoolean("d", deposit);
        com.putBoolean("t", transfer);
        com.putBoolean("m", manage);
        return com;
    }

    public String getType(){
        return account == null ? account_id.split(":")[0] : account.getType();
    }

    public String getId(){
        return account == null ? account_id.split(":")[1] : account.getId();
    }

}
