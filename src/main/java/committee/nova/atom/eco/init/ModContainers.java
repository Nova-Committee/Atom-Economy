package committee.nova.atom.eco.init;

import committee.nova.atom.eco.Eco;
import committee.nova.atom.eco.common.containers.ATMContainer;
import committee.nova.atom.eco.common.containers.WalletContainer;
import committee.nova.atom.eco.utils.RegistryUtil;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Eco.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContainers {

    public static ContainerType<ATMContainer> ATM;
    public static ContainerType<WalletContainer> WALLET;

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        final IForgeRegistry<ContainerType<?>> registry = event.getRegistry();

        registry.registerAll(
                ATM = RegistryUtil.registerContainer("atm", ATMContainer::new),
                WALLET = RegistryUtil.registerContainer("wallet", WalletContainer::createClientWallet)
        );
    }


}
