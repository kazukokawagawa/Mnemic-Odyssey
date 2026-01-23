package org.faketale.mnemic_odyssey;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.faketale.mnemic_odyssey.block.ModBlocks;
import org.faketale.mnemic_odyssey.capability.ModAttachments;
import org.faketale.mnemic_odyssey.config.ModConfig;
import org.faketale.mnemic_odyssey.effect.ModEffects;
import org.faketale.mnemic_odyssey.gamerule.ModGameRules;
import org.faketale.mnemic_odyssey.item.ModItems;
import org.faketale.mnemic_odyssey.item.MnemicOdysseyTabs;
import org.faketale.mnemic_odyssey.util.ModUtils;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MnemicOdyssey.MODID)
public class MnemicOdyssey {

    public static final String MODID = "mnemic_odyssey";

    public MnemicOdyssey(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::commonSetup);//添加监听器

        //注册方块
        ModBlocks.BLOCKS.register(modEventBus);
        //注册物品
        ModItems.ITEMS.register(modEventBus);
        //注册创造物品栏
        MnemicOdysseyTabs.CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(MnemicOdysseyTabs::addCreative);
        //注册自定义效果
        ModEffects.EFFECTS.register(modEventBus);
        //注册附加类型
        ModAttachments.ATTACHMENTS.register(modEventBus);
        //注册游戏规则
        ModGameRules.register();
        //注册配置项
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, ModConfig.SPEC);

        // 注册我们自己以响应服务器和其他我们感兴趣的游戏事件。
        // 注意，只有当我们希望*这个*类（MnemicOdyssey）直接响应事件时，这一步才是必要的。
        // 如果这个类中没有带有 @SubscribeEvent 注解的函数（例如下面的 onServerStarting()），请不要添加这一行。
        NeoForge.EVENT_BUS.register(this);

        ModUtils.LOGGER.info("Welcome to Mnemic Odyssey!");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
//        LOGGER.info("HELLO FROM COMMON SETUP");
//        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
//        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);
//        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
//        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, /*bus = EventBusSubscriber.Bus.MOD, */value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
//            LOGGER.info("HELLO FROM CLIENT SETUP");
//            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

//    @SubscribeEvent
//    public static void onRegisterReloadListeners(AddReloadListenerEvent event) {
//        // 建议将 manager 设为单例以便后续访问
//        event.addListener(new MemoryCodexManager());
//    }
}
