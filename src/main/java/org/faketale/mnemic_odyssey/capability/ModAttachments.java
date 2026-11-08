package org.faketale.mnemic_odyssey.capability;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.network.MemoryTorrentData;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MnemicOdyssey.MODID);

    //是否已经获得记忆图鉴（首次进入世界）
    public static final Supplier<AttachmentType<Boolean>> HAS_JOINED_WORLD = ATTACHMENTS
            .register("has_joined_world", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build());

    //图鉴进度（属于玩家，死亡保留；图鉴物品只是打开它的钥匙）
    public static final Supplier<AttachmentType<org.faketale.mnemic_odyssey.compendium.CompendiumData>> COMPENDIUM = ATTACHMENTS
            .register("compendium", () -> AttachmentType.builder(org.faketale.mnemic_odyssey.compendium.CompendiumData::new)
                    .serialize(org.faketale.mnemic_odyssey.compendium.CompendiumData.CODEC)
                    .copyOnDeath()
                    .build());

    public static final Supplier<AttachmentType<java.util.Map<net.minecraft.resources.ResourceLocation, java.util.List<net.minecraft.resources.ResourceLocation>>>> CRAFT_HISTORY = ATTACHMENTS
            .register("craft_history", () -> AttachmentType.<java.util.Map<net.minecraft.resources.ResourceLocation, java.util.List<net.minecraft.resources.ResourceLocation>>>builder(() -> new java.util.HashMap<>())
                    .serialize(Codec.unboundedMap(net.minecraft.resources.ResourceLocation.CODEC, net.minecraft.resources.ResourceLocation.CODEC.listOf())
                            .xmap(java.util.HashMap::new, m -> m))
                    .copyOnDeath().build());

    public static final Supplier<AttachmentType<Integer>> TORRENT_READY_DELAY = ATTACHMENTS
            .register("torrent_ready_delay", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());
    public static final Supplier<AttachmentType<Integer>> TORRENT_DURATION = ATTACHMENTS
            .register("torrent_duration", () -> AttachmentType.builder(() -> 600).serialize(Codec.INT).build());

    // Remaining immunity in player ticks; persists across logout.
    public static final Supplier<AttachmentType<Integer>> CHAOS_IMMUNITY = ATTACHMENTS
            .register("chaos_immunity", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).copyOnDeath().build());

    //忆如泉涌技能充能进度
//    public static final Supplier<AttachmentType<Float>> MEMORY_TORRENT = ATTACHMENT_TYPES
//            .register("memory_torrent", () -> AttachmentType.builder(() -> 0.0f).serialize(Codec.FLOAT).build());
//
//    //忆如泉涌技能开启情况
//    public static final Supplier<AttachmentType<Boolean>> MEMORY_TORRENT_ACTIVE = ATTACHMENT_TYPES
//            .register("memory_torrent_active", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build());

    public static final Supplier<AttachmentType<MemoryTorrentData>> MEMORY_TORRENT = ATTACHMENTS
            .register("memory_torrent", () -> AttachmentType.builder(() -> new MemoryTorrentData(0f, false))
                    .serialize(MemoryTorrentData.CODEC) //保存到存档
                    //.copyOnDeath()                      //死亡不重置
                    .build());
}
