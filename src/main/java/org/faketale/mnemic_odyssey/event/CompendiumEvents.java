package org.faketale.mnemic_odyssey.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.compendium.*;
import org.faketale.mnemic_odyssey.item.ModItems;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** 图鉴系统的全部游戏事件：解锁、经验、加成、15 级机制、惩罚。 */
@EventBusSubscriber(modid = MnemicOdyssey.MODID)
public class CompendiumEvents {
    private static final String RAMPAGE_TAG = "mnemic_rampage";
    private static final String RAMPAGE_UNTIL = "mnemic_rampage_until";
    private static final Map<UUID, Long> LAST_INTERACT = new ConcurrentHashMap<>();
    private record Mining(BlockPos pos, long start) {}
    private static final Map<UUID, Mining> MINING = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onMiningStart(PlayerInteractEvent.LeftClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        switch (event.getAction()) {
            case START -> MINING.put(player.getUUID(), new Mining(event.getPos().immutable(), player.level().getGameTime()));
            case ABORT -> MINING.remove(player.getUUID());
            default -> { }
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        MINING.remove(event.getEntity().getUUID());
        LAST_INTERACT.remove(event.getEntity().getUUID());
    }

    /** 每个玩家的互动经验冷却，防止连点刷等级/经验球 */
    private static boolean cooldownOk(ServerPlayer p, int ticks) {
        long now = p.level().getGameTime();
        Long last = LAST_INTERACT.get(p.getUUID());
        if (last != null && now - last < ticks) return false;
        LAST_INTERACT.put(p.getUUID(), now);
        return true;
    }

    // ---------------- 方块：挖掘 / 交互 ----------------

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer p) || p.isCreative()) return;
        Block b = event.getState().getBlock();
        Mining mining = MINING.remove(p.getUUID());
        if (mining != null && mining.pos().equals(event.getPos())) {
            long ticks = Math.max(1, p.level().getGameTime() - mining.start() + 1);
            org.faketale.mnemic_odyssey.ability.MemoryTorrent.charge(p, Math.min(30f, ticks / 20f));
        }
        if (!CompendiumLogic.blockAllowed(b)) return;
        CompendiumLogic.touch(p, CompendiumCategory.BLOCK, CompendiumLogic.blockId(b), 1, true);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer p) || event.getHand() != InteractionHand.MAIN_HAND) return;
        BlockState s = p.level().getBlockState(event.getPos());
        if (!CompendiumLogic.blockAllowed(s.getBlock())) return;
        if (!cooldownOk(p, 10)) return;
        CompendiumLogic.touch(p, CompendiumCategory.BLOCK, CompendiumLogic.blockId(s.getBlock()), 1, true);
    }

    /** 方块 10 级：概率双倍掉落 */
    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof ServerPlayer p)) return;
        if (CompendiumLogic.level(p, CompendiumCategory.BLOCK, CompendiumLogic.blockId(event.getState().getBlock())) < 10) return;
        if (p.getRandom().nextFloat() >= CompendiumLogic.DOUBLE_DROP_CHANCE) return;
        List<ItemEntity> extra = new ArrayList<>();
        for (ItemEntity d : event.getDrops()) {
            ItemEntity c = new ItemEntity(d.level(), d.getX(), d.getY(), d.getZ(), d.getItem().copy());
            c.setDeltaMovement(d.getDeltaMovement());
            extra.add(c);
        }
        event.getDrops().addAll(extra);
    }

    /** 方块 5 级：更快的开采 */
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Block b = event.getState().getBlock();
        if (!CompendiumLogic.blockAllowed(b)) return;
        if (CompendiumLogic.level(event.getEntity(), CompendiumCategory.BLOCK, CompendiumLogic.blockId(b)) >= 5) {
            event.setNewSpeed(event.getNewSpeed() * (1f + CompendiumLogic.MINING_SPEED_BONUS));
        }
    }

    // ---------------- 生物 / 头目 ----------------

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getTarget().getTags().contains(RAMPAGE_TAG)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer p) || event.getHand() != InteractionHand.MAIN_HAND) return;
        if (event.getTarget().getTags().contains(RAMPAGE_TAG)) {
            event.setCanceled(true);
            return;
        }
        CompendiumCategory cat = CompendiumLogic.classify(event.getTarget());
        if (cat == null || !cooldownOk(p, 20)) return;
        CompendiumLogic.touch(p, cat, CompendiumLogic.entityId(event.getTarget().getType()), 1, true);
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        Entity victim = event.getEntity();
        Entity killer = event.getSource().getEntity();
        if (victim.level().isClientSide) return;

        //玩家被本 mod 敌对生物 / 头目击杀：图鉴降级
        if (victim instanceof ServerPlayer dead && killer != null) {
            boolean modMob = CompendiumLogic.entityId(killer.getType()).getNamespace().equals(MnemicOdyssey.MODID);
            if (modMob && (killer instanceof Enemy || killer.getType().is(ModTags.BOSSES))) {
                CompendiumLogic.penalize(dead);
            }
            return;
        }
        //玩家击杀生物 / 头目：获得经验（满级后的额外经验球在 LivingExperienceDropEvent 中处理）
        if (killer instanceof ServerPlayer p) {
            CompendiumCategory cat = CompendiumLogic.classify(victim);
            if (cat != null) CompendiumLogic.touch(p, cat, CompendiumLogic.entityId(victim.getType()), 1, false);
        }
    }

    /** 满级：不再升级，而是掉落更多经验球 */
    @SubscribeEvent
    public static void onXpDrop(LivingExperienceDropEvent event) {
        if (event.getAttackingPlayer() == null) return;
        CompendiumCategory cat = CompendiumLogic.classify(event.getEntity());
        if (cat == null) return;
        if (CompendiumLogic.level(event.getAttackingPlayer(), cat, CompendiumLogic.entityId(event.getEntity().getType())) >= CompendiumLogic.MAX_LEVEL) {
            event.setDroppedExperience(event.getDroppedExperience() * 2 + 2);
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        Entity target = event.getEntity();

        //受击者是玩家：方块 15 级机制
        if (target instanceof ServerPlayer p) {
            EnumSet<BlockPerk> perks = CompendiumLogic.activePerks(p);
            if (perks.contains(BlockPerk.MAGMA_IMMUNE) && event.getSource().is(DamageTypes.HOT_FLOOR)) {
                event.setCanceled(true);
                return;
            }
            if (perks.contains(BlockPerk.FALL_REDUCTION) && event.getSource().is(DamageTypeTags.IS_FALL)) {
                event.setAmount(event.getAmount() * 0.3f);
            }
            return;
        }

        //攻击者是玩家：生物 / 头目 5 级伤害加成，10 级概率破甲
        if (event.getSource().getEntity() instanceof ServerPlayer p) {
            CompendiumCategory cat = CompendiumLogic.classify(target);
            if (cat == null) return;
            int lv = CompendiumLogic.level(p, cat, CompendiumLogic.entityId(target.getType()));
            if (lv >= 5) event.setAmount(event.getAmount() * (1f + CompendiumLogic.DAMAGE_BONUS));
            if (lv >= 10 && p.getRandom().nextFloat() < CompendiumLogic.ARMOR_PIERCE_CHANCE) {
                event.addReductionModifier(DamageContainer.Reduction.ARMOR, (container, reduction) -> 0f);
            }
        }
    }

    // ---------------- 物品解锁 / 每秒逻辑 ----------------

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer p)) return;
        int t = p.tickCount;

        //背包中出现本 mod 物品即解锁
        if (t % 20 == 0) {
            for (int i = 0; i < p.getInventory().getContainerSize(); i++) {
                ItemStack st = p.getInventory().getItem(i);
                if (st.isEmpty()) continue;
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(st.getItem());
                if (id.getNamespace().equals(MnemicOdyssey.MODID)) CompendiumLogic.unlockItem(p, id);
            }
        }

        //泡在水/岩浆里算与其互动
        if (t % 40 == 0) {
            if (p.isInWater()) CompendiumLogic.touch(p, CompendiumCategory.BLOCK, CompendiumLogic.blockId(Blocks.WATER), 1, false);
            if (p.isInLava()) CompendiumLogic.touch(p, CompendiumCategory.BLOCK, CompendiumLogic.blockId(Blocks.LAVA), 1, false);
        }

        if (!CompendiumLogic.torrentActive(p)) return;

        //方块 15 级机制（忆如泉涌期间，且背包持有该方块）
        if (t % 20 == 0) {
            for (BlockPerk perk : CompendiumLogic.activePerks(p)) {
                if (perk.effect != null) {
                    p.addEffect(new MobEffectInstance(perk.effect, 25,
                            perk.amplifier, true, false, true));
                }
            }
        }

        //生物 / 头目 15 级“暴走”：附近该类型生物无法移动、交互、攻击
        if (t % 5 == 0) {
            Set<EntityType<?>> types = CompendiumLogic.rampageTypes(p);
            if (types.isEmpty()) return;
            AABB box = p.getBoundingBox().inflate(48);
            long now = p.level().getGameTime();
            for (Mob m : p.level().getEntitiesOfClass(Mob.class, box, m -> types.contains(m.getType()))) {
                if (!m.getTags().contains(RAMPAGE_TAG)) {
                    if (m.isNoAi()) continue; //本来就无 AI 的不去动它
                    m.setNoAi(true);
                    m.setTarget(null);
                    m.addTag(RAMPAGE_TAG);
                }
                m.getPersistentData().putLong(RAMPAGE_UNTIL, now + 15);
            }
        }
    }

    /** 暴走结束后恢复生物 AI */
    @SubscribeEvent
    public static void onMobTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Mob m) || m.level().isClientSide) return;
        if (m.getTags().isEmpty() || !m.getTags().contains(RAMPAGE_TAG)) return;
        if (m.level().getGameTime() <= m.getPersistentData().getLong(RAMPAGE_UNTIL)) {
            m.getNavigation().stop();
            m.setDeltaMovement(0, 0, 0);
        } else {
            m.setNoAi(false);
            m.removeTag(RAMPAGE_TAG);
            m.getPersistentData().remove(RAMPAGE_UNTIL);
        }
    }

    // ---------------- 图鉴物品无法破坏 ----------------

    @SubscribeEvent
    public static void onInvulnerable(EntityInvulnerabilityCheckEvent event) {
        if (event.getEntity() instanceof ItemEntity ie && ie.getItem().is(ModItems.MemoryCompendium.get())
                && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            event.setInvulnerable(true);
        }
    }

    @SubscribeEvent
    public static void onItemJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ItemEntity ie && ie.getItem().is(ModItems.MemoryCompendium.get())) {
            ie.setUnlimitedLifetime();
        }
    }
}
