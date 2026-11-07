package org.faketale.mnemic_odyssey.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.faketale.mnemic_odyssey.MnemicOdyssey;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = MnemicOdyssey.MODID/*, bus = EventBusSubscriber.Bus.MOD*/)
public class ModConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    //忆如泉涌条位置：屏幕比例坐标 (0~1)
    public static final ModConfigSpec.DoubleValue MemoryTorrentPosX = BUILDER.comment("Horizontal position of memory torrent bar (0 = left, 1 = right)").defineInRange("Memory Torrent Bar X", 0.02, 0.0, 1.0);
    public static final ModConfigSpec.DoubleValue MemoryTorrentPosY = BUILDER.comment("Vertical position of memory torrent bar (0 = top, 1 = bottom)").defineInRange("Memory Torrent Bar Y", 0.03, 0.0, 1.0);
    //private static final ModConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER.comment("Whether to log the dirt block on common setup").define("logDirtBlock", true);
    //private static final ModConfigSpec.IntValue MAGIC_NUMBER = BUILDER.comment("A magic number").defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);
    //public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER.comment("What you want the introduction message to be for the magic number").define("magicNumberIntroduction", "The magic number is... ");
    // a list of strings that are treated as resource locations for items
    //private static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER.comment("A list of items to log on common setup.").defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);



    public static final ModConfigSpec SPEC = BUILDER.build();

//    public static boolean logDirtBlock;
//    public static int magicNumber;
//    public static String magicNumberIntroduction;
//    public static Set<Item> items;
//
//    private static boolean validateItemName(final Object obj) {
//        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
//    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
//        logDirtBlock = LOG_DIRT_BLOCK.get();
//        magicNumber = MAGIC_NUMBER.get();
//        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();
//
//        // convert the list of strings into a set of items
//        items = ITEM_STRINGS.get().stream().map(itemName -> BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemName))).collect(Collectors.toSet());
    }
}
