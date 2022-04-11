package nova.committee.atom.eco.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import nova.committee.atom.eco.Static;
import nova.committee.atom.eco.client.widegts.ScreenRect;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/25 21:21
 * Version: 1.0
 */
public class ScreenUtil {

    private static final int TEXTURE_SIZE = 256;
    private static final Minecraft mc = Minecraft.getInstance();

    /*
        Rectangle Methods
     */

    /**
     * Draws a textured rectangle to the screen.
     *
     * @param u      The u coordinate of the texture.
     * @param v      The v coordinate of the texture.
     * @param rect   The rectangle to render.
     * @param zLevel The z level of the rectangle. Graphics with higher values will render over graphics with lower ones.
     */
    public static void drawRect(int u, int v, ScreenRect rect, int zLevel) {

        int maxX = rect.x + rect.width;
        int maxY = rect.y + rect.height;

        int maxU = u + rect.width;
        int maxV = v + rect.height;

        float pixel = 1F / TEXTURE_SIZE;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex((float) rect.x, (float) maxY, zLevel).uv(u * pixel, maxV * pixel).endVertex();
        buffer.vertex((float) maxX, (float) maxY, zLevel).uv(maxU * pixel, maxV * pixel).endVertex();
        buffer.vertex((float) maxX, (float) rect.y, zLevel).uv(maxU * pixel, v * pixel).endVertex();
        buffer.vertex((float) rect.x, (float) rect.y, zLevel).uv(u * pixel, v * pixel).endVertex();
        tesselator.end();

        RenderSystem.disableBlend();
    }

    /**
     * Draws a textured rectangle to the screen that can be dynamically expanded in both width and height.
     *
     * @param u         The u coordinate of the texture.
     * @param v         The v coordinate of the texture.
     * @param rect      The rectangle to render.
     * @param maxWidth  The maximum width the rectangle could expand to.
     * @param maxHeight The maximum height the rectangle could expand to.
     * @param zLevel    The z level of the rectangle. Graphics with higher values will render over graphics with lower ones.
     */
    public static void drawExpandableRect(int u, int v, ScreenRect rect, int maxWidth, int maxHeight, int zLevel) {

        //TOP LEFT
        ScreenRect topLeft = new ScreenRect(rect.x, rect.y, Math.min(rect.width - 2, maxWidth), Math.min(rect.height - 2, maxHeight));
        drawRect(u, v, topLeft, zLevel);

        //RIGHT
        ScreenRect right = new ScreenRect(rect.x + rect.width - 2, rect.y, 2, Math.min(rect.height - 2, maxHeight));
        if (rect.width <= maxWidth) drawRect(u + maxWidth - 2, v, right, zLevel);

        //BOTTOM
        ScreenRect bottom = new ScreenRect(rect.x, rect.y + rect.height - 2, Math.min(rect.width - 2, maxWidth), 2);
        if (rect.height <= maxHeight) drawRect(u, v + maxHeight - 2, bottom, zLevel);

        //BOTTOM RIGHT
        ScreenRect bottomRight = new ScreenRect(rect.x + rect.width - 2, rect.y + rect.height - 2, 2, 2);
        if (rect.width <= maxWidth && rect.height <= maxHeight)
            drawRect(u + maxWidth - 2, v + maxHeight - 2, bottomRight, zLevel);
    }

    /**
     * Draws a colored rectangle to the screen (with transparency).
     *
     * @param rect     The rectangle to render.
     * @param zLevel   The z level of the rectangle. Graphics with higher values will render over graphics with lower ones.
     * @param hexColor The hexadecimal value for the color of the rectangle.
     * @param alpha    The alpha value of the rectangle. 1 = Opaque. 0 = Completely Transparent.
     */
    public static void drawColoredRect(ScreenRect rect, int zLevel, int hexColor, float alpha) {

        float r = (hexColor >> 16) & 0xFF;
        float g = (hexColor >> 8) & 0xFF;
        float b = (hexColor) & 0xFF;

        float red = ((((r * 100) / 255) / 100));
        float green = ((((g * 100) / 255) / 100));
        float blue = ((((b * 100) / 255) / 100));

        int maxX = rect.x + rect.width;
        int maxY = rect.y + rect.height;

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(rect.x, maxY, zLevel).color(red, green, blue, alpha).endVertex();
        buffer.vertex(maxX, maxY, zLevel).color(red, green, blue, alpha).endVertex();
        buffer.vertex(maxX, rect.y, zLevel).color(red, green, blue, alpha).endVertex();
        buffer.vertex(rect.x, rect.y, zLevel).color(red, green, blue, alpha).endVertex();
        tesselator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    /*
        String Methods
     */

    /**
     * Draws a string to the screen centered at x and y.
     *
     * @param poseStack The Pose Stack to render on.
     * @param x         The x coordinate of the string.
     * @param y         The y coordinate of the string.
     * @param zLevel    The z level of the rectangle. Graphics with higher values will render over graphics with lower ones.
     * @param hexColor  The hexadecimal value for the color of the string.
     * @param text      The text to render.
     */
    public static void drawCenteredString(PoseStack poseStack, int x, int y, int zLevel, int hexColor, MutableComponent text) {
        poseStack.pushPose();
        poseStack.translate(0, 0, 50 + zLevel);
        mc.font.draw(poseStack, text, x - (float) (mc.font.width(text) / 2), y, hexColor);
        poseStack.popPose();
    }

    /**
     * Draws a text box to the screen.
     *
     * @param poseStack        The Pose Stack to render on.
     * @param x                The x coordinate of the text box.
     * @param y                The y coordinate of the text box.
     * @param zLevel           The z level of the rectangle. Graphics with higher values will render over graphics with lower ones.
     * @param isStringCentered When true, will center the string inside the text box.
     * @param hexColor         The hexadecimal value for the color of the string.
     * @param text             The text inside the text box.
     */
    public static void drawTextBox(PoseStack poseStack, int x, int y, int zLevel, boolean isStringCentered, int hexColor, MutableComponent... text) {

        poseStack.pushPose();
        poseStack.translate(0, 0, zLevel);

        int maxLength = mc.font.width(text[0]);

        List<MutableComponent> textToRender = new ArrayList<>(Arrays.asList(text));

        for (MutableComponent msg : textToRender) {

            if (mc.font.width(msg) > maxLength) {
                maxLength = mc.font.width(msg);
            }
        }

        RenderSystem.setShaderTexture(0, Static.GUI_TOOLTIP);
        ScreenRect rect = new ScreenRect(x + (isStringCentered ? -maxLength / 2 : 0), y, maxLength + 5, 13 + ((textToRender.size() - 1) * 9));
        drawExpandableRect(0, 0, rect, 512, 512, zLevel);

        poseStack.translate(0, 0, 100);
        for (int i = 0; i < textToRender.size(); i++) {

            MutableComponent msg = textToRender.get(i);
            mc.font.draw(poseStack, msg.withStyle(ChatFormatting.WHITE), x + 3 + (isStringCentered ? -(int) (mc.font.width(msg.getString()) / 2) : 0), y + 3 + (i * 9), hexColor);
        }

        poseStack.translate(0, 0, 0);
        poseStack.popPose();
    }

    /**
     * Draws a text box to the screen.
     *
     * @param poseStack The Pose Stack to render on.
     * @param hoverRect The rectangle that when the mouse is over it, will render the text box.
     * @param zLevel    The z level of the rectangle. Graphics with higher values will render over graphics with lower ones.
     * @param mouseX    The x coordinate of the mouse.
     * @param mouseY    The y coordinate of the mouse.
     * @param hexColor  The hexadecimal value for the color of the string.
     * @param text      The text inside the text box.
     */
    public static void drawHoveringTextBox(PoseStack poseStack, ScreenRect hoverRect, int zLevel, int mouseX, int mouseY, int hexColor, MutableComponent... text) {

        if (hoverRect.contains(mouseX, mouseY)) {
            drawTextBox(poseStack, mouseX + 8, mouseY - 10, 50, false, hexColor, text);
        }
    }

    /*
        Misc Methods
     */

    /**
     * Draws an Item Stack to the screen.
     *
     * @param itemRender The Item Renderer to use.
     * @param stack      The Item Stack to draw.
     * @param x          The x coordinate of the Item Stack.
     * @param y          The y coordinate of the Item Stack
     */
    public static void drawItemStack(ItemRenderer itemRender, ItemStack stack, int x, int y) {
        itemRender.blitOffset = -100;
        itemRender.renderGuiItem(stack, x, y);
        itemRender.blitOffset = 0F;
    }

    /**
     * Draws an ItemStack.
     */
    public static void drawItemStack(GuiComponent gui, Font font, ItemStack stack, int x, int y) {
        drawItemStack(gui, font, stack, x, y, null);
    }

    /**
     * Draws an ItemStack.
     */
    public static void drawItemStack(GuiComponent gui, Font font, ItemStack stack, int x, int y, @Nullable String customCount) {

        Minecraft minecraft = Minecraft.getInstance();

        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        Player player = minecraft.player;
        Screen screen = minecraft.screen;
        int imageWidth = 0;
        if (screen != null) {
            imageWidth = screen.width;
            if (screen instanceof AbstractContainerScreen<?>)
                imageWidth = ((AbstractContainerScreen<?>) screen).getXSize();
        }

        if (font == null)
            font = minecraft.font;

        gui.setBlitOffset(100);
        itemRenderer.blitOffset = 100.0F;

        RenderSystem.enableDepthTest();

        itemRenderer.renderAndDecorateItem(player, stack, x, y, x + y * imageWidth);
        itemRenderer.renderGuiItemDecorations(font, stack, x, y, customCount);

        itemRenderer.blitOffset = 0.0F;
        gui.setBlitOffset(0);

    }


}
