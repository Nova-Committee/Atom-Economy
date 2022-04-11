package nova.committee.atom.eco.common.cmd;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import nova.committee.atom.eco.Static;
import nova.committee.atom.eco.core.AccountDataManager;
import nova.committee.atom.eco.core.model.Account;

import java.util.UUID;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/21 11:26
 * Version: 1.0
 */
public class ManageCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> setRegister() {
        return Commands.literal("set")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ManageCommand::setExecute)
                        .then(Commands.argument("amount", LongArgumentType.longArg())
                                .executes(ManageCommand::setExecute)))
                ;
    }

    public static int setExecute(CommandContext<CommandSourceStack> context) {
        try {
            CommandSourceStack sender = context.getSource();
            ServerPlayer args1 = EntityArgument.getPlayer(context, "player");
            UUID playerId = args1.getUUID();
            long args2 = context.getArgument("amount", Long.class);
            Account account = AccountDataManager.getAccount(playerId.toString(), false);
            if (account == null) {
                sender.sendSuccess(new TextComponent("账户未找到"), false);
            }
            account.setBalance(args2 * 1000);
            sender.sendSuccess(new TextComponent("新的余额: " + account.getBalance()), false);
        } catch (Exception e) {
            Static.LOGGER.info(e.getMessage());
        }

        return 0;
    }


}
