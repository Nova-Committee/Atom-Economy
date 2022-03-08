package committee.nova.atom.eco.utils.text;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/9 21:46
 * Version: 1.0
 */
public class LoreUtil {

    private static String getPlateText(String text, TextFormatting format) {
        return TextFormatting.GRAY + "[" + format + text + TextFormatting.GRAY + "]";
    }

    public static void addBlankLine(List<ITextComponent> tooltip) {
        tooltip.add(new StringTextComponent(""));
    }

    public static void addInformationLore(List<ITextComponent> tooltip, String lore) {
        addInformationLore(tooltip, lore, false);
    }

    public static void addInformationLore(List<ITextComponent> tooltip, String lore, boolean isFirst) {

        if (Screen.hasShiftDown()) {
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + "" + TextFormatting.ITALIC + lore));
        } else if (isFirst)
            tooltip.add(new StringTextComponent(getPlateText("Shift", TextFormatting.AQUA) + TextFormatting.GRAY + " Info"));
    }

    public static void addControlsLore(List<ITextComponent> tooltip, String lore, Type type) {
        addControlsLore(tooltip, lore, type, false);
    }

    public static void addControlsLore(List<ITextComponent> tooltip, String lore, Type type, boolean isFirst) {

        if (Screen.hasControlDown()) {
            addActionLore(tooltip, lore, type);
        } else if (isFirst)
            tooltip.add(new StringTextComponent(getPlateText("Ctrl", TextFormatting.AQUA) + TextFormatting.GRAY + " Controls"));
    }

    private static void addActionLore(List<ITextComponent> tooltip, String lore, Type type) {
        tooltip.add(new StringTextComponent(getPlateText(type.getName(), TextFormatting.YELLOW) + TextFormatting.GRAY + " " + lore));
    }

    public enum Type {

        USE("Use"),
        USE_OPEN_HAND("Use-Open-Hand"),
        USE_WRENCH("Use-Wrench"),
        USE_WALLET("Use-Wallet"),
        USE_BOOK("Use-Link-Book"),
        SNEAK_USE("Sneak-Use"),
        SNEAK_USE_BLUEPRINT("Sneak-Use-Blueprint"),
        SNEAK_USE_BOOK("Sneak-Use-Link-Book"),
        SNEAK_BREAK_BLOCK("Sneak-Break-Block"),
        RELEASE_USE("Release Use"),
        LEFT_CLICK_BLOCK("Left-Click-Block"),
        SNEAK_LEFT_CLICK_BLOCK("Sneak-Left-Click-Block"),
        LEFT_CLICK_BLUEPRINT("Left-Click-Blueprint");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }
    }
}
