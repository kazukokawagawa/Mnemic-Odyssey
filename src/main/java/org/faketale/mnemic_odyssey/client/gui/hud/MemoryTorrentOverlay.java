package org.faketale.mnemic_odyssey.client.gui.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.capability.ModAttachments;
import org.faketale.mnemic_odyssey.config.ModConfig;
import org.faketale.mnemic_odyssey.network.MemoryTorrentData;
import org.faketale.mnemic_odyssey.util.ModUtils;

public class MemoryTorrentOverlay {
    public static final ResourceLocation MEMORY_TORRENT = ResourceLocation.fromNamespaceAndPath(MnemicOdyssey.MODID, "hud/memory_torrent");
    public static final ResourceLocation MEMORY_TORRENT_FULL = ResourceLocation.fromNamespaceAndPath(MnemicOdyssey.MODID, "hud/memory_torrent_full");
    public static final ResourceLocation MEMORY_TORRENT_BAR = ResourceLocation.fromNamespaceAndPath(MnemicOdyssey.MODID, "hud/memory_torrent_bar");

    public void render(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || !ModConfig.MemoryTorrentShow.get()) return;

        MemoryTorrentData data = player.getData(ModAttachments.MEMORY_TORRENT.get());
        boolean isFull = data.isFull();

        // 是否为满
        ResourceLocation backgroundRl = isFull ? MEMORY_TORRENT_FULL : MEMORY_TORRENT;
        TextureAtlasSprite background = mc.getGuiSprites().getSprite(backgroundRl);
        int width = background.contents().width();
        int height = background.contents().height();
        int x = (int) Math.round(ModConfig.MemoryTorrentPosX.get() * (guiGraphics.guiWidth() - width));
        int y = (int) Math.round(ModConfig.MemoryTorrentPosY.get() * (guiGraphics.guiHeight() - height));

        if (data.active() && !mc.isPaused()) {
            x += ModUtils.random.nextBoolean() ? 1 : -1;
            y += ModUtils.random.nextBoolean() ? 1 : -1;
        }

        guiGraphics.blitSprite(backgroundRl, x, y, width, height);

        if (!isFull) {
            TextureAtlasSprite bar = mc.getGuiSprites().getSprite(MEMORY_TORRENT_BAR);
            int barWidth = bar.contents().width();
            int barHeight = bar.contents().height();
            float progress = Math.clamp(data.energy() / MemoryTorrentData.getMaxEnergy(), 0.0F, 1.0F);
            int progressWidth = Math.round(barWidth * progress);
            if (progressWidth > 0) {
                int barX = x + (width - barWidth) / 2;
                int barY = y + (height - barHeight) / 2;
                guiGraphics.blitSprite(MEMORY_TORRENT_BAR, barWidth, barHeight, 0, 0, barX, barY, progressWidth, barHeight);
            }
        }
    }

    /** 仅绘制空条（设置界面中条被隐藏时用来预览位置） */
    public static void renderPreview(GuiGraphics guiGraphics, int x, int y) {
        TextureAtlasSprite background = Minecraft.getInstance().getGuiSprites().getSprite(MEMORY_TORRENT);
        guiGraphics.blitSprite(MEMORY_TORRENT, x, y, background.contents().width(), background.contents().height());
    }
}
