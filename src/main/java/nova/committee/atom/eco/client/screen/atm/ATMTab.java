package nova.committee.atom.eco.client.screen.atm;

import com.mojang.blaze3d.vertex.PoseStack;
import nova.committee.atom.eco.client.widegts.TabButton;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/11 10:33
 * Version: 1.0
 */
public abstract class ATMTab implements TabButton.ITab {
    protected final ATMScreen screen;

    protected ATMTab(ATMScreen screen) {
        this.screen = screen;
    }

    public abstract void init();

    public abstract void preRender(PoseStack pose, int mouseX, int mouseY, float partialTicks);

    public abstract void postRender(PoseStack pose, int mouseX, int mouseY);

    public abstract void tick();

    public abstract void onClose();

    public boolean blockInventoryClosing() {
        return false;
    }

    public final int getColor() {
        return 0xFFFFFF;
    }
}
