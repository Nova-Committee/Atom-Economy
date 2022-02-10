package committee.nova.atom.eco.client.widegts;

import committee.nova.atom.eco.api.client.ItemStackButton;
import committee.nova.atom.eco.api.market.MarketEntity;
import committee.nova.atom.eco.core.MarketDataManager;
import committee.nova.atom.eco.utils.ScreenUtil;
import committee.nova.atom.eco.utils.text.FormatUtil;
import committee.nova.atom.eco.utils.text.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:市场里的按钮
 * Author: cnlimiter
 * Date: 2022/2/8 20:01
 * Version: 1.0
 */
@OnlyIn(Dist.CLIENT)
public class MarketButton extends ItemStackButton {

    private final List<MarketEntity> marketList;
    private final int marketListIndex;

    /**
     * 市场里的按钮，可以显示单价，介绍
     *
     * @param marketList      商品集合
     * @param marketListIndex 商品的index
     * @param pressable       当被按下时触发
     */
    public MarketButton(List<MarketEntity> marketList, int marketListIndex, int x, int y, ItemRenderer itemRender, Button.IPressable pressable) {
        super(x, y, itemRender, pressable);
        this.marketList = marketList;
        this.marketListIndex = marketListIndex;
    }

    public int getMarketListIndex() {
        return marketListIndex;
    }

    @Override
    public ItemStack getRenderedStack() {
        return MarketDataManager.getStackFromList(marketList, marketListIndex);
    }

    public void renderSelectionBox() {

        if (this.visible && this.active) {
            ScreenUtil.bindGuiTextures();
            ScreenUtil.drawRect(rect.x - 1, rect.y - 1, 0, 91, 0, 19, 19);
        }
    }

    @Override
    public String[] getTooltip() {

        MarketEntity marketItem = marketList.get(marketListIndex);

        List<String> list = new ArrayList<>();
        List<ITextComponent> lore = getRenderedStack().getTooltipLines(Minecraft.getInstance().player, ITooltipFlag.TooltipFlags.NORMAL);

        list.add(marketItem.amount + "x " + getRenderedStack().getDisplayName().getString());
        list.add("价格: " + TextFormatting.GOLD + FormatUtil.printCurrency(marketItem.value));

        if (lore.size() > 1) {

            if (Screen.hasShiftDown()) {

                for (ITextComponent component : lore) {
                    list.add(component.getString());
                }

                list.remove(2);

                StringUtil.removeNullsFromList(list);
                StringUtil.removeCharFromList(list, "Shift", "Ctrl");
            } else {
                list.add(TextFormatting.GRAY + "[" + TextFormatting.AQUA + "Shift" + TextFormatting.GRAY + "]" + TextFormatting.GRAY + " Info");
            }
        }

        return StringUtil.getArrayFromList(list);
    }
}
