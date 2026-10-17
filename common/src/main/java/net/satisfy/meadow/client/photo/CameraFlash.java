package net.satisfy.meadow.client.photo;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.satisfy.meadow.platform.PlatformHelper;

public final class CameraFlash {
    private static final float FADE_TICKS = 30.0F;
    private static final double REACH = 1.5D;
    private static long flashedAt = Long.MIN_VALUE / 2;
    private static float strength;

    private CameraFlash() {
    }

    public static void flash(float amount) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        flashedAt = minecraft.level.getGameTime();
        strength = Mth.clamp(amount, 0.0F, 1.0F);
    }

    public static void onFlash(Level level, Vec3 lens, Direction facing) {
        Player player = Minecraft.getInstance().player;
        if (player == null || player.level() != level) {
            return;
        }
        double range = PlatformHelper.getCameraFlashRange() * REACH;
        Vec3 eye = player.getEyePosition();
        Vec3 offset = eye.subtract(lens);
        double distance = offset.length();
        if (distance > range || distance < 1.0E-3) {
            return;
        }
        Vec3 direction = offset.scale(1.0 / distance);
        if (direction.dot(new Vec3(facing.getStepX(), 0.0, facing.getStepZ())) < 0.3) {
            return;
        }
        double looking = -player.getViewVector(1.0F).dot(direction);
        if (looking < 0.5 || level.clip(new ClipContext(lens, eye, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getType() != HitResult.Type.MISS) {
            return;
        }
        flash((float) (0.35 + 0.65 * looking * (1.0 - distance / range)));
    }

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        float alpha;
        if (PhotoCapture.isCapturing()) {
            alpha = 1.0F;
        } else {
            float age = minecraft.level.getGameTime() - flashedAt + deltaTracker.getGameTimeDeltaPartialTick(false);
            if (age < 0.0F || age > FADE_TICKS) {
                return;
            }
            float fade = 1.0F - age / FADE_TICKS;
            alpha = strength * fade * fade;
        }
        int color = Mth.clamp(Math.round(alpha * 255.0F), 0, 255) << 24 | 0xFFFDF4;
        graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), color);
    }
}
