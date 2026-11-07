package org.faketale.mnemic_odyssey.compendium;

import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

/** 方块 15 级机制：忆如泉涌期间且背包持有该方块时生效。 */
public enum BlockPerk {
    MAGMA_IMMUNE(null, 0),                              // 不受岩浆块伤害（事件处理）
    FIRE_RESIST(MobEffects.FIRE_RESISTANCE, 0),
    TRADE_DISCOUNT(MobEffects.HERO_OF_THE_VILLAGE, 0),  // 更便宜的村民交易
    FALL_REDUCTION(null, 0),                            // 摔落伤害减免（事件处理）
    REGEN(MobEffects.REGENERATION, 0),
    NIGHT_VISION(MobEffects.NIGHT_VISION, 0),
    DEFENSE(MobEffects.DAMAGE_RESISTANCE, 0),
    HASTE(MobEffects.DIG_SPEED, 0);

    public final Holder<MobEffect> effect;
    public final int amplifier;

    BlockPerk(Holder<MobEffect> effect, int amplifier) {
        this.effect = effect;
        this.amplifier = amplifier;
    }

    public String langKey() {
        return "compendium.mnemic_odyssey.perk." + name().toLowerCase();
    }

    public static BlockPerk of(Block b) {
        BlockState s = b.defaultBlockState();
        if (b == Blocks.MAGMA_BLOCK) return MAGMA_IMMUNE;
        if (b == Blocks.LAVA || b == Blocks.FIRE || b == Blocks.SOUL_FIRE || b == Blocks.CAMPFIRE
                || b == Blocks.SOUL_CAMPFIRE || b == Blocks.LAVA_CAULDRON) return FIRE_RESIST;
        if (b == Blocks.EMERALD_BLOCK || b == Blocks.EMERALD_ORE || b == Blocks.DEEPSLATE_EMERALD_ORE) return TRADE_DISCOUNT;
        if (b == Blocks.WATER || b == Blocks.MOSS_BLOCK || b == Blocks.WATER_CAULDRON) return REGEN;
        if (b == Blocks.HAY_BLOCK || b == Blocks.SLIME_BLOCK || b == Blocks.HONEY_BLOCK
                || s.is(BlockTags.WOOL) || s.is(BlockTags.LEAVES) || s.is(BlockTags.BEDS)) return FALL_REDUCTION;
        if (s.getLightEmission() >= 8) return NIGHT_VISION;
        SoundType st = s.getSoundType();
        boolean stoneLike = st == SoundType.STONE || st == SoundType.DEEPSLATE || st == SoundType.DEEPSLATE_BRICKS
                || st == SoundType.TUFF || st == SoundType.BASALT || st == SoundType.NETHER_BRICKS
                || st == SoundType.METAL || st == SoundType.ANVIL || st == SoundType.COPPER
                || s.is(BlockTags.BASE_STONE_OVERWORLD) || s.is(BlockTags.BASE_STONE_NETHER);
        if (stoneLike) return DEFENSE;
        return HASTE;
    }
}
