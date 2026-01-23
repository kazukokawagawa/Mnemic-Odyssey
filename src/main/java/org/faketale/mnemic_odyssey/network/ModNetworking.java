package org.faketale.mnemic_odyssey.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.network.handler.ClientPayloadHandler;

import org.faketale.mnemic_odyssey.network.handler.ServerPayloadHandler;
import org.faketale.mnemic_odyssey.network.packet.EnergySyncPayload;
import org.faketale.mnemic_odyssey.network.packet.MemoryTorrentKeyPayload;

@EventBusSubscriber(modid = MnemicOdyssey.MODID/*, bus = EventBusSubscriber.Bus.MOD*/)
public class ModNetworking {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1"); // 协议版本

        //注册 S2C 同步包 (服务端 -> 客户端)
        registrar.playToClient(
                EnergySyncPayload.TYPE,
                EnergySyncPayload.STREAM_CODEC,
                ClientPayloadHandler::handleMemoryTorrentData
        );

        //注册 C2S 按键包 (客户端 -> 服务端)
        registrar.playToServer(
                MemoryTorrentKeyPayload.TYPE,
                MemoryTorrentKeyPayload.STREAM_CODEC,
                ServerPayloadHandler::handleMemoryTorrentKey
        );
    }
}
