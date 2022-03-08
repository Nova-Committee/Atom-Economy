package committee.nova.atom.eco.common.config;

import committee.nova.atom.eco.Eco;
import committee.nova.atom.eco.Static;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/20 15:04
 * Version: 1.0
 */

@Mod.EventBusSubscriber(modid = Eco.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {

    public static final Common COMMON;
    public static final ForgeConfigSpec CONFIG_SPEC;
    public static String COMMA = ",", DOT = ".";
    public static SyncableConfig LOCAL = new SyncableConfig();

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        CONFIG_SPEC = specPair.getRight();
    }

    public static String getComma(){
        return COMMA;
    }

    public static String getDot(){
        return DOT;
    }

    public static class Common {

        public final ForgeConfigSpec.ConfigValue<String> defaultBank, currencySign;
        public final ForgeConfigSpec.ConfigValue<Integer> startingBalance, unloadFrequency, minSearchChars;
        public final ForgeConfigSpec.ConfigValue<Boolean> notifyBalanceOnJoin, showCentesimals, showDecimals, enableBankCards,
                showItemWorthInTooltip, partialAccountNameSearch;
        public final ForgeConfigSpec.ConfigValue<Integer> walletCurrencyCapacity;


        public Common(ForgeConfigSpec.Builder builder) {

            startingBalance = builder.comment("初始的金额").defineInRange("", 10000, 0, Integer.MAX_VALUE);
            unloadFrequency = builder.comment("保存间隔").defineInRange("", 600000, Static.dev() ? 30000 : 60000, 86400000 / 2);
            minSearchChars = builder.comment("最少搜索时输入的字符").defineInRange("", 3, 1, 1000);

            walletCurrencyCapacity = builder.comment("钱包的容量")
                    .defineInRange("walletCurrencyCapacity", 1000000, 0, 99999999);


            defaultBank = builder.comment("默认的银行uuid").define("default_bank", "00000000");
            currencySign = builder.comment("可以自定义货币符号").define("currency_sign", "￥");

            notifyBalanceOnJoin = builder.comment("加入游戏时提醒余额").define("notify_balance_on_join", true);
            showCentesimals = builder.comment("是否格式化显示? 实例:'29,503' / '29.503'").define("show_centesimals", false);
            showDecimals = builder.comment("是否显示小数点后几位? 实例: '234.00'").define("show_decimals", false);
            enableBankCards = builder.define("", true);
            showItemWorthInTooltip = builder.comment("是否在提示栏显示物品的价值").define("show_item_worth", true);
            partialAccountNameSearch = builder.comment("true,可以通过仅输入部分名称来搜索帐户/false,则需要完整的名字").define("partial_account_name_search", true);

        }

    }

    public static class SyncableConfig {

        public int starting_balance, unload_frequency, min_search_chars;
        public String default_bank, currency_sign, thousand_separator;
        public boolean notify_balance_on_join, invert_comma, show_centesimals, show_decimals, enable_bank_cards;
        public boolean show_item_worth_in_tooltip = true, partial_account_name_search = true;

        public static SyncableConfig fromNBT(CompoundNBT compound){
            SyncableConfig config = new SyncableConfig();
            config.starting_balance = compound.getInt("starting_balance");
            config.unload_frequency = compound.getInt("unload_frequency");
            config.default_bank = compound.getString("default_bank");
            config.currency_sign = compound.getString("currency_sign");
            config.notify_balance_on_join = compound.getBoolean("notify_balance_on_join");
            config.show_centesimals = compound.getBoolean("show_centesimals");
            config.enable_bank_cards = compound.getBoolean("enable_bank_cards");
            config.show_item_worth_in_tooltip = compound.getBoolean("show_item_worth_in_tooltip");
            config.partial_account_name_search = compound.getBoolean("partial_account_name_search");
            config.show_decimals = compound.getBoolean("show_decimals");
            config.min_search_chars = compound.getInt("min_search_chars");
            return config;
        }

        public CompoundNBT toNBT() {
            CompoundNBT compound = new CompoundNBT();
            compound.putInt("starting_balance", COMMON.startingBalance.get());
            compound.putInt("unload_frequency", COMMON.unloadFrequency.get());
            compound.putString("default_bank", COMMON.defaultBank.get());
            compound.putString("currency_sign", COMMON.currencySign.get());
            compound.putBoolean("notify_balance_on_join", COMMON.notifyBalanceOnJoin.get());
            compound.putBoolean("show_centesimals", COMMON.showCentesimals.get());
            compound.putBoolean("enable_bank_cards", COMMON.enableBankCards.get());
            compound.putBoolean("show_item_worth_in_tooltip", COMMON.showItemWorthInTooltip.get());
            compound.putBoolean("partial_account_name_search", COMMON.partialAccountNameSearch.get());
            compound.putBoolean("show_decimals", COMMON.showDecimals.get());
            compound.putInt("min_search_chars", COMMON.minSearchChars.get());
            return compound;
        }
    }



}
