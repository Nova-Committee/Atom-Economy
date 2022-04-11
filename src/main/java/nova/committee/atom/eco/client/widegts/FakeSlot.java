package nova.committee.atom.eco.client.widegts;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import nova.committee.atom.eco.util.ScreenUtil;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/11 9:46
 * Version: 1.0
 */
public class FakeSlot extends Button {
    private final ScreenRect rect;
    private final Screen screen;
    private final ItemRenderer itemRender;
    private ItemStack stack = new ItemStack(Items.AIR);

    /**
     * 物品显示框
     *
     * @param x            x
     * @param y            y
     * @param screen       宽
     * @param itemRenderer 渲染物品
     * @param onPress      事件
     */
    public FakeSlot(int x, int y, Screen screen, ItemRenderer itemRenderer, Button.OnPress onPress) {
        super(x, y, 16, 16, new TextComponent(""), onPress);
        rect = new ScreenRect(this.x, this.y, width, height);
        this.screen = screen;
        this.itemRender = itemRenderer;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {

        if (this.visible && !stack.isEmpty()) {

            ScreenUtil.drawItemStack(itemRender, getItemStack(), rect.x, rect.y);

            if (rect.contains(mouseX, mouseY)) {
                screen.renderTooltip(poseStack, screen.getTooltipFromItem(getItemStack()), getItemStack().getTooltipImage(), mouseX, mouseY);
            }
        }
    }

    private ItemStack getItemStack() {
        return stack;
    }

    public void setItemStack(ItemStack stack) {
        this.stack = stack;
    }
}
