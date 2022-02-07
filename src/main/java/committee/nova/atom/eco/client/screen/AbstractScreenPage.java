package committee.nova.atom.eco.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.container.Container;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractScreenPage<T extends Container> extends Screen {
    protected final List<Widget> buttons = Lists.newArrayList();//按钮 list
    protected T container;
    ContainerScreen<T> parent;//父页面
    Consumer<Integer> changePageNum;//转到的页面

    protected AbstractScreenPage(T container, ContainerScreen<T> parent, Consumer<Integer> changePage) {
        super(parent.getTitle());
        this.parent=parent;
        this.changePageNum =changePage;
        this.container=container;
    }


    public T getContainer()
    {
        return container;
    }

    /**
     * 子页面监听
     *
     */
    @Nonnull
    public List<? extends IGuiEventListener> children() {
        return this.children;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void render(MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks){
        RenderHelper.turnOff();
        GlStateManager._disableLighting();
        GlStateManager._disableDepthTest();

        renderInternal(matrix,mouseX, mouseY, partialTicks);
        for (Widget button : this.buttons) {
            button.render(matrix,mouseX, mouseY, partialTicks);
        }

        GlStateManager._enableLighting();
        GlStateManager._enableDepthTest();
        RenderHelper.turnBackOn();
    }

    public abstract void renderInternal(MatrixStack matrix,final int mouseX, final int mouseY, final float partialTicks);

    public abstract void drawGuiContainerForegroundLayer(MatrixStack matrix,int mouseX, int mouseY);

    @Override
    public abstract void init();


    @Nonnull
    @Override
    protected <B extends Widget> B addButton(@Nonnull B p_addButton_1_) {
        this.buttons.add(p_addButton_1_);
        this.children.add(p_addButton_1_);
        return p_addButton_1_;
    }

    /**
     * 转到xx页面
     * @param page 页面id
     */
    protected void NavigateTo(int page)
    {
        changePageNum.accept(page);
    }


}
