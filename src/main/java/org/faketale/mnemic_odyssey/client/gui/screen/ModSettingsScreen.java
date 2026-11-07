package org.faketale.mnemic_odyssey.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.faketale.mnemic_odyssey.config.ModConfig;
import org.lwjgl.glfw.GLFW;

/** 模组设置主菜单：按键设置 + 进入二级菜单调整忆如泉涌条位置 */
public class ModSettingsScreen extends Screen {
    private final Screen parent;
    private ModConfigSpec.IntValue listening = null; // 正在等待按键的设置项
    private Button compendiumButton, torrentButton;
    private Button showButton;

    public ModSettingsScreen(Screen parent) {
        super(Component.translatable("gui.mnemic_odyssey.settings.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = width / 2, y = height / 4;
        int left = cx - 155, right = cx + 5, w = 150;
        // 左栏：按键
        compendiumButton = addRenderableWidget(Button.builder(Component.empty(), b -> listening = ModConfig.OpenCompendiumKey)
                .bounds(left, y, w, 20).build());
        torrentButton = addRenderableWidget(Button.builder(Component.empty(), b -> listening = ModConfig.MemoryTorrentKey)
                .bounds(left, y + 24, w, 20).build());
        // 右栏：忆如泉涌条
        showButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            ModConfig.MemoryTorrentShow.set(!ModConfig.MemoryTorrentShow.get());
            ModConfig.SPEC.save();
            refreshLabels();
        }).bounds(right, y, w, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.mnemic_odyssey.settings.move_bar"),
                b -> minecraft.setScreen(new HudPositionScreen(this))).bounds(right, y + 24, w, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.mnemic_odyssey.settings.done"), b -> onClose())
                .bounds(cx - 100, height - 30, 200, 20).build());
        refreshLabels();
    }

    private Component keyLabel(String nameKey, ModConfigSpec.IntValue value) {
        Component key = listening == value
                ? Component.literal("> ? <")
                : InputConstants.Type.KEYSYM.getOrCreate(value.get()).getDisplayName();
        return Component.translatable(nameKey).append(": ").append(key);
    }

    private void refreshLabels() {
        compendiumButton.setMessage(keyLabel("gui.mnemic_odyssey.settings.key_compendium", ModConfig.OpenCompendiumKey));
        torrentButton.setMessage(keyLabel("gui.mnemic_odyssey.settings.key_torrent", ModConfig.MemoryTorrentKey));
        showButton.setMessage(Component.translatable("gui.mnemic_odyssey.settings.show_bar").append(": ")
                .append(Component.translatable(ModConfig.MemoryTorrentShow.get() ? "options.on" : "options.off")));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (listening != null) {
            if (keyCode != GLFW.GLFW_KEY_ESCAPE) {
                listening.set(keyCode);
                ModConfig.SPEC.save();
            }
            listening = null;
            refreshLabels();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        g.drawCenteredString(font, title, width / 2, 15, 0xFFFFFF);
        int y = height / 4 - 14;
        g.drawCenteredString(font, Component.translatable("gui.mnemic_odyssey.settings.column_keys"), width / 2 - 80, y, 0xAAAAAA);
        g.drawCenteredString(font, Component.translatable("gui.mnemic_odyssey.settings.column_hud"), width / 2 + 80, y, 0xAAAAAA);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }
}
