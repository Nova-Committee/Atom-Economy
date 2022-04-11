package nova.committee.atom.eco.init.registry;

import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import nova.committee.atom.eco.Static;
import nova.committee.atom.eco.common.item.WalletItem;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 20:04
 * Version: 1.0
 */
@Mod.EventBusSubscriber(modid = Static.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {


    public static Item Wallet;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();
        registry.registerAll(
                Wallet = new WalletItem()
        );
    }


}
