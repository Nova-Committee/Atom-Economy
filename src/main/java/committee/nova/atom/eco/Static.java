package committee.nova.atom.eco;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 10:11
 * Version: 1.0
 */
public class Static {
    public static final String MOD_ID = "atomlib";

    public static final ResourceLocation GUI_TEXTURES = new ResourceLocation(MOD_ID + ":textures/gui/gui_textures.png");
    public static boolean mclib = false;
    public static boolean devmode = false;
    public static boolean server = false;
    public static MinecraftServer SERVER = ServerLifecycleHooks.getCurrentServer();

    public static MinecraftServer getSERVER(){return SERVER;}

    public static final boolean setAsMcLib(boolean bool){
        return mclib = bool;
    }

    public static final boolean setDevmode(boolean bool){
        return devmode = bool;
    }

    public static final boolean setIsServer(boolean bool){
        return server = bool;
    }

    public static final boolean dev(){
        return devmode;
    }


    public static final void halt(){
        halt(1);
    }

    public static void halt(int errid){
        if(mclib){
            Static.halt(errid);
        }
        System.exit(errid);
    }

    public static final void stop(){
        if(devmode) halt(1);
        return;
    }
}
