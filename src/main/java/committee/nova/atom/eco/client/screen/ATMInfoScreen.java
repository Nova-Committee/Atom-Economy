package committee.nova.atom.eco.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import committee.nova.atom.eco.common.config.ConfigUtil;
import committee.nova.atom.eco.common.containers.ATMContainer;
import committee.nova.atom.eco.common.items.ItemManager;
import committee.nova.atom.eco.data.DataManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.function.Consumer;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/5 14:47
 * Version: 1.0
 */
public class ATMInfoScreen  extends AbstractScreenPage<ATMContainer>{

    protected ATMInfoScreen(ATMContainer container, ContainerScreen<ATMContainer> parent, Consumer<Integer> changePage) {
        super(container, parent, changePage);
    }

    @Override
    public void init() {
        final int halfW = width / 2;
        final int halfH = height / 2;

        this.addButton(new ExtendedButton(halfW + 40, parent.getGuiTop()+parent.getYSize() - 30, 60, 20, new TranslationTextComponent("gui.atomeco.back"),
                $ -> NavigateTo(0)
        ));
    }
    @Override
    public void renderInternal(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {

        StringTextComponent inBank = new StringTextComponent(ConfigUtil.getWorthAsString(DataManager.getAccount("player", container.player.getStringUUID(), false).getBalance()));
        StringTextComponent inPlayer = new StringTextComponent(ConfigUtil.getWorthAsString(ItemManager.countInInventory(container.player)));


        matrix.pushPose();
        matrix.scale(1.3f,1.3f,1.3f);
        font.draw(matrix, new TranslationTextComponent("gui.atomeco.bank_leave"), (int)((this.parent.getGuiLeft() + 8) / 1.3),parent.getGuiTop() + 4,0xFFFFF0);
        font.draw( matrix, inBank, (int)((this.parent.getGuiLeft() + 8) / 1.3),parent.getGuiTop() + 14,0xFFFFF0);
        font.draw(matrix, new TranslationTextComponent("gui.atomeco.player_leave"), (int)((this.parent.getGuiLeft() + 8) / 1.3),parent.getGuiTop() + 34,0xFFFFF0);
        font.draw( matrix, inPlayer, (int)((this.parent.getGuiLeft() + 8) / 1.3),parent.getGuiTop() + 44,0xFFFFF0);
        matrix.popPose();
    }

    @Override
    public void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY) {

    }


}
