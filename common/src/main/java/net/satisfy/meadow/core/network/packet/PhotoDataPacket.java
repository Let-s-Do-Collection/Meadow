package net.satisfy.meadow.core.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.block.entity.CameraBlockEntity;
import org.jetbrains.annotations.NotNull;

public record PhotoDataPacket(BlockPos pos, byte[] colors) implements CustomPacketPayload {
    public static final Type<PhotoDataPacket> TYPE = new Type<>(Meadow.identifier("photo_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PhotoDataPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PhotoDataPacket::pos,
            ByteBufCodecs.byteArray(CameraBlockEntity.PHOTO_SIZE * CameraBlockEntity.PHOTO_SIZE), PhotoDataPacket::colors,
            PhotoDataPacket::new);

    public static void handle(PhotoDataPacket packet, ServerPlayer player) {
        if (!player.level().isLoaded(packet.pos()) || player.distanceToSqr(packet.pos().getCenter()) > 48.0 * 48.0) {
            return;
        }
        if (player.level().getBlockEntity(packet.pos()) instanceof CameraBlockEntity camera) {
            camera.acceptPhoto(player, packet.colors());
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
