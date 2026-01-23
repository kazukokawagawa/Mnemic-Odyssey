package org.faketale.mnemic_odyssey.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.faketale.mnemic_odyssey.capability.ModAttachments;
import org.faketale.mnemic_odyssey.network.MemoryTorrentData;
import org.faketale.mnemic_odyssey.network.packet.EnergySyncPayload;

import java.util.Collection;
import java.util.Collections;

public class ModCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("memorytorrent")
                        .requires(source -> source.hasPermission(2)) // 权限等级2（管理员）
                        .then(Commands.literal("set")
                                .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 100f))
                                        // 场景1：仅设置自己 /memorytorrent set 50
                                        .executes(context -> setEnergy(
                                                context.getSource(),
                                                Collections.singleton(context.getSource().getPlayerOrException()),
                                                FloatArgumentType.getFloat(context, "value")
                                        ))
                                        // 场景2：设置指定目标 /memorytorrent set 50 @a
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .executes(context -> setEnergy(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        FloatArgumentType.getFloat(context, "value")
                                                ))
                                        )
                                )
                        )
        );
    }

    private static int setEnergy(CommandSourceStack source, Collection<ServerPlayer> targets, float value) {
        for (ServerPlayer player : targets) {
            // 1. 修改服务端数据
            MemoryTorrentData newData = new MemoryTorrentData(value, false);
            player.setData(ModAttachments.MEMORY_TORRENT.get(), newData);

            // 2. 同步到客户端
            // 在 1.21.1 中，直接使用 PacketDistributor 发送到指定玩家
            PacketDistributor.sendToPlayer(player, new EnergySyncPayload(newData));
        }

        // 反馈信息
        if (targets.size() == 1) {
            source.sendSuccess(() -> Component.translatable("commands.memorytorrent.set.success.single",
                    targets.iterator().next().getDisplayName(), value), true);
        } else {
            source.sendSuccess(() -> Component.translatable("commands.memorytorrent.set.success.multiple",
                    targets.size(), value), true);
        }

        return targets.size(); // 返回执行成功的条数
    }
}
