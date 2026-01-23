package org.faketale.mnemic_odyssey.event;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.ability.MemoryTorrent;
import org.faketale.mnemic_odyssey.capability.ModAttachments;
import org.faketale.mnemic_odyssey.network.MemoryTorrentData;

@EventBusSubscriber(modid = MnemicOdyssey.MODID/*, bus = EventBusSubscriber.Bus.GAME*/)
public class PlayerTickHandler {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event){//监听post阶段而不是PlayerTickEvent，因为事件会触发两次，.Pre（之前）和.Post（之后），所以指定详细阶段
        Player player = event.getEntity();

        //if(player.level().isClientSide) return;

        //客户端-服务端一起更新，进行预测

        MemoryTorrentData data = player.getData(ModAttachments.MEMORY_TORRENT.get());

        //你玩家总特么得是活的吧
        if(player.isAlive()){
            //如果开启了技能，每tick-
            if(data.active()){
                player.setData(ModAttachments.MEMORY_TORRENT, data.addEnergy(-0.2f));
                //如果没了就关闭技能
                if(data.energy() <= 0f){
                    player.setData(ModAttachments.MEMORY_TORRENT, data.withActive(false));
                    //在客户端播放灭火音效，代表没了
                    if(player.level().isClientSide) {
                        player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.PLAYERS, 1.0f, 1.0f, false);
                    }
                }
            }
            else{//没开启技能每tick+
                player.setData(ModAttachments.MEMORY_TORRENT, data.addEnergy(0.2f));

                //在客户端播放经验球音效，代表充满
                //旧值小于最大并且新值等于最大代表首次充满
//            float maxEnergy = MemoryTorrentData.getMaxEnergy();
//            if(player.level().isClientSide && data.energy() < maxEnergy && data.energy() + 0.2f >= maxEnergy) {
//                player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0f, 0.5f, false);
//            }
            }
        }
    }

    // 建立一个静态变量记录上一次的能量值（仅客户端内存，不存存档）
    private static float lastClientEnergy = MemoryTorrentData.getMaxEnergy();//初始化的值要很大，不然初次进游戏会响一声

    //解释为啥为了一个音效单独写一个方法，音符服务端要处理很多玩家，不能存旧变量，而客户端就处理一个可以存
    @SubscribeEvent
    public static void onClientTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        MemoryTorrentData data = player.getData(ModAttachments.MEMORY_TORRENT.get());

        if (!player.level().isClientSide || player != Minecraft.getInstance().player || data.active()) return;

        float currentEnergy = data.energy();

        // 捕捉瞬间：上次没满，这次满了
        float maxEnergy = MemoryTorrentData.getMaxEnergy();
        if (lastClientEnergy < maxEnergy && currentEnergy >= maxEnergy) {
            player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0f, 0.8f, false);
        }

        lastClientEnergy = currentEnergy; // 更新旧值
    }
}
