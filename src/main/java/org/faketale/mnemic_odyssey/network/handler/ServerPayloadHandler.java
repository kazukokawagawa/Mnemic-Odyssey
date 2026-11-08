package org.faketale.mnemic_odyssey.network.handler;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.faketale.mnemic_odyssey.capability.ModAttachments;
import org.faketale.mnemic_odyssey.network.MemoryTorrentData;
import org.faketale.mnemic_odyssey.network.packet.EnergySyncPayload;
import org.faketale.mnemic_odyssey.network.packet.MemoryTorrentKeyPayload;

public class ServerPayloadHandler {
    public static void handleMemoryTorrentKey(final MemoryTorrentKeyPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            MemoryTorrentData data = player.getData(ModAttachments.MEMORY_TORRENT.get());

            // 只有能量满 100 且当前未开启时，才能开启
            if (!data.active() && data.energy() >= MemoryTorrentData.getMaxEnergy() && player.getData(ModAttachments.TORRENT_READY_DELAY) <= 0) {
                // 修改服务端数据 (开启状态)
                MemoryTorrentData newData = new MemoryTorrentData(data.energy(), true);
                player.setData(ModAttachments.MEMORY_TORRENT.get(), newData);
                player.removeEffect(org.faketale.mnemic_odyssey.effect.ModEffects.MEMORY_CHAOS);
                player.setData(ModAttachments.CHAOS_IMMUNITY, 800);
                player.setData(ModAttachments.TORRENT_DURATION, org.faketale.mnemic_odyssey.compendium.CompendiumLogic.hasCompendium(player) ? 600 : 300);

                // 给触发者发送音效
                // 在服务端对 ServerPlayer 使用此方法，音效只发给该玩家本人
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundSoundPacket(
                            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.FLINTANDSTEEL_USE),
                            SoundSource.PLAYERS,
                            player.getX(), player.getY(), player.getZ(),
                            1.0f, 1.3f, player.level().getRandom().nextLong()
                    ));
                }
                // 触发音效
//                player.level().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0f, 0.5f);

                // 立即同步回客户端，让玩家看到横条变色
                PacketDistributor.sendToPlayer((ServerPlayer) player, new EnergySyncPayload(newData));
            }
            else if (data.active()) {
                // 如果已经开启，按键则为手动关闭
                player.setData(ModAttachments.MEMORY_TORRENT.get(), new MemoryTorrentData(data.energy(), false));
                PacketDistributor.sendToPlayer((ServerPlayer) player, new EnergySyncPayload(player.getData(ModAttachments.MEMORY_TORRENT.get())));

                //跟能量耗尽一样，发送一个熄火的音效
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundSoundPacket(
                            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.FIRE_EXTINGUISH),
                            SoundSource.PLAYERS,
                            player.getX(), player.getY(), player.getZ(),
                            1.0f, 1.0f, player.level().getRandom().nextLong()
                    ));
                }
            }
        });
    }
}