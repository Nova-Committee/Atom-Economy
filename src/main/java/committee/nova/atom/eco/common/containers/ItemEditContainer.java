package committee.nova.atom.eco.common.containers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import committee.nova.atom.eco.api.market.MarketEntity;
import committee.nova.atom.eco.common.containers.slots.DisplaySlot;
import committee.nova.atom.eco.common.net.PacketHandler;
import committee.nova.atom.eco.utils.InventoryUtil;
import committee.nova.atom.eco.utils.math.MathUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/8 9:11
 * Version: 1.0
 */
public class ItemEditContainer extends Container {


    public static final int columnCount = 9;//宽
    public static final int rowCount = 6;//高
    public static List<ItemGroup> ITEM_GROUP_BLACKLIST = ImmutableList.of(ItemGroup.TAB_HOTBAR, ItemGroup.TAB_INVENTORY, ItemGroup.TAB_SEARCH);
    private static List<ItemStack> allItems = null; //所有的物品
    public final PlayerEntity player;
    public final int tradeIndex;
    private final MarketEntity marketItem;
    IInventory displayInventory;//显现出来的这一页的物品
    List<ItemStack> filteredResultItems;//过滤的物品
    List<ItemStack> searchResultItems;//搜索完的物品
    private String searchString;//搜索的字符
    private int stackCount = 1;//物品统计
    private int page = 0;//页码
    private int editSlot = 0;//选中编辑的slot


    protected ItemEditContainer(int pContainerId, PlayerInventory inventory) {
        super(pMenuType, pContainerId);
        this.player = inventory.player;
        this.displayInventory = new Inventory(columnCount * rowCount);

        if (!this.isClient())
            return;

        //显现出来的栏
        for (int y = 0; y < rowCount; y++) {
            for (int x = 0; x < columnCount; x++) {
                this.addSlot(new Slot(this.displayInventory, x + y * columnCount, 8 + x * 18, 18 + y * 18));
            }
        }

        //加载所有物品
        initItemList();
        this.filteredResultItems = this.getFilteredItems();

        //将搜索值设为默认
        this.modifySearch("");
    }

    @OnlyIn(Dist.CLIENT)
    protected static void initItemList() {
        if (allItems != null)
            return;

        allItems = new ArrayList<>();

        //遍历所有的tab以防止有隐藏的物品
        for (ItemGroup group : ItemGroup.TABS) {
            if (!ITEM_GROUP_BLACKLIST.contains(group)) {
                //获取tab中的所有物品
                NonNullList<ItemStack> items = NonNullList.create();
                group.fillItemList(items);
                //如果没有插入到list，就插入到list
                for (ItemStack stack : items) {

                    if (!itemListAlreadyContains(stack))
                        allItems.add(stack);

                    if (stack.getItem() == Items.ENCHANTED_BOOK) {
                        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
                        enchantments.forEach((enchantment, level) -> {
                            for (int newLevel = level - 1; newLevel > 0; newLevel--) {
                                ItemStack newBook = new ItemStack(Items.ENCHANTED_BOOK);
                                EnchantmentHelper.setEnchantments(ImmutableMap.of(enchantment, newLevel), newBook);
                                if (!itemListAlreadyContains(newBook)) {
                                    allItems.add(newBook);
                                }
                            }
                        });
                    }

                }
            }
        }
    }

    private static boolean itemListAlreadyContains(ItemStack stack) {
        for (ItemStack s : allItems) {
            if (InventoryUtil.ItemMatches(s, stack))
                return true;
        }
        return false;
    }
//    public final ItemTradeData tradeData;
//    public final Supplier<IItemTrader> traderSource;

    public int getStackCount() {
        return this.stackCount;
    }

    public int getPage() {
        return this.page;
    }

    public int getEditSlot() {
        return this.editSlot;
    }

    protected boolean isClient() {
        return this.player.level.isClientSide;
    }

    @Override
    public ItemStack clicked(int pSlotId, int pDragType, ClickType pClickType, PlayerEntity pPlayer) {
        if (!this.isClient()) //
            return ItemStack.EMPTY;

        if (pSlotId >= 0 && pSlotId < slots.size()) {
            Slot slot = slots.get(pSlotId);
            if (slot == null)
                return ItemStack.EMPTY;
            if (slot instanceof DisplaySlot) //只是为了用于显示物品而用的插槽，没有具体功能
                return ItemStack.EMPTY;
            //Get the item stack in the slot
            ItemStack stack = slot.getItem();
            //Define the item
            if (!stack.isEmpty()) {
                this.setItem(this.editSlot, stack);
                return ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(PlayerEntity pPlayer) {
        return true;
    }

    public void modifySearch(String newSearch) {
        this.searchString = newSearch.toLowerCase();

        //插入到list表中
        if (this.searchString.length() > 0) {
            this.searchResultItems = new ArrayList<>();
            List<ItemStack> validItems = this.editSlot == 0 ? this.filteredResultItems : allItems;
            for (ItemStack stack : validItems) {
                //搜索物品名
                if (stack.getDisplayName().getString().toLowerCase().contains(this.searchString)) {
                    this.searchResultItems.add(stack);
                }
                //搜索注册名
                else if (stack.getItem().getRegistryName().toString().contains(this.searchString)) {
                    this.searchResultItems.add(stack);
                }
                //搜索附魔
                else {
                    AtomicReference<Boolean> enchantmentMatch = new AtomicReference<>(false);
                    Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
                    enchantments.forEach((enchantment, level) -> {
                        if (enchantment.getRegistryName().toString().contains(this.searchString))
                            enchantmentMatch.set(true);
                        else if (enchantment.getFullname(level).getString().toLowerCase().contains(this.searchString))
                            enchantmentMatch.set(true);
                    });
                    if (enchantmentMatch.get())
                        this.searchResultItems.add(stack);
                }
            }
        } else //没有搜索的字符串
        {
            this.searchResultItems = this.editSlot == 0 ? this.filteredResultItems : allItems;
        }

        //Run refresh page code to validate the page # and repopulate the display inventory
        this.refreshPage();

    }


    private List<ItemStack> getFilteredItems() {
        List<ItemStack> results = Lists.newArrayList();
        results.addAll(allItems);
        return results;
    }

    public int maxPage() {
        return (this.searchResultItems.size() - 1) / this.displayInventory.getContainerSize();
    }

    public void modifyPage(int deltaPage) {
        this.page += deltaPage;
        refreshPage();
    }

    public void refreshPage() {

        if (this.page < 0)
            this.page = 0;
        if (this.page > maxPage())
            this.page = maxPage();


        int startIndex = this.page * columnCount * rowCount;
        //显示内容
        for (int i = 0; i < this.displayInventory.getContainerSize(); i++) {
            int thisIndex = startIndex + i;
            if (thisIndex < this.searchResultItems.size()) //显示搜索完的
            {
                ItemStack stack = this.searchResultItems.get(thisIndex).copy();
                stack.setCount(MathUtil.clamp(this.stackCount, 1, stack.getMaxStackSize()));
                this.displayInventory.setItem(i, stack);
            } else {
                this.displayInventory.setItem(i, ItemStack.EMPTY);
            }
        }

    }

    public void toggleEditSlot() {
        if (this.tradeData.isBarter()) {
            this.editSlot = this.editSlot == 1 ? 0 : 1;
            this.modifySearch(this.searchString);
        }

    }

    public void setItem(ItemStack stack, int slot) {
        if (!this.traderSource.get().hasPermission(this.player, Permissions.EDIT_TRADES))
            return;
        if (isClient()) {
            //向服务器发送
            if (this.editSlot == 1)
                this.tradeData.setBarterItem(stack);
            else
                this.tradeData.setSellItem(stack);
            PacketHandler.INSTANCE.sendToServer(new MessageItemEditSet(stack, this.editSlot));
        } else {
            //Set the trade
            if (slot == 1)
                this.traderSource.get().getTrade(this.tradeIndex).setBarterItem(stack);
            else
                this.traderSource.get().getTrade(this.tradeIndex).setSellItem(stack);
            this.traderSource.get().markTradesDirty();
        }
    }

}
