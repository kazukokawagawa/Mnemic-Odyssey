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

    //按键（GLFW 键码），在模组设置菜单中修改
    public static final ModConfigSpec.IntValue OpenCompendiumKey = BUILDER.comment("Key code (GLFW) to open memory compendium").defineInRange("Open Compendium Key", org.lwjgl.glfw.GLFW.GLFW_KEY_B, -1, 512);
    public static final ModConfigSpec.IntValue MemoryTorrentKey = BUILDER.comment("Key code (GLFW) to trigger memory torrent").defineInRange("Memory Torrent Key", org.lwjgl.glfw.GLFW.GLFW_KEY_V, -1, 512);
    public static final ModConfigSpec.BooleanValue MemoryTorrentShow = BUILDER.comment("Whether to show the memory torrent bar").define("Memory Torrent Bar Show", true);
    //忆如泉涌条位置：屏幕比例坐标 (0~1)
    public static final ModConfigSpec.DoubleValue MemoryTorrentPosX =
            BUILDER.comment("Horizontal position of memory torrent bar (0 = left, 1 = right)")
                    .defineInRange("Memory Torrent Bar X", 0.02, 0.0, 1.0);
    public static final ModConfigSpec.DoubleValue MemoryTorrentPosY =
            BUILDER.comment("Vertical position of memory torrent bar (0 = top, 1 = bottom)")
                    .defineInRange("Memory Torrent Bar Y", 0.03, 0.0, 1.0);
    public static final ModConfigSpec.DoubleValue MemoryTorrentScale =
            BUILDER.comment("Memory torrent bar scale (1.0 = original size)")
                    .defineInRange("Memory Torrent Bar Scale", 1.0, 0.5, 2.0);
    public static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    }
}
