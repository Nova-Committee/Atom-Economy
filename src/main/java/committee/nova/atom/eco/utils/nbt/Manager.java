package committee.nova.atom.eco.utils.nbt;

import net.minecraft.nbt.CompoundNBT;

public abstract class Manager {

    protected boolean tracked = true;

    public abstract void load(String name, CompoundNBT nbt);

    public abstract void save(String name, CompoundNBT nbt);

    public <T> T noGuiTracking(){
        tracked = false;
        return (T) this;
    }

}
