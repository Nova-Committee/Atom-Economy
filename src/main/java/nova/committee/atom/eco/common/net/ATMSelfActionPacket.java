package nova.committee.atom.eco.common.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import nova.committee.atom.eco.api.common.net.IPacket;
import nova.committee.atom.eco.common.menu.ATMMenu;

import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/10 8:24
 * Version: 1.0
 */
public class ATMSelfActionPacket extends IPacket {

    private final long amount;
    private final boolean deposit;

    public ATMSelfActionPacket(long amount, boolean deposit) {
        this.amount = amount;
        this.deposit = deposit;
    }

    public ATMSelfActionPacket(FriendlyByteBuf buffer) {
        this.amount = buffer.readLong();
        this.deposit = buffer.readBoolean();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeLong(amount);
        buffer.writeBoolean(deposit);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer entity = ctx.get().getSender();
            if (entity != null) {
                if (entity.containerMenu instanceof ATMMenu container) {
                    container.processSelfAction(this.amount, this.deposit);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
