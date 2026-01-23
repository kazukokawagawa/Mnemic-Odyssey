package org.faketale.mnemic_odyssey.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.network.MemoryTorrentData;
import org.jetbrains.annotations.NotNull;

public record EnergySyncPayload(MemoryTorrentData data) implements CustomPacketPayload {
    public static final Type<EnergySyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MnemicOdyssey.MODID, "energy_sync"));

    public static final StreamCodec<ByteBuf, EnergySyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, p -> p.data().energy(),   // 先拿 data，再拿 energy
            ByteBufCodecs.BOOL, p -> p.data().active(),    // 先拿 data，再拿 active
            // 最后将读到的 float 和 bool 重新包装成 MemoryTorrentData，再塞进 Payload
            (energy, active) -> new EnergySyncPayload(new MemoryTorrentData(energy, active))
    );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
