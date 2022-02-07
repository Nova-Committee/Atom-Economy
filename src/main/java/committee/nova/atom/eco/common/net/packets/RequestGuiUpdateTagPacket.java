package committee.nova.atom.eco.common.net.packets;

import committee.nova.atom.eco.common.net.PacketHandler;
import committee.nova.atom.eco.common.tiles.base.IGuiUpdateHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestGuiUpdateTagPacket extends IPacket{

    public final BlockPos pos;

    public RequestGuiUpdateTagPacket (BlockPos pos){
        this.pos = pos;
    }

    public RequestGuiUpdateTagPacket(PacketBuffer buffer){
        this.pos = buffer.readBlockPos();
    }

    @Override
    public void toBytes(PacketBuffer buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender = ctx.get().getSender();
            if(sender != null){
                World world = sender.level;
                if(world != null){
                    TileEntity tile = world.getBlockEntity(pos);
                    if (tile instanceof IGuiUpdateHandler){
                        IGuiUpdateHandler handler = (IGuiUpdateHandler) tile;
                        CompoundNBT tag = handler.getGuiUpdateTag();
                        PacketHandler.INSTANCE.sendTo(new ReplyGuiUpdatePacket(pos, tag), sender.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                    }
                }
            }

        });

    }
}
