package committee.nova.atom.eco.client.widegts;

import com.mojang.blaze3d.matrix.MatrixStack;
import committee.nova.atom.eco.Static;
import committee.nova.atom.eco.api.market.MarketEntity;
import committee.nova.atom.eco.utils.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/8 19:57
 * Version: 1.0
 */
public class MarketTab {
    private final List<MarketButton> marketButtons;
    private final List<MarketEntity> marketList;

    private final ScreenRect rect;
    private final String name;
    private final int marketButtonsX;
    private final int marketButtonsY;
    private final ItemRenderer itemRender;

    public MarketTab(List<MarketEntity> marketList, int tabX, int tabY, String name, int marketButtonsX, int marketButtonsY, ItemRenderer itemRender) {
        this.marketList = marketList;
        marketButtons = new ArrayList<>();
        this.rect = new ScreenRect(tabX, tabY, 48, 12);
        this.name = name;
        this.marketButtonsX = marketButtonsX;
        this.marketButtonsY = marketButtonsY;
        this.itemRender = itemRender;
    }

    public ScreenRect getRect() {
        return rect;
    }

    public List<MarketButton> getMarketButtons() {
        return marketButtons;
    }

    public MarketButton addButton(int index, Button.IPressable pressable) {

        if (index <= 50) {
            MarketButton button = new MarketButton(marketList, index, 0, 0, itemRender, pressable);
            button.active = false;
            marketButtons.add(button);
            return button;
        }

        return null;
    }

    public void updateButtons() {

        int count = 0;

        for (MarketButton button : marketButtons) {

            if (button.active) {

                int rowSize = 10;

                int xPos = (marketButtonsX) + ((count % rowSize) * 18);
                int yPos = (marketButtonsY) + ((count / rowSize) * 18);
                int size = 16;

                button.setRect(new ScreenRect(xPos, yPos, size, size));
                count++;
            }
        }
    }

    public void enableButtons(boolean value) {

        for (MarketButton button : marketButtons) {
            button.active = value;
        }
    }

    public void renderTab(MatrixStack matrixStack) {
        ScreenUtil.drawCenteredString(matrixStack, name, rect.x + rect.width / 2, rect.y, 0, 0xFFFFFF);
    }

    public void renderSelectedTab() {
        Minecraft.getInstance().getTextureManager().bind(Static.GUI_TEXTURES);
        ScreenUtil.drawRect(rect.x, rect.y + 9, 0, 0, 100, rect.width - 1, 1);
    }
}
