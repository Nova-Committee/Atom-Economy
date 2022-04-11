package nova.committee.atom.eco.client.widegts;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import nova.committee.atom.eco.util.ScreenUtil;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/11 9:43
 * Version: 1.0
 */
public class SmoothButton extends Button {
    public final ScreenRect rect;

    /**
     * 更加平滑的按钮
     *
     * @param x       x
     * @param y       y
     * @param width   宽
     * @param textKey 文本
     * @param onPress 触发事件
     */
    public SmoothButton(int x, int y, int width, String textKey, OnPress onPress) {
        super(width, 16, x, y, new TranslatableComponent(textKey), onPress);

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = 16;

        rect = new ScreenRect(x, y, width, 16);
    }

    public void setPosition(int x, int y) {
        rect.x = x;
        this.x = x;
        rect.y = y;
        this.y = y;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {

        if (visible) {

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            //RenderSystem.setShaderTexture(0, Static.GUI_TEXTURES);

            if (rect.contains(mouseX, mouseY)) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                isHovered = true;
            } else {
                RenderSystem.setShaderColor(0.8F, 0.8F, 0.8F, alpha);
                isHovered = false;
            }

            if (!active) {
                RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, alpha);
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();

            ScreenUtil.drawExpandableRect(0, 0, rect, 256, 16, 0);
            ScreenUtil.drawCenteredString(poseStack, rect.x + (rect.width / 2), rect.y + (rect.height - 8) / 2, 0, 0xFFFFFF, (MutableComponent) getMessage());
        }
    }
}
