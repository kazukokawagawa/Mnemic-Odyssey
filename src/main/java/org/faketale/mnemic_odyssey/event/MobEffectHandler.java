package org.faketale.mnemic_odyssey.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.capability.ModAttachments;
import org.faketale.mnemic_odyssey.effect.ModEffects;

@EventBusSubscriber(modid = MnemicOdyssey.MODID/*, bus = EventBusSubscriber.Bus.GAME*/)
public class MobEffectHandler {
    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        //检查目标是否是玩家
        if (event.getEntity() instanceof Player player) {
            //技能期间免疫“记忆混乱”效果
            if (player.getData(ModAttachments.CHAOS_IMMUNITY) > 0 && event.getEffectInstance().is(ModEffects.MEMORY_CHAOS)) {
                //设置结果为 NO_APPLE (不适用)
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        }
    }
}
