package nova.committee.atom.eco.util.text;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/9 21:46
 * Version: 1.0
 */
public class LoreUtil {

    private static String getPlateText(String text, ChatFormatting format) {
        return ChatFormatting.GRAY + "[" + format + text + ChatFormatting.GRAY + "]";
    }

    public static void addBlankLine(List<Component> tooltip) {
        tooltip.add(new TextComponent(""));
    }

    public static void addInformationLore(List<Component> tooltip, String lore) {
        addInformationLore(tooltip, lore, false);
    }

    public static void addInformationLore(List<Component> tooltip, String lore, boolean isFirst) {

        if (Screen.hasShiftDown()) {
            tooltip.add(new TextComponent(ChatFormatting.GRAY + "" + ChatFormatting.ITALIC + lore));
        } else if (isFirst)
            tooltip.add(new TextComponent(getPlateText("Shift", ChatFormatting.AQUA) + ChatFormatting.GRAY + " Info"));
    }

    public static void addControlsLore(List<Component> tooltip, String lore, Type type) {
        addControlsLore(tooltip, lore, type, false);
    }

    public static void addControlsLore(List<Component> tooltip, String lore, Type type, boolean isFirst) {

        if (Screen.hasControlDown()) {
            addActionLore(tooltip, lore, type);
        } else if (isFirst)
            tooltip.add(new TextComponent(getPlateText("Ctrl", ChatFormatting.AQUA) + ChatFormatting.GRAY + " Controls"));
    }

    private static void addActionLore(List<Component> tooltip, String lore, Type type) {
        tooltip.add(new TextComponent(getPlateText(type.getName(), ChatFormatting.YELLOW) + ChatFormatting.GRAY + " " + lore));
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
