package org.faketale.mnemic_odyssey.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

// 数据类
public record MemoryTorrentData(float energy, boolean active) {
    private static final float maxEnergy = 100;

    public static final Codec<MemoryTorrentData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.FLOAT.fieldOf("energy").forGetter(MemoryTorrentData::energy),
            Codec.BOOL.fieldOf("active").forGetter(MemoryTorrentData::active)
    ).apply(inst, MemoryTorrentData::new));

    // 网络传输用的 StreamCodec
//    public static final StreamCodec<ByteBuf, MemoryTorrentData> STREAM_CODEC = StreamCodec.composite(
//            ByteBufCodecs.FLOAT, MemoryTorrentData::energy,
//            ByteBufCodecs.BOOL, MemoryTorrentData::active,
//            MemoryTorrentData::new
//    );

    //加能量
    public MemoryTorrentData addEnergy(float var){
        return withEnergy(this.energy + var);
    }

    // 修改能量，状态不变
    public MemoryTorrentData withEnergy(float newEnergy) {
        return new MemoryTorrentData(Math.clamp(newEnergy, 0f, maxEnergy), this.active);
    }

    // 修改状态，能量不变
    public MemoryTorrentData withActive(boolean newActive) {
        return new MemoryTorrentData(this.energy, newActive);
    }

    // 当前能量值是否已满
    public boolean isFull() {
        return energy >= maxEnergy;
    }

    // 获取最大能量值
    public static float getMaxEnergy() {
        return maxEnergy;
    }
}