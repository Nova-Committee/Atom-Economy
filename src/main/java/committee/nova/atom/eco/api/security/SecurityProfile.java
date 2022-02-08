package committee.nova.atom.eco.api.security;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/8 15:22
 * Version: 1.0
 */
public class SecurityProfile {
    private String ownerUUID = "";

    public String getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwner(PlayerEntity player) {
        ownerUUID = player.getName().getString();
    }

    public boolean hasOwner() {
        return ownerUUID.isEmpty();
    }

    public boolean isOwner(String ownerName) {
        return this.ownerUUID.equalsIgnoreCase(ownerName);
    }

    public void readFromNBT(CompoundNBT nbt) {

        if (!nbt.getString("ownerUUID").isEmpty()) {
            ownerUUID = nbt.getString("ownerUUID");
        }
    }

    public void writeToNBT(CompoundNBT nbt) {

        if (!ownerUUID.isEmpty()) {
            nbt.putString("ownerUUID", ownerUUID);
        }
    }
}
