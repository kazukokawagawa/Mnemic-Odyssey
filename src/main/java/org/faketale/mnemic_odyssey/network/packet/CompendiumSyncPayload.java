package org.faketale.mnemic_odyssey.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** 图鉴进度同步（full=true 时先清空客户端数据）。 */
public record CompendiumSyncPayload(boolean full, List<Entry> entries) implements CustomPacketPayload {
    public static final Type<CompendiumSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MnemicOdyssey.MODID, "compendium_sync"));

    public record Entry(int category, ResourceLocation id, int level, int xp) {
        public static final StreamCodec<ByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, Entry::category,
                ResourceLocation.STREAM_CODEC, Entry::id,
                ByteBufCodecs.VAR_INT, Entry::level,
                ByteBufCodecs.VAR_INT, Entry::xp,
                Entry::new);
    }

    public static final StreamCodec<ByteBuf, CompendiumSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, CompendiumSyncPayload::full,
            Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), CompendiumSyncPayload::entries,
            CompendiumSyncPayload::new);

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
