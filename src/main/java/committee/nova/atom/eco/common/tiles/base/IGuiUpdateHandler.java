package committee.nova.atom.eco.common.tiles.base;

import net.minecraft.nbt.CompoundNBT;

public interface IGuiUpdateHandler {

    CompoundNBT getGuiUpdateTag();

    void handleGuiUpdateTag(CompoundNBT tag);
}
