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
public class ATMOthersActionPacket extends IPacket {

    private long amount;
    private String id;

    public ATMOthersActionPacket(String id, long amount) {
        this.amount = amount;
        this.id = id;
    }

    public ATMOthersActionPacket(PacketBuffer buffer) {
        this.amount = buffer.readLong();
        this.id = buffer.readUtf();
    }

    @Override
    public void toBytes(PacketBuffer buffer) {
        buffer.writeLong(amount);
        buffer.writeUtf(id);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity entity = ctx.get().getSender();
            if (entity != null) {
                if (entity.containerMenu instanceof ATMContainer) {
                    ATMContainer container = (ATMContainer) entity.containerMenu;
                    container.processOthersAction(this.id, this.amount);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
