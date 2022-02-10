package committee.nova.atom.eco.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import committee.nova.atom.eco.api.client.gui.ContainerScreenBase;
import committee.nova.atom.eco.client.widegts.ButtonRect;
import committee.nova.atom.eco.common.config.ConfigUtil;
import committee.nova.atom.eco.common.config.ModConfig;
import committee.nova.atom.eco.common.containers.WalletContainer;
import committee.nova.atom.eco.common.items.GenericMoneyItem;
import committee.nova.atom.eco.common.items.WalletItem;
import committee.nova.atom.eco.common.net.PacketHandler;
import committee.nova.atom.eco.common.net.packets.WalletPacket;
import committee.nova.atom.eco.utils.CurrencyUtil;
import committee.nova.atom.eco.utils.ItemUtil;
import committee.nova.atom.eco.utils.ScreenUtil;
import committee.nova.atom.eco.utils.math.MathUtil;
import committee.nova.atom.eco.utils.text.StringUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/10 8:04
 * Version: 1.0
 */
@OnlyIn(Dist.CLIENT)
public class WalletScreen extends ContainerScreenBase<WalletContainer> {
    public WalletScreen(Container container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, new StringTextComponent("Wallet"));
    }

    /**
     * Gets the current Wallet Stack, returns empty if missing and closes the screen.
     */
    private ItemStack getCurrentWalletStack() {

        ItemStack walletStack = CurrencyUtil.getCurrentWalletStack(player);

        if (!walletStack.isEmpty()) {
            return walletStack;
        } else {
            player.closeContainer();
            return ItemStack.EMPTY;
        }
    }

    @Override
    protected void init() {
        super.init();


        addButton(new ButtonRect(getScreenX() + 146, getScreenY() + 15 + 18, 16, "+", (btn) -> addMoney()));

    }

    /**
     * Called when a "+" button is pressed.
     * Adds money to the Player from the Wallet.
     */
    private void addMoney() {

        ItemStack walletStack = getCurrentWalletStack();

        //Checks if there is a current Wallet.
        if (!walletStack.isEmpty()) {

            WalletItem walletItem = (WalletItem) walletStack.getItem();

            int price = (int) ((GenericMoneyItem) ConfigUtil.MONEY).getWorth();

            int multiplier = MathUtil.getShiftCtrlInt(1, 16, 64, 9 * 64);
            price *= multiplier;

            //If the Wallet's balance can afford the requested amount, give it to the player and sync the current balance.
            if (WalletItem.getBalance(walletStack) >= price) {

                PacketHandler.INSTANCE.sendToServer(new WalletPacket(multiplier));
                CompoundNBT nbt = ItemUtil.getNBT(walletStack);
                nbt.putInt("balance", nbt.getInt("balance") - price);
            }
        }
    }

    @Override
    public void drawGuiForeground(MatrixStack matrixStack, int mouseX, int mouseY) {

        GL11.glDisable(GL11.GL_LIGHTING);
        addInfoIcon(0);
        addInfoHoveringText(matrixStack, mouseX, mouseY, "操作说明", "Shift: 16个, Ctrl: 64个, Shift + Ctrl: 64 * 9个");
    }

    @Override
    public void drawGuiBackground(MatrixStack matrixStack, int mouseY, int mouseX) {

        ScreenUtil.drawItemStack(itemRenderer, new ItemStack(ConfigUtil.MONEY), getScreenX() + 127, getScreenY() + 15);


        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor4f(1, 1, 1, 1);

        ItemStack stack = getCurrentWalletStack();

        if (!stack.isEmpty()) {
            ScreenUtil.drawCenteredString(matrixStack, StringUtil.printCommas(ItemUtil.getNBT(stack).getInt("balance")), getScreenX() + getGuiSizeX() / 2 - 16, getScreenY() + 42, 0, TEXT_COLOR_GRAY);
            ScreenUtil.drawCenteredString(matrixStack, ModConfig.COMMON.currencySign.get(), getScreenX() + getGuiSizeX() / 2 - 16, getScreenY() + 51, 0, TEXT_COLOR_GRAY);
        }
    }

    @Override
    public String getGuiTextureName() {
        return "wallet";
    }
}
