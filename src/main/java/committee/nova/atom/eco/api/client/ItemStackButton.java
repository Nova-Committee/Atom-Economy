package committee.nova.atom.eco.api.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import committee.nova.atom.eco.client.widegts.ScreenRect;
import committee.nova.atom.eco.utils.ScreenUtil;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/8 19:59
 * Version: 1.0
 */
@OnlyIn(Dist.CLIENT)
public abstract class ItemStackButton extends Button {
    protected final ItemRenderer itemRenderer;
    protected ScreenRect rect;

    /**
     * 用于在按钮中呈现物品
     *
     * @param pressable 当按钮被按时触发
     */
    public ItemStackButton(int x, int y, ItemRenderer itemRender, IPressable pressable) {
        super(x, y, 16, 16, new StringTextComponent(""), pressable);
        rect = new ScreenRect(this.x, this.y, width, height);
        this.itemRenderer = itemRender;
    }

    public abstract ItemStack getRenderedStack();

    public abstract String[] getTooltip();

    public void setRect(ScreenRect rect) {
        this.rect = rect;
        this.x = rect.x;
        this.y = rect.y;
        this.width = rect.width;
        this.height = rect.height;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        if (this.visible && this.active) {

            isHovered = rect.contains(mouseX, mouseY);

            ScreenUtil.drawItemStack(itemRenderer, getRenderedStack(), rect.x, rect.y);
            ScreenUtil.drawHoveringTextBox(matrixStack, mouseX, mouseY, 150, rect, getTooltip());

            GL11.glColor4f(1, 1, 1, 1);
        }
    }
}

