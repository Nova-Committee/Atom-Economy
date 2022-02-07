package committee.nova.atom.eco.init;

import committee.nova.atom.eco.Eco;
import committee.nova.atom.eco.common.config.ConfigUtil;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 20:04
 * Version: 1.0
 */
@Mod.EventBusSubscriber(modid = Eco.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {



    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();
        registry.registerAll(

        );
    }


    public static void registerMoneyItems(RegistryEvent.Register<Item> event){
        ConfigUtil.init(event);
    }


}
