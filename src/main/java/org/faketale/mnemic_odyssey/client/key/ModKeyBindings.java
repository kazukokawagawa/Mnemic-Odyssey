package org.faketale.mnemic_odyssey.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.lwjgl.glfw.GLFW;

/** 游戏按键设置中只保留“打开设置菜单”，其余按键在模组设置菜单中修改（见 ModConfig） */
public class ModKeyBindings {
    @EventBusSubscriber(modid = MnemicOdyssey.MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(OPEN_SETTINGS);
        }
    }

    //打开设置菜单
    public static final KeyMapping OPEN_SETTINGS = new KeyMapping(
            "key.mnemic_odyssey.open_settings",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "mnemic_odyssey"
    );
}
