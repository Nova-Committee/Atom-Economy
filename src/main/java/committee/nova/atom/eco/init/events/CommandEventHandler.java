package committee.nova.atom.eco.init.events;

import com.mojang.brigadier.CommandDispatcher;
import committee.nova.atom.eco.common.commands.ManageCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/21 11:10
 * Version: 1.0
 */
@Mod.EventBusSubscriber
public class CommandEventHandler {
    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("eco")
                        .requires(commandSource -> commandSource.hasPermission(2))
                        .then(ManageCommand.setRegister())

        );
    }
}
