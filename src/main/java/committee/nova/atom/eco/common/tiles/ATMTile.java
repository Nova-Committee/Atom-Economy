package committee.nova.atom.eco.common.tiles;

import committee.nova.atom.eco.common.containers.ATMContainer;
import committee.nova.atom.eco.data.DataManager;
import committee.nova.atom.eco.init.ModTiles;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ATMTile extends TileEntity implements INamedContainerProvider {




    public ATMTile() {
        super(ModTiles.ATM);
    }




    private INBT getBankList(){
        ListNBT list = new ListNBT();
        DataManager.getBankNameCache().forEach((key, val) -> {
            list.add(StringNBT.valueOf(key + ":" + val));
        });
        return list;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("ATM");
    }

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory inventory, @Nonnull PlayerEntity entity) {
        return new ATMContainer(id, inventory, this);
    }
}
