package nova.committee.atom.eco.init.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import nova.committee.atom.eco.Static;
import nova.committee.atom.eco.common.block.ATMBlock;
import nova.committee.atom.eco.util.RegistryUtil;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/24 9:47
 * Version: 1.0
 */
@Mod.EventBusSubscriber(modid = Static.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks {

    public static Block ATM;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();
        registry.registerAll(

                ATM = new ATMBlock()
        );
    }

    @SubscribeEvent
    public static void registerBlockItems(RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();
        registry.registerAll(
                RegistryUtil.blockItem(ATM)
        );
    }


}
