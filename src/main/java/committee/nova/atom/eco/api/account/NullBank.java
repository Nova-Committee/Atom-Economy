package committee.nova.atom.eco.api.account;

import com.google.gson.JsonObject;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.text.StringTextComponent;

import java.util.TreeMap;
import java.util.UUID;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 10:47
 * Version: 1.0
 */
public class NullBank extends Bank {
    public static final NullBank INSTANCE = new NullBank("null", "Generic Null Bank", 0, null, null);

    public NullBank(String id, String name, long balance, JsonObject data, TreeMap<String, String> map){
        super(id, name, balance, data, map);
    }

    @Override
    public boolean processAction(Action action, ICommandSource log, Account sender, long amount, Account receiver, boolean fee_included){
        log.sendMessage(new StringTextComponent("BANK NOT FOUND >>> NULL BANK;"), UUID.randomUUID());
        return false;
    }

    @Override
    public boolean isNull(){
        return true;
    }
}
