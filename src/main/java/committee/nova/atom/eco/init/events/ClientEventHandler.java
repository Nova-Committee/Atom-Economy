package committee.nova.atom.eco.init.events;

import committee.nova.atom.eco.Eco;
import committee.nova.atom.eco.client.screen.ATMScreen;
import committee.nova.atom.eco.client.screen.WalletScreen;
import committee.nova.atom.eco.init.ModContainers;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Eco.MOD_ID,bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onFMLClientSetupEvent(final FMLClientSetupEvent event) {

        ScreenManager.register(ModContainers.ATM, ATMScreen::new);
        ScreenManager.register(ModContainers.WALLET, WalletScreen::new);


    }
}
