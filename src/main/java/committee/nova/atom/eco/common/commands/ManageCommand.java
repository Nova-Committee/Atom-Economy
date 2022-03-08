package committee.nova.atom.eco.common.commands;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import committee.nova.atom.eco.Eco;
import committee.nova.atom.eco.api.account.Account;
import committee.nova.atom.eco.common.config.ConfigUtil;
import committee.nova.atom.eco.core.AccountDataManager;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.UUID;

import static net.minecraft.command.Commands.literal;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/21 11:26
 * Version: 1.0
 */
public class ManageCommand {
    public static ArgumentBuilder<CommandSource, ?> setRegister() {
        return literal("set")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ManageCommand::setExecute)
                                .then(Commands.argument("amount", LongArgumentType.longArg())
                                        .executes(ManageCommand::setExecute)))
                ;
    }
    public static int setExecute(CommandContext<CommandSource> context) throws CommandException {
        try{
            CommandSource sender = context.getSource();
            ServerPlayerEntity args1 = EntityArgument.getPlayer(context, "player");
            UUID playerId = args1.getUUID();
            long args2 = context.getArgument("amount", Long.class);
            Account account = AccountDataManager.getAccount("player", playerId.toString(), false);
            if(account == null){
                sender.sendSuccess(new StringTextComponent("账户未找到"), false);
            }
            account.setBalance(args2 * 1000);
            sender.sendSuccess(new StringTextComponent("新的余额: " + ConfigUtil.getWorthAsString(account.getBalance())), false);
        }
        catch (Exception e){
            Eco.LOGGER.info(e.getMessage());
        }

        return 0;
    }



}
