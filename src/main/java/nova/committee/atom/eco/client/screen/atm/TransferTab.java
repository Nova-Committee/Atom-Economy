package nova.committee.atom.eco.client.screen.atm;

import com.google.common.collect.Lists;
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
import nova.committee.atom.eco.client.widegts.AccountButton;
import nova.committee.atom.eco.client.widegts.AccountSelectWidget;
import nova.committee.atom.eco.client.widegts.IconData;
import nova.committee.atom.eco.common.menu.ATMMenu;
import nova.committee.atom.eco.core.AccountDataManager;
import nova.committee.atom.eco.core.model.UseAccount;
import nova.committee.atom.eco.init.ModConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/11 18:39
 * Version: 1.0
 */
public class TransferTab extends ATMTab {

    ATMMenu atmMenu;
    Button buttonTransfer;
    //EditBox searchInput;
    EditBox playerInput;
    AccountSelectWidget accountSelect;

    String selectedAcc = null;

    protected TransferTab(ATMScreen screen) {
        super(screen);
        this.atmMenu = screen.getMenu();
    }

    @Override
    public void init() {
        //this.searchInput = this.screen.addRenderableTabWidget(new EditBox(this.screen.getFont(), this.screen.getGuiLeft() + 43, this.screen.getGuiTop() + 158, 45, 14, new TextComponent("")));
        this.playerInput = this.screen.addRenderableTabWidget(new EditBox(this.screen.getFont(), this.screen.getGuiLeft() + 27, this.screen.getGuiTop() + 157, 105, 15, new TextComponent("")));

        this.accountSelect = this.screen.addRenderableTabWidget(
                new AccountSelectWidget(this.screen.getGuiLeft() + 26, this.screen.getGuiTop() + 68, 1, AccountButton.Size.WIDE,
                        this::getAccList, this::selectedAcc, this::SelectAcc));
        this.accountSelect.init(this.screen::addRenderableTabWidget, this.screen.getFont());

        this.buttonTransfer = this.screen.addRenderableTabWidget(new ImageButton(this.screen.getGuiLeft() + 133, this.screen.getGuiTop() + 156, 17, 17, 63, 184, ATMScreen.GUI_TEXTURE, this::transferAct));
        this.buttonTransfer.active = false;

    }


    private List<UseAccount> getAccList() {
        List<UseAccount> results = Lists.newArrayList();
        results.addAll(atmMenu.gatherAccountAction());
        return results;
    }

    public UseAccount selectedAcc() {
        if (this.selectedAcc != null)
            return atmMenu.getAccountAction(selectedAcc);
        return null;
    }

    public void SelectAcc(int accIndex) {
        try {
            UseAccount team = atmMenu.gatherAccountAction().get(accIndex);
            if (team.getId().equals(this.selectedAcc))
                return;
            this.selectedAcc = team.getId();
        } catch (Exception e) {
        }
    }

    private void transferAct(Button button) {
        long money;
        if (!Objects.equals(selectedAcc, atmMenu.player.getStringUUID())) {
            try {
                money = Long.parseLong(playerInput.getValue());
                if (atmMenu.processOthersAction(selectedAcc, money)) {
                    atmMenu.player.closeContainer();
                    Static.LOGGER.info("transfer success");
                }
            } catch (Exception e) {
                atmMenu.player.sendMessage(new TranslatableComponent("gui.atomeco.valid_num"), UUID.randomUUID());
            }
        } else {
            atmMenu.player.sendMessage(new TranslatableComponent("gui.atomeco.cant_self"), UUID.randomUUID());
        }


    }

    @Override
    public void preRender(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
//        RenderSystem.setShaderTexture(0, ATMScreen.GUI_TEXTURE);
//        this.screen.blit(pose, this.screen.getGuiLeft() + 26, this.screen.getGuiTop() + 158, 44, 184, 14, 14);
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


        this.playerInput.tick();
        this.buttonTransfer.active = !this.playerInput.getValue().isBlank();

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
        return new TranslatableComponent("gui.atomeco.index.transfer");
    }
}
