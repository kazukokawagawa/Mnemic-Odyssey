package org.faketale.mnemic_odyssey.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.capability.ModAttachments;
import org.faketale.mnemic_odyssey.gamerule.ModGameRules;
import org.faketale.mnemic_odyssey.network.MemoryTorrentData;
import org.faketale.mnemic_odyssey.network.packet.EnergySyncPayload;

@EventBusSubscriber(modid = MnemicOdyssey.MODID/*, bus = EventBusSubscriber.Bus.GAME*/)
public class PlayerCloneHandler {
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        if (newPlayer.level().isClientSide) return;

        //是否保留忆如泉涌条
        boolean keepData = newPlayer.level().getGameRules().getBoolean(ModGameRules.RULE_KEEP_MEMORY_TORRENT);

        //规则开启或玩家从末地回来
        if (keepData || !event.isWasDeath()) {
            MemoryTorrentData oldData = oldPlayer.getData(ModAttachments.MEMORY_TORRENT.get());

            //将旧数据克隆到新玩家身上
            newPlayer.setData(ModAttachments.MEMORY_TORRENT.get(), oldData);

            //发包同步
//            if (newPlayer instanceof ServerPlayer serverPlayer) {
//                PacketDistributor.sendToPlayer(serverPlayer, new EnergySyncPayload(oldData));
//            }
        }
    }
}
