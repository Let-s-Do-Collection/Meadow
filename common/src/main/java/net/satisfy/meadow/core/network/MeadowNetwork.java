package net.satisfy.meadow.core.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.server.level.ServerPlayer;
import net.satisfy.meadow.core.network.packet.PhotoDataPacket;
import net.satisfy.meadow.core.network.packet.PhotoRequestPacket;
import net.satisfy.meadow.core.network.packet.SetDoormatTextPacket;

public class MeadowNetwork {
    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.c2s(), SetDoormatTextPacket.TYPE, SetDoormatTextPacket.STREAM_CODEC, (packet, context) ->
                context.queue(() -> SetDoormatTextPacket.handle(packet, (ServerPlayer) context.getPlayer())));
        NetworkManager.registerReceiver(NetworkManager.c2s(), PhotoDataPacket.TYPE, PhotoDataPacket.STREAM_CODEC, (packet, context) ->
                context.queue(() -> PhotoDataPacket.handle(packet, (ServerPlayer) context.getPlayer())));
        if (Platform.getEnvironment() == Env.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.s2c(), PhotoRequestPacket.TYPE, PhotoRequestPacket.STREAM_CODEC, (packet, context) ->
                    context.queue(() -> PhotoRequestPacket.handle(packet)));
        } else {
            NetworkManager.registerS2CPayloadType(PhotoRequestPacket.TYPE, PhotoRequestPacket.STREAM_CODEC);
        }
    }
}
