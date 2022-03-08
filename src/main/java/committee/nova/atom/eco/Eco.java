package committee.nova.atom.eco;

import committee.nova.atom.eco.api.money.Money;
import committee.nova.atom.eco.common.config.ConfigUtil;
import committee.nova.atom.eco.common.net.PacketHandler;
import committee.nova.atom.eco.core.AccountDataManager;
import committee.nova.atom.eco.core.MarketDataManager;
import committee.nova.atom.eco.init.ModBlocks;
import committee.nova.atom.eco.init.ModItems;
import committee.nova.atom.eco.utils.FileUtil;
import committee.nova.atom.eco.utils.text.LogUtil;
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
    public static AccountDataManager ACCOUNT_CACHE;
    public static MarketDataManager MARKET_CACHE;
    public static Path MAIN_DIR, ECO_DIR;
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

    public static void loadDataManager() {
        if (isDataManagerLoaded()) {
            LogUtil.debug("SKIPPING LOADING ATOMECO DATAMANAGER");
            return;
        }
        LogUtil.debug("LOADING ATOMECO DATAMANAGER");
        if (Eco.ACCOUNT_CACHE != null) {
            Eco.ACCOUNT_CACHE.saveAll();
            Eco.ACCOUNT_CACHE.clearAll();
        }
        if (Eco.MARKET_CACHE != null) {
            Eco.MARKET_CACHE.saveAll();
            Eco.MARKET_CACHE.clearAll();
        }
        Eco.ACCOUNT_CACHE = new AccountDataManager(ECO_DIR.toFile());
        Eco.MARKET_CACHE = new MarketDataManager(ECO_DIR.toFile());

    }

    public static void unloadDataManager() {
        LogUtil.debug("UN-LOADING ATOMECO DATAMANAGER");
        if (Eco.ACCOUNT_CACHE != null) {
            Eco.ACCOUNT_CACHE.saveAll();
            Eco.ACCOUNT_CACHE.clearAll();
            Eco.ACCOUNT_CACHE = null;
        }
        if (Eco.MARKET_CACHE != null) {
            Eco.MARKET_CACHE.saveAll();
            Eco.MARKET_CACHE.clearAll();
            Eco.MARKET_CACHE = null;
        }
    }

    public static boolean isDataManagerLoaded(){
        return (ACCOUNT_CACHE != null) && (MARKET_CACHE != null);
    }

    private void setup(final FMLCommonSetupEvent event) {
        MAIN_DIR = FMLPaths.GAMEDIR.get().resolve("atom");
        FileUtil.checkFolder(MAIN_DIR);
        ECO_DIR = MAIN_DIR.resolve("economy");
        FileUtil.checkFolder(ECO_DIR);
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

    }


}
