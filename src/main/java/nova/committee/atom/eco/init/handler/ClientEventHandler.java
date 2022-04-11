package nova.committee.atom.eco.init.handler;


import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import nova.committee.atom.eco.Static;
import nova.committee.atom.eco.client.screen.WalletScreen;
import nova.committee.atom.eco.client.screen.atm.ATMScreen;
import nova.committee.atom.eco.init.registry.ModMenus;

@Mod.EventBusSubscriber(modid = Static.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onFMLClientSetupEvent(final FMLClientSetupEvent event) {

        MenuScreens.register(ModMenus.ATM, ATMScreen::new);
        MenuScreens.register(ModMenus.WALLET, WalletScreen::new);


    }
}
