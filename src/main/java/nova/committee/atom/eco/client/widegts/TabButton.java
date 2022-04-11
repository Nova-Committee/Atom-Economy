package nova.committee.atom.eco.client.widegts;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import nova.committee.atom.eco.Static;

import javax.annotation.Nonnull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/11 10:29
 * Version: 1.0
 */
public class TabButton extends Button {
    public static final ResourceLocation GUI_TEXTURE = Static.GUI_TEXTURE;

    public static final int SIZE = 25;

    public final ITab tab;
    private final Font font;

    private int rotation = 0;

    public TabButton(OnPress pressable, Font font, ITab tab) {
        super(0, 0, SIZE, SIZE, new TextComponent(""), pressable);
        this.font = font;
        this.tab = tab;
    }

    public void reposition(int x, int y, int rotation) {
        this.x = x;
        this.y = y;
        this.rotation = Mth.clamp(rotation, 0, 3);
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        //Set the texture & color for the button
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        float r = (float) (this.tab.getColor() >> 16 & 255) / 255f;
        float g = (float) (this.tab.getColor() >> 8 & 255) / 255f;
        float b = (float) (this.tab.getColor() & 255) / 255f;
        float activeColor = this.active ? 1f : 0.5f;
        RenderSystem.setShaderColor(r * activeColor, g * activeColor, b * activeColor, 1f);
        int xOffset = this.rotation < 2 ? 0 : this.width;
        int yOffset = (this.rotation % 2 == 0 ? 0 : 2 * this.height) + (this.active ? 0 : this.height);
        //Render the background
        this.blit(matrixStack, x, y, 200 + xOffset, yOffset, this.width, this.height);

        RenderSystem.setShaderColor(activeColor, activeColor, activeColor, 1f);
        this.tab.getIcon().render(matrixStack, this, this.font, this.x + 4, this.y + 4);

    }

    public interface ITab {
        @Nonnull
        public IconData getIcon();

        public int getColor();

        public Component getTooltip();

    }
}
