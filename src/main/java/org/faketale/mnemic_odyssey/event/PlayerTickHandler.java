package org.faketale.mnemic_odyssey.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.capability.ModAttachments;
import org.faketale.mnemic_odyssey.compendium.CompendiumLogic;
import org.faketale.mnemic_odyssey.network.MemoryTorrentData;
import org.faketale.mnemic_odyssey.network.packet.EnergySyncPayload;

@EventBusSubscriber(modid = MnemicOdyssey.MODID)
public class PlayerTickHandler {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        int delay = player.getData(ModAttachments.TORRENT_READY_DELAY);
        if (delay > 0) player.setData(ModAttachments.TORRENT_READY_DELAY, delay - 1);
        int immunity = player.getData(ModAttachments.CHAOS_IMMUNITY);
        if (immunity > 0) player.setData(ModAttachments.CHAOS_IMMUNITY, immunity - 1);
        MemoryTorrentData data = player.getData(ModAttachments.MEMORY_TORRENT);
        if (!player.isAlive() || !data.active()) return;
        float drain = MemoryTorrentData.getMaxEnergy() / Math.max(1, player.getData(ModAttachments.TORRENT_DURATION));
        MemoryTorrentData updated = data.addEnergy(-drain);
        if (updated.energy() <= 0.001f) updated = updated.withEnergy(0).withActive(false);
        player.setData(ModAttachments.MEMORY_TORRENT, updated);
        if (player.tickCount % 5 == 0) {
            player.serverLevel().sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1, player.getZ(), 4, 0.35, 0.6, 0.35, 0.05);
        }
        if (player.tickCount % 5 == 0 || !updated.active()) {
            PacketDistributor.sendToPlayer(player, new EnergySyncPayload(updated));
        }
    }
}
