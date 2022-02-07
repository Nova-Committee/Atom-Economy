package committee.nova.atom.eco.utils.nbt;

import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;
import java.util.Map;

public class NBTManager {
    private Map<String, Manager> managerMap = new HashMap<>();

    public void load(CompoundNBT nbt){
        for (Map.Entry<String, Manager> entry : managerMap.entrySet()){
            if(nbt.contains(entry.getKey())){
                entry.getValue().load(entry.getKey(), nbt);
            }
        }
    }

    public CompoundNBT save(CompoundNBT nbt){
        for (Map.Entry<String, Manager> entry : managerMap.entrySet()){
            entry.getValue().save(entry.getKey(), nbt);

        }
        return nbt;
    }


}
