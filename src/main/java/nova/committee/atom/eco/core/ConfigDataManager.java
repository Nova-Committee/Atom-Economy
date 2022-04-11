package nova.committee.atom.eco.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import nova.committee.atom.eco.Static;
import nova.committee.atom.eco.api.core.money.IMoney;
import nova.committee.atom.eco.common.item.MoneyItem;
import nova.committee.atom.eco.core.model.Money;
import nova.committee.atom.eco.util.JsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/9 23:34
 * Version: 1.0
 */
public class ConfigDataManager {
    public static final ArrayList<IMoney> MONEY_ARRAY_LIST = new ArrayList<>();
    private static final TreeMap<String, Long> DEFAULT = new TreeMap<>();
    private static final TreeMap<ResourceLocation, Long> EXTERNAL_ITEMS = new TreeMap<>();
    private static final TreeMap<String, Long> EXTERNAL_ITEMS_WORTH = new TreeMap<>();

    static {
        DEFAULT.put("1yuan", 1L);
        DEFAULT.put("2yuan", 2L);
        DEFAULT.put("5yuan", 5L);
        DEFAULT.put("10yuan", 10L);
        DEFAULT.put("20yuan", 20L);
        DEFAULT.put("50yuan", 50L);
        DEFAULT.put("100yuan", 100L);
        DEFAULT.put("200yuan", 200L);
        DEFAULT.put("500yuan", 500L);
        DEFAULT.put("1000yuan", 1000L);

    }

    //默认配置
    private static JsonObject getDefaultContent() {
        //默认的货币
        JsonObject obj = new JsonObject();
        JsonArray items = new JsonArray();
        DEFAULT.forEach((id, worth) -> {
            JsonObject jsn = new JsonObject();
            jsn.addProperty("id", id);
            jsn.addProperty("worth", worth);
            items.add(jsn);
        });
        obj.add("Items", items);

        //额外的物品价值定义示例
        JsonObject nether_star = new JsonObject();
        JsonObject baoshi = new JsonObject();
        JsonArray ext = new JsonArray();
        nether_star.addProperty("id", "minecraft:nether_star");
        nether_star.addProperty("worth", 100000);
        nether_star.addProperty("register", false);
        baoshi.addProperty("id", "minecraft:emerald");
        baoshi.addProperty("worth", 1000);
        baoshi.addProperty("register", false);//是否注册成货币
        ext.add(nether_star);
        ext.add(baoshi);
        obj.add("ExternalItems", ext);
        //
        return obj;
    }

    public static long getItemStackWorth(ItemStack stack) {
        if (stack.getItem() instanceof Money.Item money) {
            return money.getWorth();
        }
        if (EXTERNAL_ITEMS_WORTH.containsKey(Objects.requireNonNull(stack.getItem().getRegistryName()).toString())) {
            return EXTERNAL_ITEMS_WORTH.get(stack.getItem().getRegistryName().toString());
        }
        if (EXTERNAL_ITEMS.containsKey(stack.getItem().getRegistryName())) {
            return EXTERNAL_ITEMS.get(stack.getItem().getRegistryName());
        }
        return 0;
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class MoneyItemHandler {
        public static Item MONEY;

        @SubscribeEvent()
        public static void registryEntry(NewRegistryEvent event) {
            Static.CURRENCY = event.create(new RegistryBuilder<IMoney>().setName(Static.MONEY_NAME).setType(c(IMoney.class)));
        }

        private static <T> Class<T> c(Class<?> cls) {
            return (Class<T>) cls;
        }

        @SubscribeEvent
        public static void initMoney(RegistryEvent.Register<IMoney> event) {
            final IForgeRegistry<IMoney> registry = event.getRegistry();
            File file = new File(FMLPaths.GAMEDIR.get().toString(), "/atom/economy/config.json");
            if (!file.exists()) {
                JsonUtil.write(file, getDefaultContent());
            }
            JsonObject obj = JsonUtil.get(file);
            if (obj.has("Items")) {
                obj.get("Items").getAsJsonArray().forEach((elm) -> {
                    Money money = new Money(elm.getAsJsonObject(), true);
                    registry.register(money);
                    MONEY_ARRAY_LIST.add(money);
                    Static.LOGGER.info(money.getRegistryName().getPath() + " registered!");
                });
            }
            if (obj.has("ExternalItems")) {
                obj.get("ExternalItems").getAsJsonArray().forEach(elm -> {
                    JsonObject jsn = elm.getAsJsonObject();
                    ResourceLocation rs = new ResourceLocation(jsn.get("id").getAsString());
                    long worth = jsn.get("worth").getAsLong();
                    EXTERNAL_ITEMS_WORTH.put(rs.toString(), worth);
                    if (!EXTERNAL_ITEMS.containsKey(rs)) {
                        EXTERNAL_ITEMS.put(rs, 0L);
                    } else {
                        EXTERNAL_ITEMS.put(rs, worth);
                    }
                    if (jsn.has("register") && jsn.get("register").getAsBoolean()) {
                        registry.register(new Money(jsn, false));
                    }
                });
            }


        }

        //根据配置文件中的设置注册money物品
        @SubscribeEvent
        public static void initItem(RegistryEvent.Register<Item> event) {
            final IForgeRegistry<Item> registry = event.getRegistry();

            File file = new File(FMLPaths.GAMEDIR.get().toString(), "/atom/economy/config.json");
            if (!file.exists()) {
                JsonUtil.write(file, getDefaultContent());
            }
            JsonObject obj = JsonUtil.get(file);
            if (obj.has("Items")) {
                obj.get("Items").getAsJsonArray().forEach((elm) -> {
                    Money money = new Money(elm.getAsJsonObject(), true);
                    if (money.getRegistryName() != null)
                        registry.registerAll(
                                MONEY = new MoneyItem(money).setRegistryName(money.getRegistryName().getPath())
                        );

                    money.stackLoad(elm.getAsJsonObject(), true);
                });
            }

        }
    }


}
