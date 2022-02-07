package committee.nova.atom.eco.common.net.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ReplyGuiUpdatePacket extends IPacket{

    public final BlockPos pos;
    public final CompoundNBT nbt;

    public ReplyGuiUpdatePacket(BlockPos pos, CompoundNBT nbt){
        this.pos = pos;
        this.nbt = nbt;
    }

    public ReplyGuiUpdatePacket(PacketBuffer buffer){
        pos = buffer.readBlockPos();
        nbt = buffer.readNbt();
    }

    @Override
    public void toBytes(PacketBuffer buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeNbt(nbt);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->{
            DistExecutor.safeRunWhenOn(Dist.CLIENT,() -> () -> {

                World world = Minecraft.getInstance().level;
                if(world != null){
                    TileEntity tile = world.getBlockEntity(pos);
                    //TODO tile tag更新

                }


            });


        });
    }
}
