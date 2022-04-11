package nova.committee.atom.eco.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import nova.committee.atom.eco.client.widegts.ScreenRect;
import nova.committee.atom.eco.util.ScreenUtil;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/8 19:59
 * Version: 1.0
 */
@OnlyIn(Dist.CLIENT)
public abstract class ItemStackButton extends Button {


    protected final ItemRenderer itemRenderer;
    protected ScreenRect hoverRect;

    /**
     * 用于在按钮中呈现物品
     *
     * @param onPress 当按钮被按时触发
     */
    public ItemStackButton(int x, int y, ItemRenderer itemRenderer, OnPress onPress) {
        super(x, y, 16, 16, new TextComponent(""), onPress);
        hoverRect = new ScreenRect(x, y, 16, 16);
        this.itemRenderer = itemRenderer;
    }

    public abstract ItemStack getRenderedStack();

    public abstract TranslatableComponent[] getTooltip();

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {

        if (this.visible && this.active) {

            isHovered = hoverRect.contains(mouseX, mouseY);

            ScreenUtil.drawItemStack(itemRenderer, getRenderedStack(), hoverRect.x, hoverRect.y);
            ScreenUtil.drawHoveringTextBox(poseStack, hoverRect, 150, mouseX, mouseY, 0xFFFFFF, getTooltip());
        }
    }
}

