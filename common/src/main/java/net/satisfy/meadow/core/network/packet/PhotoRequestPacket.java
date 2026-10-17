package net.satisfy.meadow.core.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.photo.PhotoCapture;
import org.jetbrains.annotations.NotNull;

public record PhotoRequestPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<PhotoRequestPacket> TYPE = new Type<>(Meadow.identifier("photo_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PhotoRequestPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PhotoRequestPacket::pos,
            PhotoRequestPacket::new);

    public static void handle(PhotoRequestPacket packet) {
        PhotoCapture.request(packet.pos());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
