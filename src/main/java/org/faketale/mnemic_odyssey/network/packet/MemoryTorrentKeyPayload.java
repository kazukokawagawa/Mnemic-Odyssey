package org.faketale.mnemic_odyssey.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.jetbrains.annotations.NotNull;

public record MemoryTorrentKeyPayload() implements CustomPacketPayload {
    public static final Type<MemoryTorrentKeyPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MnemicOdyssey.MODID, "skill_key"));

    // 空包的 StreamCodec，只需要一个触发信号
    public static final StreamCodec<ByteBuf, MemoryTorrentKeyPayload> STREAM_CODEC = StreamCodec.unit(new MemoryTorrentKeyPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}
