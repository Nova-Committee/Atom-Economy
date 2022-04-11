package nova.committee.atom.eco.client.screen.atm;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Items;
import nova.committee.atom.eco.Static;
import nova.committee.atom.eco.client.widegts.IconData;
import nova.committee.atom.eco.common.menu.ATMMenu;
import nova.committee.atom.eco.core.AccountDataManager;
import nova.committee.atom.eco.init.ModConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/11 10:40
 * Version: 1.0
 */
public class DepositTab extends ATMTab {
    ATMMenu atmMenu;
    Button buttonDeposit;
    private EditBox values;

    protected DepositTab(ATMScreen screen) {
        super(screen);
        this.atmMenu = screen.getMenu();

    }

    @Override
    public void init() {
        final int halfW = screen.width / 2;
        final int halfH = screen.height / 2;
        this.values = this.screen.addRenderableTabWidget(
                new EditBox(screen.getFont(), screen.getGuiLeft() + 27, screen.getGuiTop() + 157, 105, 15, new TranslatableComponent("gui.atomeco.withdraw.deposit_value")));

        this.buttonDeposit = this.screen.addRenderableTabWidget(
                new ImageButton(this.screen.getGuiLeft() + 133, this.screen.getGuiTop() + 156, 17, 17, 63, 184, ATMScreen.GUI_TEXTURE,
                        t -> depositAct()));
    }

    private void depositAct() {
        long money;
        try {
            money = Long.parseLong(values.getValue());
            if (money > 0 && atmMenu.processSelfAction(money, true)) {
                Static.LOGGER.info("deposit success");
            }
        } catch (Exception e) {
            atmMenu.player.sendMessage(new TranslatableComponent("gui.atomeco.valid_num"), UUID.randomUUID());
        }

    }

    @Override
    public void preRender(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
        Component accountName = this.screen.getMenu().player.getDisplayName();
        Component accountBal = new TextComponent(ChatFormatting.BLACK + ModConfig.COMMON.currencySign.get() + ChatFormatting.DARK_BLUE + Objects.requireNonNull(AccountDataManager.getAccount(atmMenu.player.getStringUUID(), false)).getBalance());
        Component accountType = new TextComponent(ChatFormatting.DARK_PURPLE + Objects.requireNonNull(AccountDataManager.getAccount(atmMenu.player.getStringUUID(), false)).getType());
        this.screen.getFont().draw(pose, accountName, this.screen.getGuiLeft() + 35, this.screen.getGuiTop() + 59, 0x404040);
        this.screen.getFont().draw(pose, new TranslatableComponent("gui.atomeco.account_balance"), this.screen.getGuiLeft() + 56, this.screen.getGuiTop() + 46, 0x404040);
        this.screen.getFont().draw(pose, new TranslatableComponent("gui.atomeco.account_type"), this.screen.getGuiLeft() + 56, this.screen.getGuiTop() + 56, 0x404040);
        this.screen.getFont().draw(pose, accountBal, this.screen.getGuiLeft() + 75, this.screen.getGuiTop() + 46, 0x404040);
        this.screen.getFont().draw(pose, accountType, this.screen.getGuiLeft() + 75, this.screen.getGuiTop() + 56, 0x404040);

    }

    @Override
    public void postRender(PoseStack pose, int mouseX, int mouseY) {

    }

    @Override
    public void tick() {
        if (this.atmMenu.account == null) {
            this.buttonDeposit.active = false;
        } else {
            this.buttonDeposit.active = this.values.getValue() != null;
        }
    }

    @Override
    public void onClose() {

    }

    @NotNull
    @Override
    public IconData getIcon() {
        return IconData.of(Items.DIRT);
    }

    @Override
    public Component getTooltip() {
        return new TranslatableComponent("gui.atomeco.index.deposit");
    }
}
