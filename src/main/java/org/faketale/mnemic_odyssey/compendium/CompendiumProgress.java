package org.faketale.mnemic_odyssey.compendium;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** 单个条目的记忆进度（可变）。 */
public class CompendiumProgress {
    public static final Codec<CompendiumProgress> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("level").forGetter(p -> p.level),
            Codec.INT.fieldOf("xp").forGetter(p -> p.xp)
    ).apply(i, CompendiumProgress::new));

    public int level;
    public int xp;

    public CompendiumProgress(int level, int xp) {
        this.level = Math.max(1, Math.min(CompendiumLogic.MAX_LEVEL, level));
        this.xp = Math.max(0, xp);
    }
}
