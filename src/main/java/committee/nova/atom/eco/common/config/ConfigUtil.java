package committee.nova.atom.eco.common.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import committee.nova.atom.eco.Eco;
import committee.nova.atom.eco.api.Money;
import committee.nova.atom.eco.common.items.GenericMoneyItem;
import committee.nova.atom.eco.core.GenericBank;
import committee.nova.atom.eco.core.GenericMoney;
import committee.nova.atom.eco.data.DataManager;
import committee.nova.atom.eco.utils.ItemUtil;
import committee.nova.atom.eco.utils.JsonUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.File;
import java.util.TreeMap;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 15:03
 * Version: 1.0
 */
public class ConfigUtil {
    private static final TreeMap<String, Long> DEFAULT = new TreeMap<String, Long>();
    private static JsonArray DEF_BANKS;
    private static Item MONEY;
    private static TreeMap<ResourceLocation, Long> EXTERNAL_ITEMS = new TreeMap<>();
    private static TreeMap<String, Long> EXTERNAL_ITEMS_METAWORTH = new TreeMap<>();

    static {
        DEFAULT.put("1yuan", 1000L);
        DEFAULT.put("2yuan", 2000L);
        DEFAULT.put("5yuan", 5000L);
        DEFAULT.put("10yuan", 10000L);
        DEFAULT.put("20yuan", 20000L);
        DEFAULT.put("50yuan", 50000L);
        DEFAULT.put("100yuan", 100000L);
        DEFAULT.put("200yuan", 200000L);
        DEFAULT.put("500yuan", 500000L);
        DEFAULT.put("1000yuan", 1000000L);

    }

    public static void init(RegistryEvent.Register<Item> event){
        final IForgeRegistry<Item> registry = event.getRegistry();

        File file = new File(FMLPaths.GAMEDIR.get().toString(), "/atom/economy/configuration.json");
        if(!file.exists()){
            JsonUtil.write(file, getDefaultContent());
        }
        JsonObject obj = JsonUtil.get(file);
        if(obj.has("Items")){
            obj.get("Items").getAsJsonArray().forEach((elm) -> {
                GenericMoney money = new GenericMoney(elm.getAsJsonObject(), true);
                Eco.CURRENCY.registerAll(money);
                assert money.getRegistryName() != null;
                registry.registerAll(
                       MONEY = new GenericMoneyItem(money).setRegistryName( money.getRegistryName().getPath())
                );
                money.stackload(ItemUtil.getByNameOrId("atomeco:" + money.getRegistryName().getPath()), elm.getAsJsonObject(), true);
            });
        }
        //
        if(obj.has("Banks")){
            DEF_BANKS = obj.get(("Banks")).getAsJsonArray();
        }
    }

    public static void loadDefaultBanks(){
        if(DEF_BANKS == null) return;
        DEF_BANKS.forEach((elm) -> {
            String uuid = elm.getAsJsonObject().get("uuid").getAsString();
            File file = new File(DataManager.BANK_DIR, uuid + ".json");
            if(!file.exists() && !DataManager.getBanks().containsKey(uuid)){
                DataManager.addBank(new GenericBank(elm.getAsJsonObject()));
            }
        });
    }
    private static JsonObject getDefaultContent(){
        JsonObject obj = new JsonObject();
        JsonArray items = new JsonArray();
        DEFAULT.forEach((id, worth) -> {
            JsonObject jsn = new JsonObject();
            jsn.addProperty("id", id);
            jsn.addProperty("worth", worth);
            items.add(jsn);
        });
        obj.add("Items", items);
        //
        JsonArray banks = new JsonArray();
        JsonObject def = new JsonObject();
        def.addProperty("uuid", ModConfig.Common.DEFAULT_BANK.get());
        def.addProperty("name", "Default Server Bank");
        def.add("data", new JsonObject());
        banks.add(def);
        obj.add("Banks", banks);
        //
        JsonObject extexp = new JsonObject();
        JsonArray ext = new JsonArray();
        extexp.addProperty("id", "minecraft:nether_star");
        extexp.addProperty("worth", 100000);
        extexp.addProperty("register", false);
        ext.add(extexp);
        obj.add("ExternalItems", ext);
        //
        return obj;
    }

    public static String getWorthAsString(long value){
        return getWorthAsString(value, true, false);
    }

    public static String getWorthAsString(long value, boolean append){
        return getWorthAsString(value, append, false);
    }

    public static String getWorthAsString(long value, boolean append, boolean ignore){
        String str = value + "";
        if(value < 1000){
            if(!ModConfig.COMMON.SHOW_DECIMALS.get() && (value == 0 || (!ModConfig.COMMON.SHOW_DECIMALS.get() && !ignore && value < 100))) return "0" + (append ? ModConfig.Common.CURRENCY_SIGN.get() : "");
            str = value + "";
            str = str.length() == 1 ? "00" + str : str.length() == 2 ? "0" + str : str;
            return ((str = "0" + ModConfig.DOT + str).length() == 5 && (!ignore && !ModConfig.COMMON.SHOW_DECIMALS.get()) ? str.substring(0, 4) : str) + (append ? ModConfig.Common.CURRENCY_SIGN.get() : "");
        }
        else{
            try{
                str = new StringBuilder(str).reverse().toString();
                String[] arr = str.split("(?<=\\G...)");
                str = arr[0] + ModConfig.DOT;
                for(int i = 1; i < arr.length; i++){
                    str += arr[i] + ((i >= arr.length - 1) ? "" :  ModConfig.COMMA);
                }
                str = new StringBuilder(str).reverse().toString();
                return (str = ModConfig.COMMON.SHOW_DECIMALS.get() ? ModConfig.COMMON.SHOW_CENTESIMALS.get() || ignore ? str : str.substring(0, str.length() - 1) : str.substring(0, str.lastIndexOf(ModConfig.DOT))) + (append ? ModConfig.Common.CURRENCY_SIGN.get() : "");
            }
            catch(Exception e){
                e.printStackTrace();
                return value + "ERR";
            }
        }
    }

    public static final long getItemStackWorth(ItemStack stack){
        if(stack.getItem() instanceof Money.Item){
            return ((Money.Item)stack.getItem()).getWorth(stack);
        }
        if(EXTERNAL_ITEMS_METAWORTH.containsKey(stack.getItem().getRegistryName())){
            return EXTERNAL_ITEMS_METAWORTH.get(stack.getItem().getRegistryName());
        }
        if(EXTERNAL_ITEMS.containsKey(stack.getItem().getRegistryName())){
            return EXTERNAL_ITEMS.get(stack.getItem().getRegistryName());
        }
        return 0;
    }

    public static boolean containsAsExternalItemStack(ItemStack stack){
        try{
            return EXTERNAL_ITEMS.containsKey(stack.getItem().getRegistryName())
                    || EXTERNAL_ITEMS_METAWORTH.containsKey(stack.getItem().getRegistryName());
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public static class EventHandler {

        @SubscribeEvent
        public void onRegistry(RegistryEvent.Register<Money> event){
            File file = new File(FMLPaths.CONFIGDIR.get().toString(), "/atom/economy/configuration.json");
            if(!file.exists()){
                return;
            }
            JsonObject obj = JsonUtil.get(file);
            if(obj.has("ExternalItems")){
                obj.get("ExternalItems").getAsJsonArray().forEach(elm -> {
                    JsonObject jsn = elm.getAsJsonObject();
                    ResourceLocation rs = new ResourceLocation(jsn.get("id").getAsString());
                    long worth = jsn.get("worth").getAsLong();
                    EXTERNAL_ITEMS_METAWORTH.put(rs.toString(), worth);
                    if(!EXTERNAL_ITEMS.containsKey(rs)){
                        EXTERNAL_ITEMS.put(rs, 0L);
                    }
                    else{
                        EXTERNAL_ITEMS.put(rs, worth);
                    }
                    if(jsn.has("register") && jsn.get("register").getAsBoolean()){
                        event.getRegistry().register(new GenericMoney(jsn, false));
                    }
                });
            }
        }

    }

}
