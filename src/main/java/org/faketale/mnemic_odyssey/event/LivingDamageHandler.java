package org.faketale.mnemic_odyssey.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.ability.MemoryTorrent;
import org.faketale.mnemic_odyssey.capability.ModAttachments;
import org.faketale.mnemic_odyssey.network.MemoryTorrentData;
import org.faketale.mnemic_odyssey.network.packet.EnergySyncPayload;

@EventBusSubscriber(modid = MnemicOdyssey.MODID/*, bus = EventBusSubscriber.Bus.GAME*/)
public class LivingDamageHandler {
    //攻击会回复一部分条
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event){
        //攻击者是玩家
        if(event.getSource().getEntity() instanceof Player player){
            MemoryTorrentData newData = player.getData(ModAttachments.MEMORY_TORRENT).addEnergy(2.0f);//新的数据

            player.setData(ModAttachments.MEMORY_TORRENT, newData);//更新服务端数据

            //更新客户端数据
            if(!player.level().isClientSide) {
                //发包同步客户端
                PacketDistributor.sendToPlayer((ServerPlayer) player, new EnergySyncPayload(newData));
            }
        }
    }
}
