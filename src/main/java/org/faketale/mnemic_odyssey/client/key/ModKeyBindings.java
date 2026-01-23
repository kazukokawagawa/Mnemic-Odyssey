package org.faketale.mnemic_odyssey.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.lwjgl.glfw.GLFW;

import javax.swing.text.JTextComponent;

public class ModKeyBindings {
    @EventBusSubscriber(modid = MnemicOdyssey.MODID, /*bus = EventBusSubscriber.Bus.MOD,*/ value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(OPEN_CODEX_GUI);
            event.register(MEMORY_TORRENT);
        }
    }

    //打开图鉴
    public static final KeyMapping OPEN_CODEX_GUI = new KeyMapping(
            "key.mnemic_odyssey.open_memory_codex",//描述文字
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,//默认键位
            "mnemic_odyssey"//分类文字
    );

    //释放技能：忆如泉涌
    public static final KeyMapping MEMORY_TORRENT = new KeyMapping(
            "key.mnemic_odyssey.memory_torrent",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "mnemic_odyssey"
    );
}
