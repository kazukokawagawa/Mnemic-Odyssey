package org.faketale.mnemic_odyssey.event;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.ability.MemoryTorrent;

@EventBusSubscriber(modid = MnemicOdyssey.MODID)
public class LivingDamageHandler {
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player && event.getEntity() != player) {
            MemoryTorrent.charge(player, event.getNewDamage());
        }
    }
}
