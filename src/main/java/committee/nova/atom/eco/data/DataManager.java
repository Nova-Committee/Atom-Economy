package committee.nova.atom.eco.data;

import com.google.gson.JsonObject;
import committee.nova.atom.eco.Eco;
import committee.nova.atom.eco.api.Account;
import committee.nova.atom.eco.api.Bank;
import committee.nova.atom.eco.common.config.ConfigUtil;
import committee.nova.atom.eco.common.config.ModConfig;
import committee.nova.atom.eco.core.GenericBank;
import committee.nova.atom.eco.core.NullBank;
import committee.nova.atom.eco.utils.JsonUtil;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 10:06
 * Version: 1.0
 */
public class DataManager  {
    private static final Map<String, Map<String, Account>> ACCOUNTS = new ConcurrentHashMap<>(); // 类型 - uuid - 账户
    private static final Map<String, Bank> BANKS = new ConcurrentHashMap<>();
    private static final Map<String, String> BANK_NAME_CACHE = new ConcurrentHashMap<>();
    public static File ACCOUNT_DIR, BANK_DIR;
    protected static Timer timer;

    public DataManager(File file){
        timer = new Timer();
        BANK_NAME_CACHE.clear();
        ACCOUNT_DIR = new File(file, "/economy/accounts/");
        if(!ACCOUNT_DIR.exists()){ ACCOUNT_DIR.mkdirs(); }
        BANK_DIR = new File(file, "/economy/banks/");
        if(!BANK_DIR.exists()){ BANK_DIR.mkdirs(); }
        ConfigUtil.loadDefaultBanks();
        for(File bfl : BANK_DIR.listFiles()){
            if(bfl.isDirectory()) continue;
            try{
                new GenericBank(JsonUtil.get(bfl));
            }
            catch(Throwable ignored){}
        }
    }

    public static void save(Account account){
        if(account == null){ return; }
        File file = new File(ACCOUNT_DIR, account.getType() + "/" + account.getId() + ".json");
        if(!file.exists()){ file.getParentFile().mkdirs(); }
        JsonUtil.write(file, account.toJson(), true);
    }

    public static void save(Bank bank){
        if(bank == null){ return; }
        File file = new File(BANK_DIR, bank.getId() + ".json");
        if(!file.exists()){ file.getParentFile().mkdirs(); }
        JsonUtil.write(file, bank.toJson(), true);
    }

    public static void unloadAccount(Account account){
        unloadAccount(account.getType(), account.getId());
    }

    public static void unloadAccount(ResourceLocation loc){
        unloadAccount(loc.getNamespace(), loc.getPath());
    }

    public static void unloadAccount(String type, String id){
        if(ACCOUNTS.containsKey(type) && ACCOUNTS.get(type).containsKey(id)){
            try{
                save(ACCOUNTS.get(type).remove(id));
            }
            catch(Exception e){
                e.printStackTrace();
                return;
            }
        }
    }

    public static void unloadBank(String id){
        try{
            save(BANKS.remove(id));
        }
        catch(Exception e){
            e.printStackTrace();
            return;
        }
    }

    public static DataManager getInstance(){
        return Eco.CACHE;
    }

    @Nullable
    public static Account getAccount(String type, String uuid,  boolean create){
        return getAccount(type, uuid,  create, null);
    }

    @Nullable
    public static Account getAccount(String type, String uuid, boolean create, Class<? extends Account> impl){
        if(ACCOUNTS.containsKey(type) && ACCOUNTS.get(type).containsKey(uuid)){
            Account account = ACCOUNTS.get(type).get(uuid);
            return account;
        }
        return  create ? loadAccount(type, uuid, create, impl) : null;
    }

    private static Account loadAccount(String type, String uuid, boolean create, Class<? extends Account> impl){
        impl = impl == null ? Account.class : impl;
        File file = new File(ACCOUNT_DIR, type + "/" + uuid + ".json");
        if(file.exists()){
            try{
                Account account = impl.getConstructor(JsonObject.class).newInstance(JsonUtil.get(file));
                if(!account.getType().equals(type) || !account.getId().equals(uuid)){
                    throw new RuntimeException("文件中的账户数据与请求的不匹配！应该得到解决.\n" + file.getPath());
                }
                addAccount(type, account);
                return account;
            }
            catch(ReflectiveOperationException | RuntimeException e){
                e.printStackTrace();
                return null;
            }
        }
        else if(create){
            try{
                Account account = impl.getConstructor(String.class, String.class, long.class, String.class, JsonObject.class).newInstance(uuid, type, type.equals("player") ? ModConfig.COMMON.STARTING_BALANCE.get() : 0, ModConfig.Common.DEFAULT_BANK.get(), null);
                addAccount(type, account);
                Eco.LOGGER.info("创建新的账户 " +type + ":" + uuid + "!");
                return account;
            }
            catch(ReflectiveOperationException | RuntimeException e){
                e.printStackTrace();
                return null;
            }
        }
        else{
            return null;
        }
    }

    public static boolean addAccount(String type, Account account){
        if(getAccountsOfType(type) == null){
            ACCOUNTS.put(type, new ConcurrentHashMap<>());
        }
        return Objects.requireNonNull(getAccountsOfType(type)).put(account.getId(), account) == null;
    }

    public static boolean addAccount(Account account){
        return addAccount(account.getType(), account);
    }

    @Nullable
    public static Map<String, Account> getAccountsOfType(String type){
        return ACCOUNTS.get(type);
    }

    @Nullable
    public static Bank getBank(String id,  boolean create){
        return getBank(id,  create, null);
    }

    @Nullable
    public static Bank getBank(String id,  boolean create, Class<? extends Bank> impl){
        if(BANKS.containsKey(id)){
            return BANKS.get(id);
        }
        if (create){
            impl = impl == null ? GenericBank.class : impl; File file = new File(BANK_DIR, id + ".json");
            if(file.exists()){
                try{
                    Bank bank = impl.getConstructor(JsonObject.class).newInstance(JsonUtil.get(file));
                    if(!bank.getId().equals(id)){
                        throw new RuntimeException("文件中的银行数据与请求的不匹配！应该得到解决.\n" + file.getPath());
                    }
                    addBank(bank);
                    return bank;
                }
                catch(ReflectiveOperationException | RuntimeException e){
                    e.printStackTrace();
                    return NullBank.INSTANCE;
                }
            }
            else {
                try{
                    Bank bank = impl.getConstructor(String.class, String.class, long.class, JsonObject.class, TreeMap.class).newInstance(id, id.equals(ModConfig.Common.DEFAULT_BANK.get()) ? "Default Server Bank" : "Generated Bank", ModConfig.COMMON.STARTING_BALANCE.get(), null, null);
                    addBank(bank);
                    Eco.LOGGER.info("创建ID为 " + id + "的新银行.");
                    return bank;
                }
                catch(ReflectiveOperationException | RuntimeException e){
                    e.printStackTrace();
                    return NullBank.INSTANCE;
                }
            }
        }
        return NullBank.INSTANCE;
    }

    public static boolean addBank(Bank bank){
        return BANKS.put(bank.getId(), bank) == null;
    }

    @Nullable
    public static Map<String, Bank> getBanks(){
        return BANKS;
    }

    /** @param offline 显示所有帐户类型或仅显示已加载的帐户类型 */
    public static String[] getAccountTypes(boolean offline){
        if(offline){
            return ACCOUNT_DIR.list();
        }
        return ACCOUNTS.keySet().toArray(new String[0]);
    }

    public static Map<String, String> getBankNameCache(){
        return BANK_NAME_CACHE;
    }

    public static boolean exists(String type, String id){
        if(ACCOUNTS.containsKey(type) && ACCOUNTS.get(type).containsKey(id)) return true;
        File folder = new File(DataManager.ACCOUNT_DIR, type + "/");
        if(!folder.exists()) return false;
        for(File file : Objects.requireNonNull(folder.listFiles())){
            if(file.isDirectory() || file.isHidden()) continue;
            if(file.getName().endsWith(".json") && file.getName().substring(0, file.getName().length() - 5).equals(id)) return true;
        }
        return false;
    }

    public final void saveAll(){
        for(Map<String, Account> map : ACCOUNTS.values()){
            for(Account account : map.values()){
                try{ save(account); } catch(Exception e){ e.printStackTrace(); return; }
            }
        }
        for(Bank bank : BANKS.values()){
            try{ save(bank); } catch(Exception e){ e.printStackTrace(); return; }
        }
    }

    public void clearAll(){
        ACCOUNTS.clear(); BANKS.clear(); timer.cancel();
    }

}
