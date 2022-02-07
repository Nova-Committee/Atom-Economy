package committee.nova.atom.eco;

import committee.nova.atom.eco.api.Money;
import committee.nova.atom.eco.common.config.ConfigUtil;
import committee.nova.atom.eco.common.net.PacketHandler;
import committee.nova.atom.eco.data.DataManager;
import committee.nova.atom.eco.init.ModBlocks;
import committee.nova.atom.eco.init.ModItems;
import committee.nova.atom.eco.utils.FileUtil;
import committee.nova.atom.eco.utils.PrintUtil;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static committee.nova.atom.eco.Eco.MOD_ID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MOD_ID)
public class Eco {


    public static final String MOD_ID = "atomeco";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static IForgeRegistry<Money> CURRENCY;
    public static DataManager CACHE;
    public static Path MAIN_FOLDER ;
    private static Eco INSTANCE;
    public Eco() {

        INSTANCE = this;
        CURRENCY = new RegistryBuilder<Money>().setName(new ResourceLocation("atomeco:economy")).setType(Money.class).create();
        MinecraftForge.EVENT_BUS.register(new ConfigUtil.EventHandler());


        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, ModItems::registerMoneyItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, ModBlocks::registerBlockItems);
        //FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, ModBlocks::registerBlocks);

        PacketHandler.registerMessage();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static Eco getInstance(){
        return INSTANCE;
    }

    public static List<Money> getSortedMoneyList(){
        return CURRENCY.getValues().stream().sorted(new Comparator<Money>(){
            @Override public int compare(Money o1, Money o2){ return o1.getWorth() < o2.getWorth() ? 1 : -1; }
        }).collect(Collectors.toList());
    }

    public static void loadDataManager(){
        if(isDataManagerLoaded()){
            PrintUtil.debug("SKIPPING LOADING ATOME DATAMANAGER");
            return;
        }
        PrintUtil.debug("LOADING ATOME DATAMANAGER");
        if(Eco.CACHE != null){
            Eco.CACHE.saveAll(); Eco.CACHE.clearAll();
        }
        Eco.CACHE = new DataManager(MAIN_FOLDER.toFile());
    }

    public static void unloadDataManager(){
        PrintUtil.debug("UN-LOADING ATOME DATAMANAGER");
        if(Eco.CACHE != null){
            Eco.CACHE.saveAll();
            Eco.CACHE.clearAll();
            Eco.CACHE = null;
        }
    }

    public static boolean isDataManagerLoaded(){
        return CACHE != null;
    }

    private void setup(final FMLCommonSetupEvent event) {
        MAIN_FOLDER = FMLPaths.GAMEDIR.get().resolve("atom");
        FileUtil.checkFolder(MAIN_FOLDER);

        PermissionAPI.registerNode("atom.economy.admin", DefaultPermissionLevel.OP, "Atom Economy Admin Permission");
    }

    private void doClientStuff(final FMLClientSetupEvent event) {

    }

    private void enqueueIMC(final InterModEnqueueEvent event) {

    }

    private void processIMC(final InterModProcessEvent event) {

    }

    @SubscribeEvent
    public void onServerAboutToStart(FMLServerAboutToStartEvent event){
        Static.SERVER = event.getServer();
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }


}
