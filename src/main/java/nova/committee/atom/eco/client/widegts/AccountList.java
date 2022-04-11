package nova.committee.atom.eco.client.widegts;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Mth;
import nova.committee.atom.eco.util.UserUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class AccountList extends ContainerObjectSelectionList<AccountList.AccountEntry> {

    private final Font font;

    public AccountList(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.font = mcIn.font;
    }

    @Override
    public int getRowWidth() {
        return width - 10;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.x0 + width - 10;
    }

    @Override
    public void setSelected(@Nullable AccountEntry pEntry) {
        super.setSelected(pEntry);
        pEntry.onSelected.accept(pEntry);
    }

    @Override
    public int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
    }

    Font getFont() {
        return font;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        int i = this.getScrollbarPosition();
        int j = i + 6;
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
        float f = 32.0F;
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(this.x0, this.y1, 0.0D).uv((float) this.x0 / 32.0F, (float) (this.y1 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        bufferbuilder.vertex(this.x1, this.y1, 0.0D).uv((float) this.x1 / 32.0F, (float) (this.y1 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        bufferbuilder.vertex(this.x1, this.y0, 0.0D).uv((float) this.x1 / 32.0F, (float) (this.y0 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        bufferbuilder.vertex(this.x0, this.y0, 0.0D).uv((float) this.x0 / 32.0F, (float) (this.y0 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        tessellator.end();
        int k = this.getRowLeft();
        int l = this.y0 + 4 - (int) this.getScrollAmount();


        this.renderList(matrixStack, k, l, mouseX, mouseY, partialTicks);

        int k1 = getMaxScroll();
        if (k1 > 0) {
            int l1 = (int) ((float) ((this.y1 - this.y0) * (this.y1 - this.y0)) / (float) this.getMaxPosition());
            l1 = Mth.clamp(l1, 32, this.y1 - this.y0 - 8);
            int i2 = (int) this.getScrollAmount() * (this.y1 - this.y0 - l1) / k1 + this.y0;
            if (i2 < this.y0) {
                i2 = this.y0;
            }

            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferbuilder.vertex(i, this.y1, 0.0D).uv(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(j, this.y1, 0.0D).uv(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(j, this.y0, 0.0D).uv(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(i, this.y0, 0.0D).uv(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(i, i2 + l1, 0.0D).uv(0.0F, 1.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(j, i2 + l1, 0.0D).uv(1.0F, 1.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(j, i2, 0.0D).uv(1.0F, 0.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(i, i2, 0.0D).uv(0.0F, 0.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(i, i2 + l1 - 1, 0.0D).uv(0.0F, 1.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex(j - 1, i2 + l1 - 1, 0.0D).uv(1.0F, 1.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex(j - 1, i2, 0.0D).uv(1.0F, 0.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex(i, i2, 0.0D).uv(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();
            tessellator.end();
        }

        this.renderDecorations(matrixStack, mouseX, mouseY);
        RenderSystem.enableTexture();

        RenderSystem.disableBlend();
    }

    public static class AccountEntry extends ContainerObjectSelectionList.Entry<AccountEntry> {
        private final String id;
        private final String name;
        private final AccountList parent;

        private final Consumer<AccountEntry> onSelected;


        public AccountEntry(String id, AccountList parent, @Nullable Consumer<AccountEntry> onSelected) {

            this.id = id;

            this.parent = parent;
            this.onSelected = onSelected;

            this.name = "账户:" + UserUtil.getNameByUUID(UUID.fromString(id));
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public void render(PoseStack matrix, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isMouseOver,
                           float partialTicks) {

            Font font = this.parent.getFont();
            if (id == null)
                font.draw(matrix, I18n.get("gui.atomeco.transfer.all_account"), left + 3, top + 2, 0xFFF0F0);
            else
                font.draw(matrix, name, left + 3, top + 2, 0xFFFFFF);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return null;
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            parent.setSelected(this);
            if (onSelected != null) {
                onSelected.accept(this);
            }
            return false;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return null;
        }


    }

}
