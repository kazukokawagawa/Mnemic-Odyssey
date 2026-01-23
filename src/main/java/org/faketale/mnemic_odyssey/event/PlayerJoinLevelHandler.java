package org.faketale.mnemic_odyssey.event;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.capability.ModAttachments;
import org.faketale.mnemic_odyssey.network.MemoryTorrentData;
import org.faketale.mnemic_odyssey.network.packet.EnergySyncPayload;

@EventBusSubscriber(modid = MnemicOdyssey.MODID/*, bus = EventBusSubscriber.Bus.GAME*/)
public class PlayerJoinLevelHandler {
    //确保每次玩家加入世界数据都是新的（主要为了死亡重生）
    @SubscribeEvent
    public static void onPlayerJoinLevel(EntityJoinLevelEvent event) {
        // 确保是玩家，且是服务端
        if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
            // 获取当前数据并同步
            MemoryTorrentData data = player.getData(ModAttachments.MEMORY_TORRENT.get());
            PacketDistributor.sendToPlayer(player, new EnergySyncPayload(data));
        }
    }
}
