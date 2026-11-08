package org.faketale.mnemic_odyssey.compendium;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.network.PacketDistributor;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.capability.ModAttachments;
import org.faketale.mnemic_odyssey.item.ModItems;
import org.faketale.mnemic_odyssey.network.packet.CompendiumSyncPayload;

import java.util.*;

public final class CompendiumLogic {
    private CompendiumLogic() {}

    public static final int MAX_LEVEL = 15;
    public static final float MINING_SPEED_BONUS = 0.25f;   // 5 级（方块）
    public static final float DOUBLE_DROP_CHANCE = 0.20f;   // 10 级（方块）
    public static final float DAMAGE_BONUS = 0.15f;         // 5 级（生物/头目）
    public static final float ARMOR_PIERCE_CHANCE = 0.25f;  // 10 级（生物/头目）

    /** Faction tags are datapack-extensible. Unassigned items remain neutral. */
    private static final net.minecraft.tags.TagKey<Item> MATERIALIST = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MnemicOdyssey.MODID, "faction/materialist"));
    private static final net.minecraft.tags.TagKey<Item> IDEALIST = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MnemicOdyssey.MODID, "faction/idealist"));

    public static int xpNeeded(int level) {
        return 3 + level * 2;
    }

    // ---------- 分类 / id ----------

    public static ResourceLocation blockId(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b);
    }

    public static ResourceLocation entityId(EntityType<?> t) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(t);
    }

    public static boolean blockAllowed(Block b) {
        if (b == Blocks.AIR || b == Blocks.CAVE_AIR || b == Blocks.VOID_AIR) return false;
        String ns = blockId(b).getNamespace();
        return ns.equals("minecraft") || ns.equals(MnemicOdyssey.MODID);
    }

    /** 实体所属门类：本 mod 头目 -> BOSS；原版生物 -> MOB；其他 -> null */
    public static CompendiumCategory classify(Entity e) {
        if (!(e instanceof LivingEntity) || e instanceof Player || e instanceof ArmorStand) return null;
        if (e.getType().is(ModTags.BOSSES)) return CompendiumCategory.BOSS;
        if (entityId(e.getType()).getNamespace().equals("minecraft")) return CompendiumCategory.MOB;
        return null;
    }

    public static Component displayName(CompendiumCategory cat, ResourceLocation id) {
        return switch (cat) {
            case BLOCK -> BuiltInRegistries.BLOCK.get(id).getName();
            case MOB, BOSS -> BuiltInRegistries.ENTITY_TYPE.get(id).getDescription();
            case ITEM -> BuiltInRegistries.ITEM.get(id).getDescription();
        };
    }

    public static Component faction(ResourceLocation itemId) {
        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(itemId));
        String f = stack.is(MATERIALIST) ? "materialist" : stack.is(IDEALIST) ? "idealist" : "neutral";
        return Component.translatable("gui.mnemic_odyssey.compendium.faction." + f);
    }

    public static ItemStack iconFor(CompendiumCategory cat, ResourceLocation id) {
        switch (cat) {
            case BLOCK -> {
                Block b = BuiltInRegistries.BLOCK.get(id);
                if (b == Blocks.WATER) return new ItemStack(Items.WATER_BUCKET);
                if (b == Blocks.LAVA) return new ItemStack(Items.LAVA_BUCKET);
                Item i = b.asItem();
                return new ItemStack(i == Items.AIR ? Items.BARRIER : i);
            }
            case ITEM -> {
                return new ItemStack(BuiltInRegistries.ITEM.get(id));
            }
            default -> {
                Item egg = net.minecraft.world.item.SpawnEggItem.byId(BuiltInRegistries.ENTITY_TYPE.get(id)) == null
                        ? Items.NAME_TAG
                        : net.minecraft.world.item.SpawnEggItem.byId(BuiltInRegistries.ENTITY_TYPE.get(id));
                return new ItemStack(egg);
            }
        }
    }

    // ---------- 查询 ----------

    public static int level(Player p, CompendiumCategory cat, ResourceLocation id) {
        CompendiumProgress pr = p.getData(ModAttachments.COMPENDIUM).get(cat, id);
        return pr == null ? 0 : pr.level;
    }

    public static boolean hasCompendium(Player p) {
        for (int i = 0; i < p.getInventory().getContainerSize(); i++) {
            if (p.getInventory().getItem(i).is(ModItems.MemoryCompendium.get())) return true;
        }
        return false;
    }

    public static boolean torrentActive(Player p) {
        return p.getData(ModAttachments.MEMORY_TORRENT).active();
    }

    public static boolean holdsBlock(Player p, Block b) {
        Item it = b == Blocks.WATER ? Items.WATER_BUCKET : b == Blocks.LAVA ? Items.LAVA_BUCKET : b.asItem();
        if (it == Items.AIR) return false;
        for (int i = 0; i < p.getInventory().getContainerSize(); i++) {
            if (p.getInventory().getItem(i).is(it)) return true;
        }
        return false;
    }

    /** 当前生效的方块 15 级机制 */
    public static EnumSet<BlockPerk> activePerks(Player p) {
        EnumSet<BlockPerk> set = EnumSet.noneOf(BlockPerk.class);
        if (!torrentActive(p)) return set;
        for (Map.Entry<ResourceLocation, CompendiumProgress> e : p.getData(ModAttachments.COMPENDIUM).of(CompendiumCategory.BLOCK).entrySet()) {
            if (e.getValue().level < MAX_LEVEL) continue;
            Block b = BuiltInRegistries.BLOCK.get(e.getKey());
            if (holdsBlock(p, b)) set.add(BlockPerk.of(b));
        }
        return set;
    }

    /** 已达 15 级的生物/头目类型 */
    public static Set<EntityType<?>> rampageTypes(Player p) {
        Set<EntityType<?>> set = new HashSet<>();
        CompendiumData d = p.getData(ModAttachments.COMPENDIUM);
        for (CompendiumCategory c : new CompendiumCategory[]{CompendiumCategory.MOB, CompendiumCategory.BOSS}) {
            for (Map.Entry<ResourceLocation, CompendiumProgress> e : d.of(c).entrySet()) {
                if (e.getValue().level >= MAX_LEVEL) set.add(BuiltInRegistries.ENTITY_TYPE.get(e.getKey()));
            }
        }
        return set;
    }

    // ---------- 增加经验 ----------

    /**
     * 一次互动/击杀。首次接触解锁条目；未满级时获得经验；满级后不再升级，
     * 若 orbs 为 true 则改为掉落额外经验球。
     */
    public static void touch(ServerPlayer p, CompendiumCategory cat, ResourceLocation id, int amount, boolean orbs) {
        CompendiumData data = p.getData(ModAttachments.COMPENDIUM);
        Map<ResourceLocation, CompendiumProgress> m = data.of(cat);
        CompendiumProgress pr = m.get(id);
        boolean created = pr == null;
        if (created) {
            pr = new CompendiumProgress(1, 0);
            m.put(id, pr);
            p.displayClientMessage(Component.translatable("message.mnemic_odyssey.compendium.unlocked", displayName(cat, id)).withStyle(ChatFormatting.GOLD), true);
        }
        if (cat.leveled) {
            if (pr.level >= MAX_LEVEL) {
                if (orbs && !created) {
                    ExperienceOrb.award((ServerLevel) p.level(), p.position(), 1 + p.getRandom().nextInt(3));
                }
            } else {
                pr.xp += amount;
                while (pr.level < MAX_LEVEL && pr.xp >= xpNeeded(pr.level)) {
                    pr.xp -= xpNeeded(pr.level);
                    pr.level++;
                    onLevelUp(p, cat, id, pr.level);
                }
                if (pr.level >= MAX_LEVEL) pr.xp = 0;
            }
        }
        send(p, false, List.of(entry(cat, id, pr)));
    }

    /** 仅解锁（用于物品） */
    public static void unlockItem(ServerPlayer p, ResourceLocation id) {
        CompendiumData data = p.getData(ModAttachments.COMPENDIUM);
        if (data.of(CompendiumCategory.ITEM).containsKey(id)) return;
        CompendiumProgress pr = new CompendiumProgress(1, 0);
        data.of(CompendiumCategory.ITEM).put(id, pr);
      
        p.displayClientMessage(Component.translatable("message.mnemic_odyssey.compendium.unlocked", displayName(CompendiumCategory.ITEM, id)).withStyle(ChatFormatting.GOLD), true);
        send(p, false, List.of(entry(CompendiumCategory.ITEM, id, pr)));
    }

    private static void onLevelUp(ServerPlayer p, CompendiumCategory cat, ResourceLocation id, int level) {
        Component name = displayName(cat, id);
        if (level == 5 || level == 10 || level == MAX_LEVEL) {
            p.sendSystemMessage(Component.translatable("message.mnemic_odyssey.compendium.milestone", name, level).withStyle(ChatFormatting.LIGHT_PURPLE));
            p.level().playSound(null, p.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8f, 1.2f);
        } else {
            p.displayClientMessage(Component.translatable("message.mnemic_odyssey.compendium.level_up", name, level).withStyle(ChatFormatting.YELLOW), true);
        }
    }

    /** 本 mod 敌对生物击杀玩家：总等级 >= 10 时随机减少 5 级（每个条目最低 1 级） */
    public static void penalize(ServerPlayer p) {
        CompendiumData data = p.getData(ModAttachments.COMPENDIUM);
        if (data.totalLevels() < 10) return;
        List<Object[]> changed = new ArrayList<>();
        for (int n = 0; n < 5; n++) {
            List<Object[]> cands = new ArrayList<>();
            for (CompendiumCategory c : CompendiumCategory.values()) {
                if (!c.leveled) continue;
                for (Map.Entry<ResourceLocation, CompendiumProgress> e : data.of(c).entrySet()) {
                    if (e.getValue().level > 1) cands.add(new Object[]{c, e.getKey(), e.getValue()});
                }
            }
            if (cands.isEmpty()) break;
            Object[] pick = cands.get(p.getRandom().nextInt(cands.size()));
            CompendiumProgress pr = (CompendiumProgress) pick[2];
            pr.level--;
            pr.xp = 0;
            changed.add(pick);
        }
        if (changed.isEmpty()) return;
        List<CompendiumSyncPayload.Entry> out = new ArrayList<>();
        for (Object[] o : changed) out.add(entry((CompendiumCategory) o[0], (ResourceLocation) o[1], (CompendiumProgress) o[2]));
        send(p, false, out);
        p.sendSystemMessage(Component.translatable("message.mnemic_odyssey.compendium.penalty", changed.size()).withStyle(ChatFormatting.RED));
    }

    // ---------- 同步 ----------

    private static CompendiumSyncPayload.Entry entry(CompendiumCategory c, ResourceLocation id, CompendiumProgress p) {
        return new CompendiumSyncPayload.Entry(c.ordinal(), id, p.level, p.xp);
    }

    public static void sendFull(ServerPlayer p) {
        CompendiumData d = p.getData(ModAttachments.COMPENDIUM);
        List<CompendiumSyncPayload.Entry> all = new ArrayList<>();
        for (CompendiumCategory c : CompendiumCategory.values()) {
            d.of(c).forEach((id, pr) -> all.add(entry(c, id, pr)));
        }
        send(p, true, all);
    }

    private static void send(ServerPlayer p, boolean full, List<CompendiumSyncPayload.Entry> entries) {
        PacketDistributor.sendToPlayer(p, new CompendiumSyncPayload(full, new ArrayList<>(entries)));
    }
}
