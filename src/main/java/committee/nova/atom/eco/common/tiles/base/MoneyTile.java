package committee.nova.atom.eco.common.tiles.base;

import committee.nova.atom.eco.api.security.ISecurity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/8 15:41
 * Version: 1.0
 */
public abstract class MoneyTile extends BaseTile {
    public boolean enable;

    public MoneyTile(final TileEntityType<?> tileEntityType) {
        super(tileEntityType);

        enable = true;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {

        if (this instanceof ISecurity) {

            ISecurity security = (ISecurity) this;
            security.getSecurityProfile().readFromNBT(nbt);
        }


        enable = nbt.getBoolean("enable");
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {

        if (this instanceof ISecurity) {

            ISecurity security = (ISecurity) this;
            security.getSecurityProfile().writeToNBT(nbt);
        }


        nbt.putBoolean("enable", enable);
        return super.save(nbt);
    }
}
