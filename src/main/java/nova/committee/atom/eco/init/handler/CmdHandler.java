package nova.committee.atom.eco.init.handler;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.atom.eco.common.cmd.ManageCommand;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/11 9:38
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CmdHandler {
    @SubscribeEvent
    public static void registryCmd(RegisterCommandsEvent event) {
        event.getDispatcher().register(ManageCommand.setRegister());
    }
}
