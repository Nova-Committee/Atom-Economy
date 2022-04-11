package nova.committee.atom.eco.init.handler;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import nova.committee.atom.eco.Static;
import nova.committee.atom.eco.util.FileUtil;

import java.io.File;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/9 22:19
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class FileHandler {


    @SubscribeEvent
    public static void fileInit(FMLCommonSetupEvent event) {
        Static.ATOM_FOLDER = FMLPaths.GAMEDIR.get().resolve("atom").toFile();
        FileUtil.Check(Static.ATOM_FOLDER);
        Static.ECO_FOLDER = new File(Static.ATOM_FOLDER.getAbsolutePath() + "/economy");
        FileUtil.Check(Static.ECO_FOLDER);
    }

}
