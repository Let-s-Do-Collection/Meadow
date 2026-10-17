package net.satisfy.meadow.core.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringUtil;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.block.entity.DoormatBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SetDoormatTextPacket(BlockPos pos, List<String> lines) implements CustomPacketPayload {
    public static final Type<SetDoormatTextPacket> TYPE = new Type<>(Meadow.identifier("set_doormat_text"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetDoormatTextPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SetDoormatTextPacket::pos,
            ByteBufCodecs.stringUtf8(DoormatBlockEntity.MAX_LENGTH * 4).apply(ByteBufCodecs.list(DoormatBlockEntity.LINES)), SetDoormatTextPacket::lines,
            SetDoormatTextPacket::new);

    public static void handle(SetDoormatTextPacket packet, ServerPlayer player) {
        if (!player.level().isLoaded(packet.pos()) || player.distanceToSqr(packet.pos().getCenter()) > 64.0) {
            return;
        }
        if (player.level().getBlockEntity(packet.pos()) instanceof DoormatBlockEntity doormat) {
            doormat.setLines(packet.lines().stream().map(line -> StringUtil.filterText(line).strip()).toList());
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
