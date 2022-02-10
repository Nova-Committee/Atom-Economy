package committee.nova.atom.eco.common.net.packets;

import committee.nova.atom.eco.common.config.ConfigUtil;
import committee.nova.atom.eco.common.items.GenericMoneyItem;
import committee.nova.atom.eco.common.items.WalletItem;
import committee.nova.atom.eco.utils.CurrencyUtil;
import committee.nova.atom.eco.utils.ItemUtil;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/10 8:07
 * Version: 1.0
 */
public class WalletPacket extends IPacket {
    private int multiplier;

    public WalletPacket() {
    }

    /**
     * 处理从钱包中取钱
     *
     * @param multiplier 处理shift和ctrl
     */
    public WalletPacket(int multiplier) {
        this.multiplier = multiplier;
    }

    public WalletPacket(PacketBuffer buf) {
        multiplier = buf.readInt();
    }

    @Override
    public void toBytes(PacketBuffer buffer) {
        buffer.writeInt(multiplier);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ServerPlayerEntity player = ctx.get().getSender();

            if (player != null) {

                ItemStack walletStack = CurrencyUtil.getCurrentWalletStack(player);

                //检查钱包是否存在
                if (walletStack != null) {

                    WalletItem wallet = (WalletItem) walletStack.getItem();

                    Item item = ConfigUtil.MONEY;
                    int price = (int) ((GenericMoneyItem) item).getWorth();
                    price *= multiplier;

                    //处理将新余额同步到服务器并生成实体货币。
                    if (!walletStack.isEmpty()) {
                        CurrencyUtil.withdrawFromWallet(walletStack, price);
                        ItemUtil.spawnStackAtEntity(player.level, player, new ItemStack(item, multiplier));
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
