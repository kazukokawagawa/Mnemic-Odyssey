package org.faketale.mnemic_odyssey.gamerule;

import net.minecraft.world.level.GameRules;

public class ModGameRules {

    //死亡后是否保留忆如泉涌条
    public static GameRules.Key<GameRules.BooleanValue> RULE_KEEP_MEMORY_TORRENT;

    public static void register() {
        RULE_KEEP_MEMORY_TORRENT = GameRules.register("keepMemoryTorrent", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
    }

}
