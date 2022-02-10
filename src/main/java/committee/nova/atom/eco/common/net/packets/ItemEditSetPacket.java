package committee.nova.atom.eco.common.net.packets;

import committee.nova.atom.eco.common.containers.ItemEditContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/9 8:12
 * Version: 1.0
 */
public class ItemEditSetPacket extends IPacket {
    private ItemStack stack;

    public ItemEditSetPacket(ItemStack stack) {
        this.stack = stack;
    }

    public ItemEditSetPacket(PacketBuffer buffer) {
        this.stack = buffer.readItem();
    }

    @Override
    public void toBytes(PacketBuffer buffer) {
        buffer.writeItemStack(stack, false);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
        {
            ServerPlayerEntity entity = ctx.get().getSender();
            if (entity != null) {
                if (entity.containerMenu instanceof ItemEditContainer) {
                    ItemEditContainer container = (ItemEditContainer) entity.containerMenu;
                    container.setItem(this.stack);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
