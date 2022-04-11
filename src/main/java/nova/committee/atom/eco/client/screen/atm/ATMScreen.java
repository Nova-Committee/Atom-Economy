package nova.committee.atom.eco.client.screen.atm;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import nova.committee.atom.eco.Static;
import nova.committee.atom.eco.client.widegts.TabButton;
import nova.committee.atom.eco.common.menu.ATMMenu;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ATMScreen extends AbstractContainerScreen<ATMMenu> {
    public static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Static.MOD_ID, "textures/gui/container/atm.png");

    int currentTabIndex = 0;
    List<ATMTab> tabs = Lists.newArrayList(new WithDrawTab(this), new DepositTab(this), new TransferTab(this));
    List<AbstractWidget> tabWidgets = Lists.newArrayList();
    List<GuiEventListener> tabListeners = Lists.newArrayList();
    List<TabButton> tabButtons = Lists.newArrayList();

    public ATMScreen(ATMMenu p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_);
        this.imageHeight = 183;
        this.imageWidth = 175;
    }

    public List<ATMTab> getTabs() {
        return this.tabs;
    }

    public ATMTab currentTab() {
        return tabs.get(this.currentTabIndex);
    }

    @Override
    protected void renderBg(@NotNull PoseStack pose, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        this.blit(pose, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        try {
            this.currentTab().preRender(pose, mouseX, mouseY, partialTicks);
            this.tabWidgets.forEach(widget -> widget.render(pose, mouseX, mouseY, partialTicks));
        } catch (Exception e) {
            Static.LOGGER.error(e.getMessage());
        }

    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        //this.font.draw(poseStack, this.playerInventoryTitle, 8.0f, (this.imageHeight - 94), 0x404040);//物品栏标题
    }

    @Override
    protected void init() {
        super.init();

        for (int i = 0; i < this.tabs.size(); ++i) {
            TabButton button = this.addRenderableWidget(new TabButton(this::clickedOnTab, this.font, this.tabs.get(i)));
            button.reposition(this.leftPos - TabButton.SIZE, this.topPos + i * TabButton.SIZE, 3);
            button.active = i != this.currentTabIndex;
            this.tabButtons.add(button);
        }

        this.currentTab().init();

    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(pose);

        //Render the tab buttons & background
        super.render(pose, mouseX, mouseY, partialTicks);

        //Render the current tab
        try {
            this.currentTab().postRender(pose, mouseX, mouseY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.renderTooltip(pose, mouseX, mouseY);

        //Render the tab button tooltips
        for (TabButton tabButton : this.tabButtons) {
            if (tabButton.isMouseOver(mouseX, mouseY))
                this.renderTooltip(pose, tabButton.tab.getTooltip(), mouseX, mouseY);
        }

    }

    public void changeTab(int tabIndex) {

        //Close the old tab
        this.currentTab().onClose();
        this.tabButtons.get(this.currentTabIndex).active = true;
        this.currentTabIndex = tabIndex;
        this.tabButtons.get(this.currentTabIndex).active = false;

        //Clear the previous tabs widgets
        this.tabWidgets.clear();
        this.tabListeners.clear();

        //Initialize the new tab
        this.currentTab().init();

    }

    private void clickedOnTab(Button tab) {
        int tabIndex = this.tabButtons.indexOf(tab);
        if (tabIndex < 0)
            return;
        this.changeTab(tabIndex);
    }

    public void containerTick() {
        this.currentTab().tick();
    }

    public <T extends AbstractWidget> T addRenderableTabWidget(T widget) {
        this.tabWidgets.add(widget);
        return widget;
    }

    public void removeRenderableTabWidget(AbstractWidget widget) {
        if (this.tabWidgets.contains(widget))
            this.tabWidgets.remove(widget);
    }

    public <T extends GuiEventListener> T addTabListener(T listener) {
        this.tabListeners.add(listener);
        return listener;
    }

    public void removeTabListener(GuiEventListener listener) {
        if (this.tabListeners.contains(listener))
            this.tabListeners.remove(listener);
    }

    public Font getFont() {
        return this.font;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        List<? extends GuiEventListener> coreListeners = super.children();
        List<GuiEventListener> listeners = Lists.newArrayList();
        listeners.addAll(coreListeners);
        listeners.addAll(this.tabWidgets);
        listeners.addAll(this.tabListeners);
        return listeners;
    }

    @Override
    public boolean keyPressed(int p_97765_, int p_97766_, int p_97767_) {
        InputConstants.Key mouseKey = InputConstants.getKey(p_97765_, p_97766_);
        if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey) && this.currentTab().blockInventoryClosing()) {
            return true;
        }
        return super.keyPressed(p_97765_, p_97766_, p_97767_);
    }
}
