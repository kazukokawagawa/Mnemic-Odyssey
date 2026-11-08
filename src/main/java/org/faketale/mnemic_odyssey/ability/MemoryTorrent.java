package org.faketale.mnemic_odyssey.ability;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.faketale.mnemic_odyssey.capability.ModAttachments;
import org.faketale.mnemic_odyssey.network.MemoryTorrentData;
import org.faketale.mnemic_odyssey.network.packet.EnergySyncPayload;

/** Server-authoritative charging; one energy per damage point or second of mining. */
public final class MemoryTorrent {
    private MemoryTorrent() {}

    public static void charge(ServerPlayer player, float amount) {
        MemoryTorrentData data = player.getData(ModAttachments.MEMORY_TORRENT);
        if (data.active() || amount <= 0 || !Float.isFinite(amount)) return;
        MemoryTorrentData updated = data.addEnergy(amount);
        if (!data.isFull() && updated.isFull()) player.setData(ModAttachments.TORRENT_READY_DELAY, 30);
        player.setData(ModAttachments.MEMORY_TORRENT, updated);
        PacketDistributor.sendToPlayer(player, new EnergySyncPayload(updated));
    }
}
