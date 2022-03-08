package committee.nova.atom.eco.api.common.tiles;

import committee.nova.atom.eco.utils.math.Location;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;

/**
 * Description:需要传输数据的tile
 * Author: cnlimiter
 * Date: 2022/2/8 15:28
 * Version: 1.0
 */
public abstract class BaseTile extends TileEntity implements ITickableTileEntity {

    public BaseTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    @Override
    public void tick() {

    }

//    protected UnitChatMessage getUnitName (PlayerEntity player) {
//        return new UnitChatMessage(getLocation().getBlock().getName().getString(), player);
//    }

    public Location getLocation() {
        return new Location(level, worldPosition);
    }

    public void markForUpdate() {

        if (level != null) {
            setChanged();
            level.blockEvent(getBlockPos(), getBlockState().getBlock(), 1, 1);
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
            level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        load(getBlockState(), pkt.getTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        load(state, tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        save(nbtTagCompound);
        int tileEntityType = 64;
        return new SUpdateTileEntityPacket(getBlockPos(), tileEntityType, nbtTagCompound);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = new CompoundNBT();
        save(nbt);
        return nbt;
    }

    @Override
    public CompoundNBT getTileData() {
        CompoundNBT nbt = new CompoundNBT();
        save(nbt);
        return nbt;
    }
}
