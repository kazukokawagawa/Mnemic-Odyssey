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
import org.faketale.mnemic_odyssey.network.packet.MemoryTorrentKeyPayload;

@EventBusSubscriber(modid = MnemicOdyssey.MODID, /*bus = EventBusSubscriber.Bus.GAME,*/ value = Dist.CLIENT)
public class ClientInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        //打开记忆图鉴
        if (ModKeyBindings.OPEN_CODEX_GUI.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.screen == null) {
                mc.setScreen(new MemoryCodexScreen());
            }
        }

        //打开设置菜单
        if (ModKeyBindings.OPEN_SETTINGS.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.screen == null) {
                mc.setScreen(new ModSettingsScreen());
            }
        }

        //忆如泉涌
        if (ModKeyBindings.MEMORY_TORRENT.consumeClick()) {
            PacketDistributor.sendToServer(new MemoryTorrentKeyPayload());
        }
    }
}
