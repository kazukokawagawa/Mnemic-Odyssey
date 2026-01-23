package org.faketale.mnemic_odyssey.effect;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.faketale.mnemic_odyssey.MnemicOdyssey;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MnemicOdyssey.MODID);

    //记忆混乱
    public static final DeferredHolder<MobEffect, MemoryChaos> MEMORY_CHAOS = EFFECTS.register("memory_chaos", MemoryChaos::new);
}
