package org.faketale.mnemic_odyssey.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.capability.ModAttachments;
import org.faketale.mnemic_odyssey.item.ModItems;
import org.faketale.mnemic_odyssey.network.MemoryTorrentData;
import org.faketale.mnemic_odyssey.network.packet.EnergySyncPayload;

@EventBusSubscriber(modid = MnemicOdyssey.MODID/*, bus = EventBusSubscriber.Bus.GAME*/)
public class PlayerLoggedHandler {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        //给予初始物品
        if (!player.level().isClientSide) {
            //检查玩家是否已经获得过初始物品
            if (!player.getData(ModAttachments.HAS_JOINED_WORLD)) {

                //给予物品
                ItemStack guideBook = new ItemStack(ModItems.MemoryCodex.get());
                //这里可以添加自定义 NBT 或组件

                if (!player.getInventory().add(guideBook)) {
                    //背包满了就丢在地上
                    player.drop(guideBook, false);
                }

                //更新标记
                player.setData(ModAttachments.HAS_JOINED_WORLD, true);

                //发送欢迎消息
                player.sendSystemMessage(Component.translatable("message.mnemic_odyssey.welcome"));
            }
        }

        //进入存档时向客户端同步忆如泉涌条
        if (player instanceof ServerPlayer serverPlayer) {
            // 从存档加载数据
            MemoryTorrentData savedData = serverPlayer.getData(ModAttachments.MEMORY_TORRENT.get());
            // 同步给客户端
            PacketDistributor.sendToPlayer(serverPlayer, new EnergySyncPayload(savedData));
        }
    }
}
