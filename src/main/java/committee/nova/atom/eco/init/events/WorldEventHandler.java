package committee.nova.atom.eco.init.events;

import com.mojang.authlib.GameProfile;
import committee.nova.atom.eco.Eco;
import committee.nova.atom.eco.Static;
import committee.nova.atom.eco.api.account.Account;
import committee.nova.atom.eco.api.account.AccountPermission;
import committee.nova.atom.eco.common.config.ModConfig;
import committee.nova.atom.eco.core.AccountDataManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.util.HashMap;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/21 10:59
 * Version: 1.0
 */
@Mod.EventBusSubscriber
public class WorldEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onWorldLoad(WorldEvent.Load event){
        if(event.getWorld().isClientSide() ) return;
        Eco.loadDataManager();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onWorldUnload(WorldEvent.Unload event){
        if(event.getWorld().isClientSide()) return;
        Eco.unloadDataManager();
    }

    @SubscribeEvent
    public static void onGatherAccounts(ATMEvent.GatherAccounts event){
        event.getAccountsList().add(new AccountPermission(event.getAccount(), true, true, true, true));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSearchAccounts(ATMEvent.SearchAccounts event){
        if(!event.getSearchedType().equals("player")){
            if (!contains(event.getAccountsMap(), event.getSearchedType()) && AccountDataManager.exists(event.getSearchedType(), event.getSearchedId())) {
                put(event.getAccountsMap(), event.getSearchedType() + ":" + event.getSearchedId());
            }
            return;
        }
        for (Account account : AccountDataManager.getAccountsOfType("player").values()) {
            if (account.getId().contains(event.getSearchedId()) || account.getName().contains(event.getSearchedId())) {
                event.getAccountsMap().put(account.getTypeAndId(), new AccountPermission(account));
            }
        }
        if (ModConfig.COMMON.partialAccountNameSearch.get()) {
            for (String str : Static.getSERVER().getPlayerNames()) {
                if (str.contains(event.getSearchedId()) && !event.getAccountsMap().containsKey("player:" + str)) {
                    GameProfile gp = Static.getSERVER().getProfileCache().get(str);
                    if (gp == null) continue;
                    putIn(event.getAccountsMap(), "player:" + gp.getId().toString());
                }
            }
            File folder = new File(AccountDataManager.ACCOUNT_DIR, "player/");
            if (!folder.exists()) return;
            String str = null;
            for(File file : folder.listFiles()){
                if(file.isDirectory() || file.isHidden()) continue;
                if(file.getName().endsWith(".json") && (str = file.getName().substring(0, file.getName().length() - 5)).toLowerCase().contains(event.getSearchedId())){
                    put(event.getAccountsMap(), "player:" + str);
                }
            }
        }
        else{
            GameProfile gp = Static.getSERVER().getProfileCache().get(event.getSearchedId());
            if (gp != null && new File(AccountDataManager.ACCOUNT_DIR, "player/" + gp.getId().toString() + ".json").exists()) {
                put(event.getAccountsMap(), "player:" + gp.getId().toString());
            } else if (new File(AccountDataManager.ACCOUNT_DIR, "player/" + event.getSearchedId() + ".json").exists()) {
                put(event.getAccountsMap(), "player:" + event.getSearchedId());
            }
        }
    }

    private static void put(HashMap<String, AccountPermission> map, String id){
        if(map.containsKey(id)) return;
        map.put(id, new AccountPermission(id));
    }

    private static void putIn(HashMap<String, AccountPermission> map, String id){
        map.put(id, new AccountPermission(id));
    }

    /** 检查另一个 mod 是否已经为这种类型返回了任何值. */
    private static boolean contains(HashMap<String, AccountPermission> map, String type){
        for(AccountPermission perm : map.values()){
            if(perm.getType().equals(type)) return true;
        }
        return false;
    }
}
