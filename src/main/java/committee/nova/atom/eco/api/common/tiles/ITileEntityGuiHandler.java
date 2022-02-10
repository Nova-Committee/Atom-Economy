package committee.nova.atom.eco.api.common.tiles;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/9 21:57
 * Version: 1.0
 */
public interface ITileEntityGuiHandler {
    Container getTileContainer(int windowId, PlayerInventory playerInv);

    @OnlyIn(Dist.CLIENT)
    ContainerScreen getTileGuiContainer(int windowId, PlayerInventory playerInv);
}
