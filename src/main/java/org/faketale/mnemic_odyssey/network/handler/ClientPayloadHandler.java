package org.faketale.mnemic_odyssey.network.handler;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.faketale.mnemic_odyssey.capability.ModAttachments;
import org.faketale.mnemic_odyssey.network.MemoryTorrentData;
import org.faketale.mnemic_odyssey.network.packet.EnergySyncPayload;

//客户端处理器
public class ClientPayloadHandler {
    //更新本地 Attachment
    public static void handleMemoryTorrentData(final EnergySyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().setData(ModAttachments.MEMORY_TORRENT.get(), new MemoryTorrentData(payload.data().energy(), payload.data().active()));
        });
    }
}
