package nova.committee.atom.eco.client.widegts;

import com.google.common.base.Supplier;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import nova.committee.atom.eco.Static;
import nova.committee.atom.eco.core.model.UseAccount;
import nova.committee.atom.eco.util.UserUtil;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/11 18:51
 * Version: 1.0
 */
public class AccountButton extends Button {

    public static final int HEIGHT = 14;
    public static final int TEXT_COLOR = 0xFFFFFF;
    public static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Static.MOD_ID, "textures/gui/container/accountbutton.png");
    private final Font font;
    private final Size size;
    private final Supplier<UseAccount> useAccountSupplier;
    private final Supplier<Boolean> selectedUseAccount;

    public AccountButton(int x, int y, Size size, OnPress press, Font font, @Nonnull Supplier<UseAccount> accSource, @Nonnull Supplier<Boolean> selectedAcc) {
        super(x, y, size.width, HEIGHT, new TextComponent(""), press);
        this.font = font;
        this.size = size;
        this.useAccountSupplier = accSource;
        this.selectedUseAccount = selectedAcc;
    }

    public UseAccount getAccounts() {
        return this.useAccountSupplier.get();
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible || this.getAccounts() == null)
            return;

        //Render Background
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        this.blit(pose, this.x, this.y, 0, (selectedUseAccount.get() ? HEIGHT : 0) + this.size.guiPos, this.size.width, HEIGHT);

        //Render Team Name
        this.font.draw(pose, this.fitString(UserUtil.getNameByUUID(UUID.fromString(this.getAccounts().getId()))), this.x + 2, this.y + 2, TEXT_COLOR);

    }

    private String fitString(String string) {
        //If it already fits, don't bother editing it
        if (this.font.width(string) <= this.width - 4)
            return string;
        //Shorten 1 char at a time, but assume a ... will be at the end
        while (this.font.width(string + "...") > this.width - 4 && string.length() > 0)
            string = string.substring(0, string.length() - 1);
        return string + "...";
    }

    public enum Size {
        WIDE(123, 0), NORMAL(62, 1), NARROW(41, 2);
        public final int width;
        public final int guiPos;

        Size(int width, int guiPos) {
            this.width = width;
            this.guiPos = guiPos * HEIGHT * 2;
        }
    }
}
