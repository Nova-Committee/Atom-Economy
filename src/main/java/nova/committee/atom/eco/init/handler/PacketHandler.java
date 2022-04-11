package nova.committee.atom.eco.init.handler;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import nova.committee.atom.eco.Static;
import nova.committee.atom.eco.common.net.ATMOthersActionPacket;
import nova.committee.atom.eco.common.net.ATMSelfActionPacket;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 20:41
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PacketHandler {
    public static final String VERSION = "1.0";
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(Static.MOD_ID, "network"),
                () -> VERSION,
                (version) -> version.equals(VERSION),
                (version) -> version.equals(VERSION)
        );
        INSTANCE.registerMessage(nextID(), ATMOthersActionPacket.class, ATMOthersActionPacket::toBytes, ATMOthersActionPacket::new, ATMOthersActionPacket::handle);
        INSTANCE.registerMessage(nextID(), ATMSelfActionPacket.class, ATMSelfActionPacket::toBytes, ATMSelfActionPacket::new, ATMSelfActionPacket::handle);

    }


}
