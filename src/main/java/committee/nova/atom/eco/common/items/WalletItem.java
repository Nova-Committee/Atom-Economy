package committee.nova.atom.eco.common.items;

import committee.nova.atom.eco.api.common.containers.inventorys.ItemStackInventory;
import committee.nova.atom.eco.api.common.items.BaseItem;
import committee.nova.atom.eco.common.config.ModConfig;
import committee.nova.atom.eco.common.containers.WalletContainer;
import committee.nova.atom.eco.init.ModTabs;
import committee.nova.atom.eco.utils.CurrencyUtil;
import committee.nova.atom.eco.utils.ItemUtil;
import committee.nova.atom.eco.utils.text.LoreUtil;
import committee.nova.atom.eco.utils.text.MsgUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/9 21:42
 * Version: 1.0
 */
public class WalletItem extends BaseItem {

    public WalletItem() {
        super(new Item.Properties().tab(ModTabs.tab).stacksTo(1));
    }

    public static MsgUtil getMessage(PlayerEntity player) {
        return new MsgUtil("Wallet", player);
    }

    /**
     * 获取钱包现有的钱
     */
    public static int getBalance(ItemStack stack) {
        return ItemUtil.getNBT(stack).getInt("balance");
    }

    /**
     * 将钱存入钱包
     */
    public static void depositCurrency(ItemStack stack, int depsositAmount) {
        ItemUtil.getNBT(stack).putInt("balance", getBalance(stack) + depsositAmount);
    }

    /**
     * 将钱从钱包取出
     */
    public static void withdrawCurrency(ItemStack stack, int withdrawAmount) {
        ItemUtil.getNBT(stack).putInt("balance", getBalance(stack) - withdrawAmount);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltipList, ITooltipFlag advanced) {
        LoreUtil.addInformationLore(tooltipList, I18n.get("用于存放货币的地方"), true);
        LoreUtil.addControlsLore(tooltipList, I18n.get("打开钱包"), LoreUtil.Type.USE, true);
        LoreUtil.addBlankLine(tooltipList);
        CurrencyUtil.addCurrencyLore(tooltipList, getBalance(stack), ModConfig.COMMON.walletCurrencyCapacity.get());
    }

    /**
     * 处理打开gui
     */
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {

        ItemStack stack = player.getItemInHand(hand);

        //Checks if on server & if the Player is a Server Player.
        if (!world.isClientSide && player instanceof ServerPlayerEntity) {
            openGui((ServerPlayerEntity) player, stack, player.inventory.selected);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }

        return new ActionResult<>(ActionResultType.FAIL, stack);
    }

    /**
     * 打开gui
     */
    private void openGui(ServerPlayerEntity player, ItemStack stack, int selectedSlot) {

        NetworkHooks.openGui(player, new SimpleNamedContainerProvider(
                        (id, playerInventory, openPlayer) -> new WalletContainer(id, playerInventory, new ItemStackInventory(stack, 1)), stack.getDisplayName()),
                (buffer) -> buffer.writeVarInt(selectedSlot));
    }

    /**
     * 与curios联动
     */
    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, CompoundNBT unused) {

//        if (CalemiUtils.curiosLoaded) {
//            return CuriosIntegration.walletCapability();
//        }

        return super.initCapabilities(stack, unused);
    }
}
