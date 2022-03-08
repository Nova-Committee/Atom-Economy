package committee.nova.atom.eco.common.net.packets;

import committee.nova.atom.eco.common.containers.ATMContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/10 8:24
 * Version: 1.0
 */
public class ATMSelfActionPacket extends IPacket {

    private long amount;
    private boolean deposit;

    public ATMSelfActionPacket(long amount, boolean deposit) {
        this.amount = amount;
        this.deposit = deposit;
    }

    public ATMSelfActionPacket(PacketBuffer buffer) {
        this.amount = buffer.readLong();
        this.deposit = buffer.readBoolean();
    }

    @Override
    public void toBytes(PacketBuffer buffer) {
        buffer.writeLong(amount);
        buffer.writeBoolean(deposit);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity entity = ctx.get().getSender();
            if (entity != null) {
                if (entity.containerMenu instanceof ATMContainer) {
                    ATMContainer container = (ATMContainer) entity.containerMenu;
                    container.processSelfAction(this.amount, this.deposit);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
