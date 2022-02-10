package committee.nova.atom.eco.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import committee.nova.atom.eco.Static;
import committee.nova.atom.eco.client.widegts.ScreenRect;
import committee.nova.atom.eco.utils.text.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
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

    public static void bindGuiTextures() {
        mc.getTextureManager().bind(Static.GUI_TEXTURES);
    }

    public static void bindTexture(String name) {
        mc.getTextureManager().bind(new ResourceLocation(Static.MOD_ID + ":textures/gui/" + name + ".png"));
    }


    public static void drawCenteredString(MatrixStack matrixStack, String text, int x, int y, int zLevel, int color) {

        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 50 + zLevel);
        mc.font.draw(matrixStack, text, x - (float) (mc.font.width(text) / 2), y, color);
        GL11.glPopMatrix();
    }


    public static void drawRect(int x, int y, int u, int v, int zLevel, int width, int height) {

        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, zLevel);

        int maxX = x + width;
        int maxY = y + height;

        int maxU = u + width;
        int maxV = v + height;

        float pixel = 1F / TEXTURE_SIZE;

        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder buffer = tessellator.getBuilder();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex((float) x, (float) maxY, 50).uv(u * pixel, maxV * pixel).endVertex();
        buffer.vertex((float) maxX, (float) maxY, 50).uv(maxU * pixel, maxV * pixel).endVertex();
        buffer.vertex((float) maxX, (float) y, 50).uv(maxU * pixel, v * pixel).endVertex();
        buffer.vertex((float) x, (float) y, 50).uv(u * pixel, v * pixel).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();

        GL11.glPopMatrix();
    }

    public static void drawItemStack(ItemRenderer itemRender, ItemStack stack, int x, int y) {

        GL11.glTranslatef(0.0F, 0.0F, 0.0F);
        itemRender.blitOffset = -100;
        itemRender.renderGuiItem(stack, x, y);
        itemRender.blitOffset = 0F;
    }

    public static void drawCappedRect(int x, int y, int u, int v, int zLevel, int width, int height, int maxWidth, int maxHeight) {

        //左上
        drawRect(x, y, u, v, zLevel, Math.min(width - 2, maxWidth), Math.min(height - 2, maxHeight));

        //右
        if (width <= maxWidth)
            drawRect(x + width - 2, y, u + maxWidth - 2, v, zLevel, 2, Math.min(height - 2, maxHeight));

        //底部
        if (height <= maxHeight) drawRect(x, y + height - 2, u, v + maxHeight - 2, zLevel, Math.min(width - 2, maxWidth), 2);

        //右底部
        if (width <= maxWidth && height <= maxHeight) drawRect(x + width - 2, y + height - 2, u + maxWidth - 2, v + maxHeight - 2, zLevel, 2, 2);
    }


    public static void drawHoveringTextBox (MatrixStack matrixStack, int mouseX, int mouseY, int zLevel, ScreenRect rect, String... text) {

        if (rect.contains(mouseX, mouseY)) {
            drawTextBox(matrixStack, mouseX + 8, mouseY - 10, zLevel, false, text);
        }
    }

    public static void drawTextBox (MatrixStack matrixStack, int x, int y, int zLevel, boolean centeredString, String... text) {

        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, zLevel);

        List<String> textToRender = new ArrayList<>();

        int maxLength = mc.font.width(text[0]);

        for (String str : text) {

            if (str.length() >= 80) {

                textToRender.add(str.substring(0, str.length() / 2));
                textToRender.add(StringUtil.addAllFormats(StringUtil.getFormatsFromString(str)) + str.substring(str.length() / 2));
            }

            else textToRender.add(str);
        }

        for (String str : textToRender) {

            if (mc.font.width(str) > maxLength) {
                maxLength = mc.font.width(str);
            }
        }

        bindTexture("tooltip");
        drawCappedRect(x + (centeredString ? - maxLength / 2 : 0), y, 0, 0, 0, maxLength + 5, 13 + ((textToRender.size() - 1) * 9), 512, 512);

        GL11.glTranslatef(0, 0, 100);
        for (int i = 0; i < textToRender.size(); i++) {

            String str = textToRender.get(i);
            mc.font.draw(matrixStack, TextFormatting.WHITE + str, x + 3 + (centeredString ? -(int)(mc.font.width(str) / 2) : 0), y + 3 + (i * 9), 0xFFFFFF);
        }

        GL11.glTranslatef(0, 0, 0);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }



}
