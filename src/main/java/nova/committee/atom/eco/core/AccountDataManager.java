package nova.committee.atom.eco.core;

import com.google.gson.JsonObject;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.atom.eco.Static;
import nova.committee.atom.eco.core.model.Account;
import nova.committee.atom.eco.init.ModConfig;
import nova.committee.atom.eco.util.FileUtil;
import nova.committee.atom.eco.util.JsonUtil;
import nova.committee.atom.eco.util.text.LogUtil;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/10 9:56
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AccountDataManager {
    private static final Map<String, Account> ACCOUNTS = new ConcurrentHashMap<>(); // uuid - 账户


    public static Map<String, Account> getACCOUNTS() {
        return ACCOUNTS;
    }

    public static Account getAccountById(String id) {
        return ACCOUNTS.get(id);
    }

    public static boolean exists(String id) {
        if (ACCOUNTS.containsKey(id)) return true;
        File folder = Static.DATA_FOLDER;
        if (!folder.exists()) return false;
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory() || file.isHidden()) continue;
            if (file.getName().endsWith(".json") && file.getName().substring(0, file.getName().length() - 5).equals(id))
                return true;
        }
        return false;
    }

    @Nullable
    public static Account getAccount(String uuid, boolean create) {
        return getAccount(uuid, create, null);
    }

    @Nullable
    public static Account getAccount(String uuid, boolean create, Class<? extends Account> impl) {
        if (ACCOUNTS.containsKey(uuid)) {
            Account account = ACCOUNTS.get(uuid);
            return account;
        }
        return create ? loadAccount(uuid, create, impl) : null;
    }


    public static boolean addAccount(String id, Account account) {
        if (!ACCOUNTS.containsKey(id)) {
            ACCOUNTS.put(id, account);
            return true;
        }
        return false;
    }

    private static void loadAccount(File file) {
        File[] list = file.listFiles();
        Class<? extends Account> impl = Account.class;
        if (list != null)
            for (File accountFile : list) {
                String str = accountFile.getName().substring(0, accountFile.getName().lastIndexOf("."));
                Pattern r = Pattern.compile("[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}");
                Matcher m = r.matcher(str);
                if (m.find()) {
                    try {
                        Account account = impl.getConstructor(JsonObject.class).newInstance(JsonUtil.get(accountFile));
                        addAccount(account.getId(), account);
                    } catch (ReflectiveOperationException | RuntimeException e) {
                        e.printStackTrace();
                    }
                } else {
                    Static.LOGGER.error("未找到账户数据！");
                }

            }
    }

    private static Account loadAccount(String uuid, boolean create, Class<? extends Account> impl) {
        impl = impl == null ? Account.class : impl;
        File file = new File(Static.DATA_FOLDER, uuid + ".json");
        if (file.exists()) {
            try {
                Account account = impl.getConstructor(JsonObject.class).newInstance(JsonUtil.get(file));
                if (!account.getId().equals(uuid)) {
                    throw new RuntimeException("文件中的账户数据与请求的不匹配！应该得到解决.\n" + file.getPath());
                }
                addAccount(uuid, account);
                return account;
            } catch (ReflectiveOperationException | RuntimeException e) {
                e.printStackTrace();
                return null;
            }
        } else if (create) {
            try {
                Account account = impl.getConstructor(String.class, String.class, long.class, JsonObject.class).newInstance(uuid, "player", ModConfig.COMMON.startingBalance.get(), null);
                addAccount(uuid, account);
                Static.LOGGER.info("创建新的账户 " + uuid + "!");
                return account;
            } catch (ReflectiveOperationException | RuntimeException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }


    public static void unloadAccount(String id) {
        if (ACCOUNTS.containsKey(id)) {
            try {
                save(ACCOUNTS.get(id));
                ACCOUNTS.remove(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadDataManager() {
        Static.DATA_FOLDER = new File(Static.ECO_FOLDER, "data/");
        FileUtil.Check(Static.DATA_FOLDER);
        if (!ACCOUNTS.isEmpty()) {
            saveAll();
            clearAll();
        }

        loadAccount(Static.DATA_FOLDER);

    }

    public static void unloadDataManager() {
        LogUtil.debug("UN-LOADING ATOMECO DATAMANAGER");
        if (!ACCOUNTS.isEmpty()) {
            saveAll();
            clearAll();
        }

    }

    public static void saveAll() {
        for (Account account : ACCOUNTS.values()) {
            try {
                save(account);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public static void clearAll() {
        ACCOUNTS.clear();
    }

    public static void save(Account account) {
        if (account == null) {
            return;
        }
        File file = new File(Static.DATA_FOLDER, account.getId() + ".json");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        JsonUtil.write(file, account.toJson(), true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld().isClientSide()) return;
        loadDataManager();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isClientSide()) return;
        unloadDataManager();
    }

}
