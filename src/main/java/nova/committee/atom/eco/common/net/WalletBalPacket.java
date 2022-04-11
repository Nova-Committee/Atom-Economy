package nova.committee.atom.eco.common.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import nova.committee.atom.eco.api.common.net.IPacket;
import nova.committee.atom.eco.common.item.WalletItem;
import nova.committee.atom.eco.common.menu.WalletMenu;

import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/10 14:36
 * Version: 1.0
 */
public class WalletBalPacket extends IPacket {
    private final ItemStack item;

    public WalletBalPacket(ItemStack item) {
        this.item = item;
    }

    public WalletBalPacket(FriendlyByteBuf buf) {
        this.item = buf.readItem();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeItem(item);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var walletCon = WalletItem.getInventory(item);
            var player = ctx.get().getSender();
            long bal = 0;
            if (player.containerMenu instanceof WalletMenu walletMenu && item.getItem() instanceof WalletItem walletItem) {
                // WalletItem.setBal(walletMenu.countInWallet());
            }
        });
        ctx.get().setPacketHandled(true);

    }
}
