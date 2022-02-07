package committee.nova.atom.eco.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import committee.nova.atom.eco.common.containers.ATMContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ATMScreen extends ContainerScreen<ATMContainer> {

    private static final ResourceLocation backgroundLocation = new ResourceLocation("minecraft", "textures/gui" +
            "/demo_background.png");
    private final List<AbstractScreenPage<ATMContainer>> pages=new ArrayList<>();
    private int pageNumber=0;

    public ATMScreen(ATMContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        pages.add(new ATMIndexScreen(screenContainer,this,this::ChangePage));
        pages.add(new ATMDepositScreen(screenContainer,this,this::ChangePage));
        pages.add(new ATMWithdrawScreen(screenContainer,this,this::ChangePage));
        pages.add(new ATMInfoScreen(screenContainer,this,this::ChangePage));
        pages.add(new ATMTransferScreen(screenContainer,this,this::ChangePage));
        this.imageWidth = 250;
        //pages.add(new TerritoryPermissionScreen(screenContainer,inv,titleIn));
    }


    public void ChangePage(int num)
    {
        pageNumber=num;
    }

    @Override
    public void init(@Nonnull Minecraft mc, int width, int height) {
        super.init(mc, width, height);
        pages.forEach(t->t.init(mc,width,height));
    }

    @Override
    public void render(MatrixStack stack,int mouseX, int mouseY, float ticks) {
        this.renderBackground(stack);
        super.render(stack,mouseX, mouseY, ticks);

        pages.get(pageNumber).render(stack,mouseX,mouseY,ticks);
    }



    @Override
    protected void renderBg(MatrixStack stack,float partialTicks, int mouseX, int mouseY) {
        Minecraft.getInstance().textureManager.bind(backgroundLocation);
        int startX = this.getGuiLeft();
        int startY = this.getGuiTop();
        this.blit(stack,startX, startY, 0, 0, this.getXSize(), this.getYSize());
    }



    @Override
    protected void renderLabels(MatrixStack stack,int mouseX, int mouseY) {
        //super.drawGuiContainerForegroundLayer(stack,mouseX, mouseY);
        pages.get(pageNumber).drawGuiContainerForegroundLayer(stack,mouseX,mouseY);
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        pages.get(pageNumber).mouseClicked(p_mouseClicked_1_,p_mouseClicked_3_,p_mouseClicked_5_);
        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }
    @Override
    public boolean mouseDragged(double d1, double d3, int d5, double d6, double d8) {
        return pages.get(pageNumber).mouseDragged(d1,d3,d5,d6,d8);
    }

    @Override
    public boolean mouseReleased(double x, double y, int btn) {
        return pages.get(pageNumber).mouseReleased(x,y,btn);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double amount) {
        return pages.get(pageNumber).mouseScrolled(x,y,amount);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return pages.get(pageNumber).keyReleased(keyCode,scanCode,modifiers);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        pages.get(pageNumber).keyPressed(p_keyPressed_1_,p_keyPressed_2_,p_keyPressed_3_);
        return true;//super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    @Override
    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        return pages.get(pageNumber).charTyped(p_charTyped_1_,p_charTyped_2_);
    }

    @Override
    public void tick() {
        pages.get(pageNumber).tick();
    }
}
