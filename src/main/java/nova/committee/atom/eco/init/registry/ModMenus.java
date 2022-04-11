package nova.committee.atom.eco.init.registry;


import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import nova.committee.atom.eco.Static;
import nova.committee.atom.eco.common.menu.ATMMenu;
import nova.committee.atom.eco.common.menu.WalletMenu;
import nova.committee.atom.eco.util.RegistryUtil;

@Mod.EventBusSubscriber(modid = Static.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModMenus {

    public static MenuType<ATMMenu> ATM;
    public static MenuType<WalletMenu> WALLET;

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
        final IForgeRegistry<MenuType<?>> registry = event.getRegistry();

        registry.registerAll(
                ATM = RegistryUtil.registerContainer("atm", ATMMenu::getClientSideInstance),
                WALLET = RegistryUtil.registerContainer("wallet", WalletMenu::getClientSideInstance)
        );
    }


}
