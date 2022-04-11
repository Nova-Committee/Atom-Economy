package nova.committee.atom.eco;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.registries.IForgeRegistry;
import nova.committee.atom.eco.api.core.money.IMoney;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/9 22:18
 * Version: 1.0
 */
public class Static {
    public static final String MOD_ID = "atomeco";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final ResourceLocation MONEY_NAME = new ResourceLocation(MOD_ID, "money");
    public static final ResourceLocation GUI_TOOLTIP = new ResourceLocation(MOD_ID, "textures/gui/tooltip.png");
    public static final ResourceLocation GUI_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/container/settings.png");
    public static Supplier<IForgeRegistry<IMoney>> CURRENCY;
    public static File ATOM_FOLDER;
    public static File ECO_FOLDER;
    public static File DATA_FOLDER;
    public static MinecraftServer SERVER;
    public static boolean dev = false;

    public static boolean setDevMode(boolean bool) {
        return dev = bool;
    }

    public static boolean dev() {
        return dev;
    }


}
