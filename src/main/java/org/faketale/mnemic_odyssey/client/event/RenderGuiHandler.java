package org.faketale.mnemic_odyssey.client.event;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.client.gui.hud.MemoryTorrentOverlay;

@EventBusSubscriber(modid = MnemicOdyssey.MODID, value = Dist.CLIENT)
public class RenderGuiHandler {
    private static final MemoryTorrentOverlay MEMORY_TORRENT_OVERLAY = new MemoryTorrentOverlay();

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        // 排除掉调试界面或者旁观者模式等不需要显示的场景
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) return;

        MEMORY_TORRENT_OVERLAY.render(event.getGuiGraphics());
    }
}
