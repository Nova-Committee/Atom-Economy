package committee.nova.atom.eco.core;

import com.google.gson.JsonObject;
import committee.nova.atom.eco.Eco;
import committee.nova.atom.eco.api.market.MarketEntity;
import committee.nova.atom.eco.utils.ItemUtil;
import committee.nova.atom.eco.utils.JsonUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 市场数据管理类
 * Author: cnlimiter
 * Date: 2022/2/8 15:50
 * Version: 1.0
 */
public class MarketDataManager {

    /**
     * type - uuid - index - markets
     */
    public static final Map<String, Map<String, Map<Integer, MarketEntity>>> MARKET_ITEM_CACHE = new ConcurrentHashMap<>();

    public static File MARKET_DIR;

    public MarketDataManager(File file) {
        MARKET_DIR = new File(file, "/market/");

    }

    public static MarketDataManager getInstance() {
        return Eco.MARKET_CACHE;
    }

    @Nullable
    public static MarketEntity getMarketEntity(String type, String uuid, int index) {
        return getMarketEntity(type, uuid, index, null);
    }

    @Nullable
    public static MarketEntity getMarketEntity(String type, String uuid, int index, Class<? extends MarketEntity> impl) {
        if (MARKET_ITEM_CACHE.get(type).containsKey(uuid) && MARKET_ITEM_CACHE.get(type).get(uuid).containsKey(index)) {
            MarketEntity market = MARKET_ITEM_CACHE.get(type).get(uuid).get(index);
            return market;
        }
        return loadMarket(type, uuid, index, impl);
    }

    public static void save(MarketEntity marketEntity) {
        File owner, entity;
        if (marketEntity.isSell()) {
            File mType = new File(MARKET_DIR, "/sell/");
            owner = new File(mType, "/" + marketEntity.getOwnerUUID() + "/");
            entity = new File(owner, "/" + marketEntity.getIndex() + ".json");
            if (!entity.exists()) {
                entity.getParentFile().mkdirs();
            }
            JsonUtil.write(entity, marketEntity.toJson(), true);
        } else {
            File mType = new File(MARKET_DIR, "/buy/");
            owner = new File(mType, "/" + marketEntity.getOwnerUUID() + "/");
            entity = new File(owner, "/" + marketEntity.getIndex() + ".json");
            if (!entity.exists()) {
                entity.getParentFile().mkdirs();
            }
            JsonUtil.write(entity, marketEntity.toJson(), true);
        }

    }

    public static void createMarket(String type, String uuid, int index, String displayName, ItemStack itemStack, long amount, long value) {
        createMarket(type, uuid, index, displayName, itemStack, amount, value, null);
    }

    private static boolean createMarket(String type, String uuid, int index, String displayName, ItemStack itemStack, long amount, long value, Class<? extends MarketEntity> impl) {
        if (type.equals("buy")) {
            try {
                MarketEntity market = impl.getConstructor(int.class, String.class, String.class, String.class, String.class, boolean.class, long.class, long.class)
                        .newInstance(index, displayName, itemStack.getItem().getRegistryName().toString(), itemStack.getTag().toString(), uuid, false, amount, value);

                addMarket("buy", uuid, index, market);
                Eco.LOGGER.info("创建新的" + uuid + "的" + type + "市场 " + "!");
                return true;
            } catch (ReflectiveOperationException | RuntimeException e) {
                e.printStackTrace();
                return false;
            }

        } else if (type.equals("sell")) {
            try {
                MarketEntity market = impl.getConstructor(int.class, String.class, String.class, String.class, String.class, boolean.class, long.class, long.class)
                        .newInstance(index, displayName, itemStack.getItem().getRegistryName().toString(), itemStack.getTag().toString(), uuid, true, amount, value);

                addMarket("sell", uuid, index, market);
                Eco.LOGGER.info("创建" + uuid + "的" + type + "市场 " + "!");
                return true;
            } catch (ReflectiveOperationException | RuntimeException e) {
                e.printStackTrace();
                return false;
            }

        }
        return false;
    }

    public static void unloadMarket(String type, String uuid, int index) {
        if (MARKET_ITEM_CACHE.get(type).containsKey(uuid) && MARKET_ITEM_CACHE.get(type).get(uuid).containsKey(index)) {
            try {
                save(MARKET_ITEM_CACHE.get(type).get(uuid).remove(index));
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private static MarketEntity loadMarket(String type, String uuid, int index, Class<? extends MarketEntity> impl) {
        impl = impl == null ? MarketEntity.class : impl;
        File mType = new File(MARKET_DIR, "/" + type + "/");
        File owner = new File(mType, "/" + uuid + "/");
        File entity = new File(owner, type + "/" + index + ".json");
        if (entity.exists()) {
            try {
                MarketEntity market = impl.getConstructor(JsonObject.class).newInstance(JsonUtil.get(entity));
                if (market.getIndex() != index) {
                    throw new RuntimeException("文件中的数据与请求的不匹配！应该得到解决.\n" + entity.getPath());
                }
                addMarket(type, uuid, index, market);
                return market;
            } catch (ReflectiveOperationException | RuntimeException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public static boolean addMarket(String type, String uuid, int index, MarketEntity market) {
        if (getMarketOfType(type) == null) {
            MARKET_ITEM_CACHE.put(type, new ConcurrentHashMap<>());
        }
        return Objects.requireNonNull(getMarketOfTypeUUID(type, uuid)).put(index, market) == null;
    }

    @Nullable
    public static Map<String, Map<Integer, MarketEntity>> getMarketOfType(String type) {
        return MARKET_ITEM_CACHE.get(type);
    }

    @Nullable
    public static Map<Integer, MarketEntity> getMarketOfTypeUUID(String type, String uuid) {
        return getMarketOfType(type).get(uuid);
    }

    public static ItemStack getStackFromList(List<MarketEntity> list, int index) {

        ItemStack stack = ItemStack.EMPTY;
        MarketEntity marketItem = getMarketItemById(list, index);

        if (marketItem != null) {

            Item item = Registry.ITEM.get(new ResourceLocation(marketItem.getRegistryName()));

            if (item != Items.AIR) {

                stack = new ItemStack(item, marketItem.amount);

                if (!marketItem.getStackNBT().isEmpty()) {
                    ItemUtil.attachNBTFromString(stack, marketItem.getStackNBT());
                }
            }
        }

        return stack;
    }

    private static MarketEntity getMarketItemById(List<MarketEntity> list, int index) {

        if (index < list.size()) {

            for (MarketEntity marketItem : list) {

                if (marketItem.index == index) {
                    return marketItem;
                }
            }
        }

        return null;
    }

    public final void saveAll() {
        for (Map<String, Map<Integer, MarketEntity>> map : MARKET_ITEM_CACHE.values()) {
            for (Map<Integer, MarketEntity> uuid : map.values()) {
                for (MarketEntity market : uuid.values()) {
                    try {
                        save(market);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }
    }

    public void clearAll() {
        MARKET_ITEM_CACHE.clear();
    }
}
