package nova.committee.atom.eco.common.item;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.NetworkHooks;
import nova.committee.atom.eco.common.container.ItemBagContainer;
import nova.committee.atom.eco.common.menu.WalletMenu;
import nova.committee.atom.eco.init.ModTabs;
import nova.committee.atom.eco.util.text.LoreUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/10 0:34
 * Version: 1.0
 */
public class WalletItem extends Item {
    public static final int SIZE = 27;

    public WalletItem() {
        super(new Item.Properties()
                .tab(ModTabs.tab)
                .stacksTo(1));
        this.setRegistryName("wallet");
    }

    public static Container getInventory(ItemStack stack) {
        return new ItemBagContainer(stack, SIZE) {
            @Override
            public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) {
                return isValid(stack);
            }
        };
    }

    public static boolean isValid(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem().getClass() == MoneyItem.class
                ;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltipList, TooltipFlag advanced) {
        LoreUtil.addInformationLore(tooltipList, I18n.get("tooltip.wallet.1"), true);
        LoreUtil.addControlsLore(tooltipList, I18n.get("tooltip.wallet.2"), LoreUtil.Type.USE, true);
        LoreUtil.addBlankLine(tooltipList);
    }

    /**
     * 处理打开gui
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

        var stack = player.getItemInHand(hand);

        if (!world.isClientSide && player instanceof ServerPlayer) {
            openGui((ServerPlayer) player, stack);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }

        return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
    }

    //储存
    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        var world = ctx.getLevel();
        var pos = ctx.getClickedPos();
        var side = ctx.getClickedFace();

        var tile = world.getBlockEntity(pos);
        if (tile != null) {
            if (!world.isClientSide) {
                IItemHandler tileInv;
                if (tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).isPresent()) {
                    tileInv = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).orElseThrow(NullPointerException::new);
                } else if (tile instanceof Container) {
                    tileInv = new InvWrapper((Container) tile);
                } else {
                    return InteractionResult.FAIL;
                }

                var bagInv = getInventory(ctx.getItemInHand());
                for (int i = 0; i < bagInv.getContainerSize(); i++) {
                    ItemStack flower = bagInv.getItem(i);
                    ItemStack rem = ItemHandlerHelper.insertItemStacked(tileInv, flower, false);
                    bagInv.setItem(i, rem);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    /**
     * 打开gui
     */
    private void openGui(ServerPlayer player, ItemStack stack) {
        var container = new SimpleMenuProvider((id, p, player1) -> WalletMenu.getServerSideInstance(id, p, stack), stack.getDisplayName());
        NetworkHooks.openGui(player, container);
    }

    /**
     * 与curios联动
     */
    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, CompoundTag unused) {

//        if (CalemiUtils.curiosLoaded) {
//            return CuriosIntegration.walletCapability();
//        }

        return new InvProvider(stack);
    }

    private static class InvProvider implements ICapabilityProvider {
        private final LazyOptional<IItemHandler> opt;

        private InvProvider(ItemStack stack) {
            opt = LazyOptional.of(() -> new InvWrapper(getInventory(stack)));
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, opt);
        }
    }
}
