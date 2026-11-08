package org.faketale.mnemic_odyssey.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record CraftHistoryPayload(List<Entry> entries) implements CustomPacketPayload {
    public static final Type<CraftHistoryPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MnemicOdyssey.MODID, "craft_history"));

    public record Entry(ResourceLocation item, List<ResourceLocation> recipes) {
        public static final StreamCodec<ByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, Entry::item,
                ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), Entry::recipes,
                Entry::new);
    }

    public static final StreamCodec<ByteBuf, CraftHistoryPayload> STREAM_CODEC = StreamCodec.composite(
            Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), CraftHistoryPayload::entries,
            CraftHistoryPayload::new);

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
