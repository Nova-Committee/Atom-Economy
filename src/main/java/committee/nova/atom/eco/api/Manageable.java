package committee.nova.atom.eco.api;

import net.minecraft.command.ICommandSource;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 9:23
 * Version: 1.0
 */
public interface Manageable {
    public void modifyBalance(Action action, long amount, ICommandSource source);

    public static enum Action{
        SET, ADD, SUB
    }
}
