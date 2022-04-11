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
public class ATMOthersActionPacket extends IPacket {

    private final long amount;
    private final String id;

    public ATMOthersActionPacket(String id, long amount) {
        this.amount = amount;
        this.id = id;
    }

    public ATMOthersActionPacket(FriendlyByteBuf buffer) {
        this.amount = buffer.readLong();
        this.id = buffer.readUtf();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeLong(amount);
        buffer.writeUtf(id);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer entity = ctx.get().getSender();
            if (entity != null) {
                if (entity.containerMenu instanceof ATMMenu container) {
                    container.processOthersAction(this.id, this.amount);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
