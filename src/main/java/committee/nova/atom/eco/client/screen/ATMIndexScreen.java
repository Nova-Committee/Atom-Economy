package committee.nova.atom.eco.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import committee.nova.atom.eco.common.containers.ATMContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.function.Consumer;

public class ATMIndexScreen extends AbstractScreenPage<ATMContainer>{


    protected ATMIndexScreen(ATMContainer container, ContainerScreen<ATMContainer> parent, Consumer<Integer> changePage) {
        super(container, parent, changePage);
    }

    @Override
    public void init() {
        final int halfW = width / 2;
        this.addButton(new ExtendedButton(parent.getGuiLeft() + 10, parent.getGuiTop() + parent.getYSize()-52, 70, 20,  new TranslationTextComponent("gui.atomeco.index.deposit"),
                $ -> NavigateTo(1)));
        this.addButton(new ExtendedButton(parent.getGuiLeft() + 10, parent.getGuiTop() + 8, 70, 20,  new TranslationTextComponent("gui.atomeco.index.withdraw"),
                $ -> NavigateTo(2)));
        this.addButton(new ExtendedButton(halfW + 40, parent.getGuiTop() + 8, 70, 20,  new TranslationTextComponent("gui.atomeco.index.self_info"),
                $ -> NavigateTo(3)));
        this.addButton(new ExtendedButton(halfW + 40, parent.getGuiTop() + parent.getYSize()-52, 70, 20,  new TranslationTextComponent("gui.atomeco.index.transfer"),
                $ -> NavigateTo(4)));
    }

    @Override
    public void renderInternal(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {

    }

    @Override
    public void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY) {

    }


}
