package committee.nova.atom.eco.utils.text;

import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/9 21:45
 * Version: 1.0
 */
public class MsgUtil {
    private final String unitName;
    private final Entity[] players;

    public MsgUtil(String unitName, Entity... players) {
        this.unitName = unitName;
        this.players = players;
    }

    public void printMessage(TextFormatting format, String message) {

        for (Entity player : players) {

            StringTextComponent componentString = new StringTextComponent(getUnitName() + (format + message));
            player.sendMessage(componentString, Util.NIL_UUID);
        }
    }

    private String getUnitName() {
        return "[" + unitName + "] ";
    }
}
