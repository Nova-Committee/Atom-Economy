package nova.committee.atom.eco.core;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.atom.eco.Static;
import nova.committee.atom.eco.api.init.event.ATMEvent;
import nova.committee.atom.eco.core.model.Account;
import nova.committee.atom.eco.core.model.UseAccount;
import nova.committee.atom.eco.init.ModConfig;
import nova.committee.atom.eco.util.UserUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/10 16:32
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AtmDataManager {

    @SubscribeEvent
    public static void onGatherAccounts(ATMEvent.GatherAccounts event) {
        event.getAccountsList().add(new UseAccount(event.getAccount()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSearchAccounts(ATMEvent.SearchAccounts event) {
        if (!contains(event.getAccountsMap(), event.getSearchedId()) && AccountDataManager.exists(event.getSearchedId())) {
            put(event.getAccountsMap(), event.getSearchedId());

        }


        for (Account account : AccountDataManager.getACCOUNTS().values()) {
            if (account.getId().contains(event.getSearchedId()) || account.getId().contains(UserUtil.getUUIDByName(event.getSearchedId()).toString())) {
                event.getAccountsMap().put(account.getId(), new UseAccount(account));
            }
        }
        if (ModConfig.COMMON.partialAccountNameSearch.get()) {
            for (String name : Static.SERVER.getPlayerNames()) {
                if (name.contains(UserUtil.getNameByUUID(UUID.fromString(event.getSearchedId()))) && !event.getAccountsMap().containsKey(UserUtil.getUUIDByName(name).toString())) {
                    Static.SERVER.getProfileCache().get(name).ifPresent(
                            gameProfile -> putIn(event.getAccountsMap(), gameProfile.getId().toString())
                    );
                }
            }
            File folder = Static.DATA_FOLDER;
            if (!folder.exists()) return;
            String str = null;
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                if (file.isDirectory() || file.isHidden()) continue;
                if (file.getName().endsWith(".json") &&
                        (str = file.getName().substring(0, file.getName().length() - 5)).toLowerCase()
                                .contains(UserUtil.getUUIDByName(event.getSearchedId()).toString())) {
                    put(event.getAccountsMap(), str);
                }
            }
        } else {
            Static.SERVER.getProfileCache().get(event.getSearchedId()).ifPresent(
                    gameProfile -> {
                        if (new File(Static.DATA_FOLDER, gameProfile.getId().toString() + ".json").exists()) {
                            put(event.getAccountsMap(), gameProfile.getId().toString());
                        } else if (new File(Static.DATA_FOLDER, event.getSearchedId() + ".json").exists()) {
                            put(event.getAccountsMap(), event.getSearchedId());
                        }
                    }
            );

        }
    }

    private static void put(HashMap<String, UseAccount> map, String id) {
        if (map.containsKey(id)) return;
        map.put(id, new UseAccount(id));
    }

    private static void putIn(HashMap<String, UseAccount> map, String id) {
        map.put(id, new UseAccount(id));
    }

    private static boolean contains(HashMap<String, UseAccount> map, String id) {
        for (UseAccount perm : map.values()) {
            if (perm.getId().equals(id)) return true;
        }
        return false;
    }
}
