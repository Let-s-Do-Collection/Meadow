package net.satisfy.meadow.client.photo;

import com.mojang.blaze3d.platform.NativeImage;
import dev.architectury.networking.NetworkManager;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.satisfy.meadow.core.block.CameraBlock;
import net.satisfy.meadow.core.network.packet.PhotoDataPacket;
import org.jetbrains.annotations.Nullable;

public final class PhotoCapture {
    private static final int WARMUP_FRAMES = 4;
    private static final long STALE_MILLIS = 2000L;
    private static final double EYE_OFFSET = 0.35D;

    @Nullable
    private static BlockPos pending;
    private static Vec3 eye = Vec3.ZERO;
    private static float yaw;
    private static int frames;
    private static long requestedAt;

    private PhotoCapture() {
    }

    public static void request(BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || (pending != null && Util.getMillis() - requestedAt < STALE_MILLIS)) {
            return;
        }
        BlockState state = minecraft.level.getBlockState(pos);
        if (!(state.getBlock() instanceof CameraBlock)) {
            return;
        }
        Direction facing = state.getValue(CameraBlock.FACING);
        eye = CameraBlock.lens(pos, facing).add(facing.getStepX() * EYE_OFFSET, 0.0D, facing.getStepZ() * EYE_OFFSET);
        yaw = facing.toYRot();
        frames = 0;
        requestedAt = Util.getMillis();
        pending = pos;
        minecraft.gameRenderer.setRenderHand(false);
        minecraft.gameRenderer.setRenderBlockOutline(false);
        CameraFlash.flash(1.0F);
    }

    public static boolean isCapturing() {
        return pending != null;
    }

    public static Vec3 eye() {
        return eye;
    }

    public static float yaw() {
        return yaw;
    }

    public static void afterLevel() {
        if (pending == null || ++frames < WARMUP_FRAMES) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        BlockPos pos = pending;
        pending = null;
        minecraft.gameRenderer.setRenderHand(true);
        minecraft.gameRenderer.setRenderBlockOutline(true);
        NativeImage shot = Screenshot.takeScreenshot(minecraft.getMainRenderTarget());
        NetworkManager.sendToServer(new PhotoDataPacket(pos, PhotoDeveloper.develop(shot)));
        PhotoDeveloper.save(shot, minecraft.gameDirectory);
    }
}
