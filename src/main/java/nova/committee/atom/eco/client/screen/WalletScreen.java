package nova.committee.atom.eco.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import nova.committee.atom.eco.Static;
import nova.committee.atom.eco.common.menu.WalletMenu;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/10 18:41
 * Version: 1.0
 */
public class WalletScreen extends AbstractContainerScreen<WalletMenu> {

    private static final ResourceLocation backgroundLocation = new ResourceLocation(Static.MOD_ID, "textures/gui/container" +
            "/wallet.png");

    public WalletScreen(WalletMenu p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_);
        this.imageWidth = 175;
        this.imageHeight = 165;
    }


    @Override
    public void render(PoseStack poseStack, int x, int y, float p_97798_) {
        this.renderBackground(poseStack);
        super.render(poseStack, x, y, p_97798_);
        this.renderTooltip(poseStack, x, y);

    }

    @Override
    protected void renderBg(PoseStack poseStack, float p_97788_, int p_97789_, int p_97790_) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, backgroundLocation);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(poseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}
