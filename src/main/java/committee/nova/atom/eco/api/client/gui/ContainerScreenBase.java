package committee.nova.atom.eco.api.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import committee.nova.atom.eco.api.common.containers.BaseContainer;
import committee.nova.atom.eco.api.common.tiles.BaseInventoryTile;
import committee.nova.atom.eco.api.security.ISecurity;
import committee.nova.atom.eco.client.widegts.ScreenRect;
import committee.nova.atom.eco.utils.CurrencyUtil;
import committee.nova.atom.eco.utils.ScreenUtil;
import committee.nova.atom.eco.utils.math.MathUtil;
import committee.nova.atom.eco.utils.text.StringUtil;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/10 7:47
 * Version: 1.0
 */
@OnlyIn(Dist.CLIENT)
public abstract class ContainerScreenBase<T extends BaseContainer> extends ContainerScreen<Container> {

    protected static final int TEXT_COLOR_GRAY = 0x555555;

    protected final PlayerInventory playerInventory;
    protected final PlayerEntity player;
    private final Container container;

    protected int leftTabOffset;
    private int rightTabOffset;

    private int currentProgress;

    protected ContainerScreenBase(Container container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.leftPos = 0;
        this.topPos = 0;
        this.width = getGuiSizeX();
        this.height = getGuiSizeY();
        this.container = container;
        this.playerInventory = playerInventory;
        this.player = playerInventory.player;

        leftTabOffset = 4;
        rightTabOffset = 4;

        titleLabelX = 1000;
    }

    /**
     * 绑定textures
     */
    protected abstract String getGuiTextureName();

    /**
     * 渲染背景层
     */
    protected abstract void drawGuiBackground(MatrixStack matrixStack, int mouseY, int mouseX);

    /**
     * 渲染前景层
     */
    protected abstract void drawGuiForeground(MatrixStack matrixStack, int mouseX, int mouseY);

    /**
     * 用于确定 GUI 的宽度。
     */
    public int getGuiSizeX() {
        return 176;
    }

    /**
     * 用于确定 GUI 的高度。
     */
    protected int getGuiSizeY() {
        return 176;
    }

    /**
     * 用于确定 GUI 的左部。
     */
    public int getScreenX() {
        return (this.width - getGuiSizeX()) / 2;
    }

    /**
     * 用于确定 GUI 的顶部。
     */
    public int getScreenY() {
        return (this.height - getGuiSizeY()) / 2;
    }

    /**
     * @return 绑定的tile
     */
    public BaseInventoryTile getTileEntity() {

        if (container instanceof BaseContainer) {

            BaseContainer containerBase = (BaseContainer) container;

            if (containerBase.tileEntity != null) {
                return containerBase.tileEntity;
            }
        }

        return null;
    }

    /**
     * tick
     */
    @Override
    public void tick() {
        super.tick();

        //If the Tile Entity has a progress value, sync it.
//        if (getTileEntity() != null && getTileEntity() instanceof TileEntityUpgradable) {
//            this.currentProgress = ((TileEntityUpgradable) getTileEntity()).currentProgress;
//        }
    }

    /**
     * 基础渲染方法
     */
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float f) {

        //Renders the transparent black background behind the GUI.
        renderBackground(matrixStack);

        addGraphicsBeforeRendering(matrixStack);

        super.render(matrixStack, mouseX, mouseY, f);

        addGraphicsAfterRendering(matrixStack, mouseX, mouseY);
        drawGuiForeground(matrixStack, mouseX, mouseY);

        renderTooltip(matrixStack, mouseX, mouseY);
    }

    /**
     * 处理渲染库存纹理及其名称。此方法调用 drawGuiBackground 来添加任何特定的内容。
     */
    @Override
    protected void renderBg(MatrixStack matrixStack, float f, int mouseX, int mouseY) {

        leftTabOffset = 4;
        rightTabOffset = 4;

        ScreenUtil.bindTexture(getGuiTextureName());
        ScreenUtil.drawRect(getScreenX(), getScreenY(), 0, 0, 0, getGuiSizeX(), getGuiSizeY());

        drawGuiBackground(matrixStack, mouseY, mouseX);
    }

    /**
     * 在inventory之前渲染任何东西
     */
    private void addGraphicsBeforeRendering(MatrixStack matrixStack) {

        ScreenUtil.bindGuiTextures();

        if (getTileEntity() != null) {

            BaseInventoryTile tileEntity = getTileEntity();

            //If the Tile Entity is upgradeable, render its slots & progress bar.
//            if (tileEntity instanceof TileEntityUpgradable) {
//
//                TileEntityUpgradable tileEntityUpgradable = (TileEntityUpgradable) tileEntity;
//
//                addUpgradeSlot(matrixStack, 0);
//                addUpgradeSlot(matrixStack, 1);
//
//                addProgressBar(currentProgress, tileEntityUpgradable.getMaxProgress());
//            }
        }
    }

    /**
     * 在inventory之后渲染任何东西
     */
    private void addGraphicsAfterRendering(MatrixStack matrixStack, int mouseX, int mouseY) {

        GL11.glDisable(GL11.GL_LIGHTING);
        ScreenUtil.bindGuiTextures();

        if (minecraft != null && getTileEntity() != null) {

            BaseInventoryTile tileEntity = getTileEntity();

            //If the Tile Entity has security, render its owners name.
            if (tileEntity instanceof ISecurity) {

                ISecurity tileEntitySecurity = (ISecurity) tileEntity;

                String name = tileEntitySecurity.getSecurityProfile().getOwnerUUID();
                int width = minecraft.font.width(name) + 7;

                ScreenUtil.drawCappedRect(getScreenX() + (getGuiSizeX() / 2) - (width / 2), getScreenY() + getGuiSizeY() - 1, 0, 218, 0, width, 13, 256, 22);
                ScreenUtil.drawCenteredString(matrixStack, name, getScreenX() + (getGuiSizeX() / 2) + (width % 2 == 0 ? 0 : 1), getScreenY() + getGuiSizeY() + 2, 50, TEXT_COLOR_GRAY);
                GL11.glColor3f(1, 1, 1);
            }


            //If the Tile Entity has a progress bar, render it. (Currently Disabled)
            if (tileEntity instanceof IProgress) {

                IProgress tileEntityProgress = (IProgress) tileEntity;
                BaseContainer containerBase = (BaseContainer) container;

                GL11.glColor3f(1, 1, 1);
                //addProgressHoveringText(matrixStack, mouseX, mouseY, currentProgress, tileEntityProgress.getMaxProgress());
            }
        }
    }


    /**
     * 渲染进度条
     */
    private void addProgressBar(int progress, int maxProgress) {

        ScreenUtil.bindGuiTextures();
        ScreenRect rect = new ScreenRect(getScreenX() - 13, getScreenY() + leftTabOffset, 13, 35);

        int scale = MathUtil.scaleInt(progress, maxProgress, 26);

        ScreenUtil.drawRect(rect.x, rect.y, 0, 37, 0, rect.width, rect.height);
        ScreenUtil.drawRect(getScreenX() - 8, getScreenY() + 30 + leftTabOffset - scale, 13, 62 - scale, 0, 5, scale);
    }

    /**
     * 渲染进度条的悬浮文本
     */
    private void addProgressHoveringText(MatrixStack matrixStack, int mouseX, int mouseY, int progress, int maxProgress) {

        ScreenRect rect = new ScreenRect(getScreenX() - 13, getScreenY() + leftTabOffset, 13, 35);
        ScreenUtil.drawHoveringTextBox(matrixStack, mouseX, mouseY, 170, rect, "进度: " + MathUtil.scaleInt(progress, maxProgress, 100) + "%");
    }

    /**
     * 渲染一个信息图标
     */
    protected void addInfoIcon(int index) {

        GL11.glDisable(GL11.GL_LIGHTING);
        ScreenUtil.bindGuiTextures();
        ScreenUtil.drawRect(getScreenX() - 13, getScreenY() + leftTabOffset, (index * 13), 72, 2, 13, 15);
    }

    /**
     * 渲染悬浮文本
     */
    protected void addInfoHoveringText(MatrixStack matrixStack, int mouseX, int mouseY, String... text) {
        ScreenRect rect = new ScreenRect(getScreenX() - 13, getScreenY() + leftTabOffset, 13, 15);
        ScreenUtil.drawHoveringTextBox(matrixStack, mouseX, mouseY, 170, rect, text);
    }

    /**
     * 渲染tab
     */
    protected void addCurrencyTab(MatrixStack matrixStack, int mouseX, int mouseY, int currency, int maxCurrency) {

        if (minecraft != null) {

            String fullName = StringUtil.printCommas(currency) + " / " + CurrencyUtil.printCurrency(maxCurrency);

            int fullWidth = minecraft.font.width(fullName) + 6;

            ScreenRect rect = new ScreenRect(getScreenX() - fullWidth, getScreenY() + leftTabOffset, fullWidth, 15);
            String text = CurrencyUtil.printCurrency(currency);

            if (rect.contains(mouseX, mouseY)) {
                text = fullName;
            }

            addLeftInfoTab(matrixStack, text, 15);
        }
    }

    /**
     * Renders a tab on the left of the inventory.
     */
    private void addLeftInfoTab(MatrixStack matrixStack, String text, int sizeY) {

        if (minecraft != null) {

            int width = minecraft.font.width(text) + 6;

            ScreenUtil.bindGuiTextures();
            ScreenUtil.drawCappedRect(getScreenX() - width, getScreenY() + leftTabOffset, 0, 218, 10, width, sizeY, 255, 22);

            if (!text.isEmpty()) {
                GL11.glPushMatrix();
                GL11.glColor3f(0.35F, 0.35F, 0.35F);
                GL11.glTranslatef(0, 0, 5);
                minecraft.font.draw(matrixStack, text, getScreenX() - width + 4, getScreenY() + (float) (sizeY / 2) - 3 + leftTabOffset, TEXT_COLOR_GRAY);
                GL11.glColor3f(1, 1, 1);
                GL11.glPopMatrix();
            }

            leftTabOffset += (sizeY + 2);
        }
    }

    /**
     * Renders a tab on the right of the inventory.
     */
    private void addRightInfoTab(MatrixStack matrixStack, String text, int sizeAdd, int sizeY) {

        if (minecraft != null) {

            int width = minecraft.font.width(text) + sizeAdd + 7;

            ScreenUtil.bindGuiTextures();
            ScreenUtil.drawCappedRect(getScreenX() + getGuiSizeX() - 1, getScreenY() + rightTabOffset, 0, 218, -40, width, sizeY, 256, 22);

            if (!text.isEmpty()) {
                GL11.glPushMatrix();
                GL11.glTranslatef(0, 0, 5);
                GL11.glColor3f(0.35F, 0.35F, 0.35F);
                minecraft.font.draw(matrixStack, text, getScreenX() + getGuiSizeX() + 4, getScreenY() + (float) (sizeY / 2) - 3 + rightTabOffset, TEXT_COLOR_GRAY);
                GL11.glColor3f(1, 1, 1);
                GL11.glPopMatrix();
            }

            rightTabOffset += (sizeY + 2);
        }
    }
}