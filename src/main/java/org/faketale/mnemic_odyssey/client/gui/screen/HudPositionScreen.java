package org.faketale.mnemic_odyssey.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.faketale.mnemic_odyssey.client.gui.hud.MemoryTorrentOverlay;
import org.faketale.mnemic_odyssey.config.ModConfig;

/** 模组设置菜单：可拖动忆如泉涌条 */
public class HudPositionScreen extends Screen {
    private boolean dragging = false;
    private double grabX, grabY;

    private final Screen parent;

    public HudPositionScreen(Screen parent) {
        super(Component.translatable("gui.mnemic_odyssey.settings.move_bar"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addRenderableWidget(new ScaleSlider(width / 2 - 105, height - 54));
        addRenderableWidget(Button.builder(Component.translatable("gui.mnemic_odyssey.settings.reset"), b -> {
            ModConfig.MemoryTorrentPosX.set(ModConfig.MemoryTorrentPosX.getDefault());
            ModConfig.MemoryTorrentPosY.set(ModConfig.MemoryTorrentPosY.getDefault());
            ModConfig.SPEC.save();
        }).bounds(width / 2 - 105, height - 30, 100, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.mnemic_odyssey.settings.done"), b -> onClose())
                .bounds(width / 2 + 5, height - 30, 100, 20).build());
    }

    private int barX() { return (int) Math.round(ModConfig.MemoryTorrentPosX.get() * Math.max(0, width - this.getBarWidth())); }
    private int barY() { return (int) Math.round(ModConfig.MemoryTorrentPosY.get() * Math.max(0, height - this.getBarHeight())); }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        MemoryTorrentOverlay.renderPreview(g, barX(), barY());
        int x = barX(), y = barY();
        int color = dragging ? 0xFFFFFF55 : 0xFFFFFFFF;
        g.renderOutline(x - 2, y - 2, this.getBarWidth() + 4, this.getBarHeight() + 4, color);
        super.render(g, mouseX, mouseY, partialTick);
        g.drawCenteredString(font, title, width / 2, 15, 0xFFFFFF);
        g.drawCenteredString(font, Component.translatable("gui.mnemic_odyssey.settings.hint"), width / 2, 30, 0xAAAAAA);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (super.mouseClicked(mx, my, button)) return true;
        int x = barX(), y = barY();
        if (button == 0 && mx >= x - 2 && mx <= x + this.getBarWidth() + 2
                && my >= y - 2 && my <= y + this.getBarHeight() + 2) {
            dragging = true;
            grabX = mx - x;
            grabY = my - y;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (dragging) {
            int rangeX = Math.max(1, width - this.getBarWidth());
            int rangeY = Math.max(1, height - this.getBarHeight());
            ModConfig.MemoryTorrentPosX.set(Math.max(0.0, Math.min(1.0, (mx - grabX) / rangeX)));
            ModConfig.MemoryTorrentPosY.set(Math.max(0.0, Math.min(1.0, (my - grabY) / rangeY)));
            return true;
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (dragging) {
            dragging = false;
            ModConfig.SPEC.save();
            return true;
        }
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    public int getBarWidth() {
        return MemoryTorrentOverlay.scaledSize(Minecraft.getInstance().getGuiSprites().getSprite(MemoryTorrentOverlay.MEMORY_TORRENT).contents().width());
    }

    public int getBarHeight() {
        return MemoryTorrentOverlay.scaledSize(Minecraft.getInstance().getGuiSprites().getSprite(MemoryTorrentOverlay.MEMORY_TORRENT).contents().height());
    }

    private static class ScaleSlider extends AbstractSliderButton {
        private ScaleSlider(int x, int y) {
            super(x, y, 210, 20, Component.empty(), (ModConfig.MemoryTorrentScale.get() - 0.5) / 1.5);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            int percent = (int) Math.round((0.5 + value * 1.5) * 100);
            setMessage(Component.translatable("gui.mnemic_odyssey.settings.scale", percent));
        }

        @Override
        protected void applyValue() {
            ModConfig.MemoryTorrentScale.set(Math.round((0.5 + value * 1.5) * 100.0) / 100.0);
            ModConfig.SPEC.save();
        }
    }
}
