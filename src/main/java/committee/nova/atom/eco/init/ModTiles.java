package committee.nova.atom.eco.init;

import committee.nova.atom.eco.Eco;
import committee.nova.atom.eco.common.tiles.ATMTile;
import committee.nova.atom.eco.utils.RegistryUtil;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Eco.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModTiles {

    public static TileEntityType<ATMTile> ATM;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<TileEntityType<?>> event) {
        final IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
        registry.registerAll(
            ATM = RegistryUtil.build(ATMTile::new, "atm", ModBlocks.ATM)
        );
    }



}
