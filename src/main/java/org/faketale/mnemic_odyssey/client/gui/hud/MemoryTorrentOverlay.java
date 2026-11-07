package org.faketale.mnemic_odyssey.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.capability.ModAttachments;
import org.faketale.mnemic_odyssey.config.ModConfig;
import org.faketale.mnemic_odyssey.network.MemoryTorrentData;
import org.faketale.mnemic_odyssey.util.ModUtils;

public class MemoryTorrentOverlay {
    private static final ResourceLocation BAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(MnemicOdyssey.MODID, "textures/gui/memory_torrent.png");
    private static final ResourceLocation BAR_TEXTURE_2 = ResourceLocation.fromNamespaceAndPath(MnemicOdyssey.MODID, "textures/gui/memory_torrent_2.png");
    public static final int barWidth = 100;
    public static final int barHeight = 10;
    //private static final boolean shaking = true;//开大了是会摇晃的

    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player == null) return;

        MemoryTorrentData data = player.getData(ModAttachments.MEMORY_TORRENT.get());
        float energy = data.energy();
        boolean isActive = data.active();
        boolean isFull = energy >= MemoryTorrentData.getMaxEnergy();

        // 计算进度宽度 (假设 barWidth 是你的背景总宽)
        int progressWidth = (int) (barWidth * (energy / MemoryTorrentData.getMaxEnergy()));

        //设置渲染状态
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        //初始化颜色为白色
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        //计算位置及偏移
        int screenW = guiGraphics.guiWidth();
        int screenH = guiGraphics.guiHeight();
        int x = (int) Math.round(ModConfig.MemoryTorrentPosX.get() * (screenW - barWidth));
        int y = (int) Math.round(ModConfig.MemoryTorrentPosY.get() * (screenH - barHeight));

        //如果开启技能，随机摇晃
        if(player.getData(ModAttachments.MEMORY_TORRENT).active() && !mc.isPaused()){
            x += ModUtils.random.nextBoolean() ? 1 : -1;
            y += ModUtils.random.nextBoolean() ? 1 : -1;
        }

        //绘制背景层 (参数：贴图，x, y, u, v, width, height, textureWidth, textureHeight)
        //背景在贴图的 (0,0)，宽 100，高 10
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);//确保颜色完全不透明
        guiGraphics.blit(BAR_TEXTURE, x, y, 0, 0, barWidth, barHeight, 256, 256);

        // 5. 绘制填充层
        if (progressWidth > 0) {
            float alpha = 1.0F;

            if (isActive) {
                // 技能开启期间：可以设置一个特定的颜色（例如青色高亮）
                //guiGraphics.setColor(0.4F, 1.0F, 1.0F, 1.0F);
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
            } else if (isFull) {
                // 能量满但未开启：呼吸灯闪烁效果
                // 使用正弦波控制 alpha 在 0.4 到 1.0 之间波动
                alpha = 0.7f + (float) Math.sin(player.level().getGameTime() * 0.5f) * 0.3f;
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
            } else {
                // 普通充能期间：正常白色渲染
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }

            // 绘制填充图层
            guiGraphics.blit(BAR_TEXTURE_2, x, y, 0, 0, progressWidth, barHeight, 256, 256);

            // 渲染完重置颜色，避免影响其他 GUI 渲染
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
