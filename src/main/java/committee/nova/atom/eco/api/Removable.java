package committee.nova.atom.eco.api;

import com.google.gson.JsonObject;
import committee.nova.atom.eco.utils.math.Time;


/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 9:26
 * Version: 1.0
 */
public abstract class Removable {
    private boolean temporary;
    private long last_access;

    /** 上次访问此帐户/银行的时间，用于删除临时加载的帐户/银行 */
    public long lastAccessed(){
        return temporary ? last_access : -1;
    }

    /** 更新. */
    public long updateLastAccess(){
        return last_access = temporary ? Time.getDate() : -1;
    }

    public boolean isTemporary(){
        return temporary;
    }

    /** 设置为“临时加载”，以便在下次检查非活动的帐户/银行时将其删除.*/
    public <T extends Removable> T setTemporary(boolean value){
        this.temporary = value;
        this.updateLastAccess();
        return (T)this;
    }

    public abstract JsonObject toJson();
}
