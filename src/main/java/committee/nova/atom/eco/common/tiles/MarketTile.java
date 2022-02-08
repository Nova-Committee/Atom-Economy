package committee.nova.atom.eco.common.tiles;

import committee.nova.atom.eco.api.market.MarketEntity;
import committee.nova.atom.eco.api.security.ISecurity;
import committee.nova.atom.eco.api.security.SecurityProfile;
import committee.nova.atom.eco.common.tiles.base.MoneyTile;
import committee.nova.atom.eco.core.MarketDataManager;
import committee.nova.atom.eco.utils.math.Location;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/8 15:26
 * Version: 1.0
 */
public class MarketTile extends MoneyTile implements ISecurity {

    public final List<MarketEntity> marketItemsToBuy = new ArrayList<>();
    public final List<MarketEntity> marketItemsToSell = new ArrayList<>();
    private final SecurityProfile profile = new SecurityProfile();
    public boolean dirtyFlag;
    public boolean buyMode = true;
    public int selectedIndex;
    public int purchaseAmount = 1;
    private Location location;

    public MarketTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
        dirtyFlag = true;
    }

    public ItemStack getSelectedItemStack() {
        return MarketDataManager.getStackFromList(getCurrentMarketItemList(), selectedIndex);
    }

    public MarketEntity getSelectedMarketItem() {
        clampSelectedIndex();

        if (getCurrentMarketItemList().size() > 0) {
            return getCurrentMarketItemList().get(selectedIndex);
        }

        return null;
    }

    private void clampSelectedIndex() {

        if (selectedIndex < 0 || selectedIndex >= getCurrentMarketItemList().size()) {
            selectedIndex = 0;
        }
    }

    private List<MarketEntity> getCurrentMarketItemList() {
        return buyMode ? marketItemsToBuy : marketItemsToSell;
    }

    @Override
    public SecurityProfile getSecurityProfile() {
        return profile;
    }

    private void registerMarketItems() {

        marketItemsToBuy.clear();
        marketItemsToSell.clear();
        if (MarketDataManager.MARKET_ITEM_CACHE != null)
            marketItemsToBuy.addAll(MarketDataManager.getMarketOfTypeUUID("buy", profile.getOwnerUUID()).values());
        if (MarketDataManager.MARKET_ITEM_CACHE != null)
            marketItemsToSell.addAll(MarketDataManager.getMarketOfTypeUUID("sell", profile.getOwnerUUID()).values());
        marketItemsToBuy.sort(Comparator.comparingInt(o -> o.index));
        marketItemsToSell.sort(Comparator.comparingInt(o -> o.index));
        dirtyFlag = false;

        markForUpdate();
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide && dirtyFlag) {
            registerMarketItems();
        }

        MarketEntity selectedMarketItem = getSelectedMarketItem();

        assert level != null;
        if (level.getGameTime() % 10 == 0) {

            if (selectedMarketItem != null) {

                int totalAmount = selectedMarketItem.amount * purchaseAmount;
                int totalValue = selectedMarketItem.value * purchaseAmount;

                ItemStack selectedStack = getSelectedItemStack();
                selectedStack.setCount(totalAmount);

                if (buyMode) {

//todo buy
                } else {

//todo sell
                }
            }
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        buyMode = nbt.getBoolean("buyMode");

        selectedIndex = nbt.getInt("selectedOffer");
        purchaseAmount = nbt.getInt("purchaseAmount");

        marketItemsToBuy.clear();
        marketItemsToSell.clear();

        if (nbt.contains("MarketItemsBuy")) {

            CompoundNBT marketParent = nbt.getCompound("MarketItemsBuy");

            for (int i = 0; i < marketParent.size(); i++) {
                CompoundNBT itemTag = marketParent.getCompound("BuyItem" + i);
                marketItemsToBuy.add(MarketEntity.readFromNBT(itemTag));
            }
        }

        if (nbt.contains("MarketItemsSell")) {

            CompoundNBT marketParent = nbt.getCompound("MarketItemsSell");

            for (int i = 0; i < marketParent.size(); i++) {
                CompoundNBT itemTag = marketParent.getCompound("SellItem" + i);
                marketItemsToSell.add(MarketEntity.readFromNBT(itemTag));
            }
        }
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        CompoundNBT buyParent = new CompoundNBT();
        CompoundNBT sellParent = new CompoundNBT();

        for (int i = 0; i < marketItemsToBuy.size(); i++) {

            MarketEntity marketItem = marketItemsToBuy.get(i);

            CompoundNBT marketTag = marketItem.writeToNBT();

            buyParent.put("BuyItem" + i, marketTag);
        }

        for (int i = 0; i < marketItemsToSell.size(); i++) {

            MarketEntity marketItem = marketItemsToSell.get(i);

            CompoundNBT marketTag = marketItem.writeToNBT();

            sellParent.put("SellItem" + i, marketTag);
        }

        //Write parent to root
        nbt.put("MarketItemsBuy", buyParent);
        nbt.put("MarketItemsSell", sellParent);

        nbt.putBoolean("buyMode", buyMode);

        nbt.putInt("selectedOffer", selectedIndex);
        nbt.putInt("purchaseAmount", purchaseAmount);
        return super.save(nbt);
    }
}
