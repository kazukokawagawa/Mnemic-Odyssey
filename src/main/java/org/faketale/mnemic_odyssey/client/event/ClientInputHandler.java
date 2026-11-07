package org.faketale.mnemic_odyssey.client.event;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.client.gui.screen.MemoryCodexScreen;
import org.faketale.mnemic_odyssey.client.gui.screen.ModSettingsScreen;
import org.faketale.mnemic_odyssey.client.key.ModKeyBindings;
import org.faketale.mnemic_odyssey.config.ModConfig;
import org.faketale.mnemic_odyssey.network.packet.MemoryTorrentKeyPayload;

@EventBusSubscriber(modid = MnemicOdyssey.MODID, value = Dist.CLIENT)
public class ClientInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();

        //打开设置菜单（唯一的原版按键绑定）
        if (ModKeyBindings.OPEN_SETTINGS.consumeClick()) {
            if (mc.player != null && mc.screen == null) {
                mc.setScreen(new ModSettingsScreen(null));
            }
        }

        //其余按键来自模组配置，仅在无界面时响应按下
        if (mc.player == null || mc.screen != null || event.getAction() != 1) return;
        int key = event.getKey();

        //打开记忆图鉴
        if (key == ModConfig.OpenCodexKey.get()) {
            mc.setScreen(new MemoryCodexScreen());
        }
        //忆如泉涌
        if (key == ModConfig.MemoryTorrentKey.get()) {
            PacketDistributor.sendToServer(new MemoryTorrentKeyPayload());
        }
    }
}
