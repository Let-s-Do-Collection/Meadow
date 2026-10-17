package net.satisfy.meadow.client.photo;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.MapColor;
import net.satisfy.meadow.core.block.entity.CameraBlockEntity;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public final class PhotoDeveloper {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int SIZE = CameraBlockEntity.PHOTO_SIZE;
    private static final int BORDER = 5;
    private static final int SAMPLES = 4;
    private static final float[] PAPER = {236.0F, 226.0F, 204.0F};
    private static final List<float[]> PALETTE = new ArrayList<>();
    private static final List<Byte> PALETTE_IDS = new ArrayList<>();

    private PhotoDeveloper() {
    }

    public static byte[] develop(NativeImage shot) {
        palette();
        int inner = SIZE - BORDER * 2;
        int side = Math.min(shot.getWidth(), shot.getHeight());
        int left = (shot.getWidth() - side) / 2;
        int top = (shot.getHeight() - side) / 2;
        Random grain = new Random();
        float[] pixels = new float[inner * inner * 3];
        for (int y = 0; y < inner; y++) {
            for (int x = 0; x < inner; x++) {
                float red = 0;
                float green = 0;
                float blue = 0;
                for (int sy = 0; sy < SAMPLES; sy++) {
                    for (int sx = 0; sx < SAMPLES; sx++) {
                        int px = left + (int) ((x + (sx + 0.5F) / SAMPLES) * side / inner);
                        int py = top + (int) ((y + (sy + 0.5F) / SAMPLES) * side / inner);
                        int abgr = shot.getPixelRGBA(Math.min(px, shot.getWidth() - 1), Math.min(py, shot.getHeight() - 1));
                        red += abgr & 0xFF;
                        green += abgr >> 8 & 0xFF;
                        blue += abgr >> 16 & 0xFF;
                    }
                }
                float count = SAMPLES * SAMPLES;
                float[] toned = tone(red / count, green / count, blue / count, (x + 0.5F) / inner, (y + 0.5F) / inner, grain);
                System.arraycopy(toned, 0, pixels, (x + y * inner) * 3, 3);
            }
        }

        byte[] colors = new byte[SIZE * SIZE];
        byte paper = nearest(PAPER[0], PAPER[1], PAPER[2]);
        Arrays.fill(colors, paper);
        for (int y = 0; y < inner; y++) {
            for (int x = 0; x < inner; x++) {
                int index = (x + y * inner) * 3;
                float red = pixels[index];
                float green = pixels[index + 1];
                float blue = pixels[index + 2];
                int match = nearestIndex(red, green, blue);
                float[] chosen = PALETTE.get(match);
                colors[(x + BORDER) + (y + BORDER) * SIZE] = PALETTE_IDS.get(match);
                spread(pixels, inner, x + 1, y, red - chosen[0], green - chosen[1], blue - chosen[2], 7.0F / 16.0F);
                spread(pixels, inner, x - 1, y + 1, red - chosen[0], green - chosen[1], blue - chosen[2], 3.0F / 16.0F);
                spread(pixels, inner, x, y + 1, red - chosen[0], green - chosen[1], blue - chosen[2], 5.0F / 16.0F);
                spread(pixels, inner, x + 1, y + 1, red - chosen[0], green - chosen[1], blue - chosen[2], 1.0F / 16.0F);
            }
        }
        return colors;
    }

    public static void save(NativeImage shot, File gameDirectory) {
        Util.ioPool().execute(() -> {
            try {
                Random grain = new Random();
                int width = shot.getWidth();
                int height = shot.getHeight();
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int abgr = shot.getPixelRGBA(x, y);
                        float[] toned = tone(abgr & 0xFF, abgr >> 8 & 0xFF, abgr >> 16 & 0xFF, (x + 0.5F) / width, (y + 0.5F) / height, grain);
                        shot.setPixelRGBA(x, y, 0xFF000000 | channel(toned[2]) << 16 | channel(toned[1]) << 8 | channel(toned[0]));
                    }
                }
                File file = file(new File(gameDirectory, "screenshots"));
                shot.writeToFile(file);
                Component link = Component.literal(file.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())));
                Minecraft.getInstance().execute(() -> Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("screenshot.success", link)));
            } catch (IOException exception) {
                LOGGER.warn("Couldn't save photo", exception);
            } finally {
                shot.close();
            }
        });
    }

    private static float[] tone(float red, float green, float blue, float u, float v, Random grain) {
        float luminance = (0.299F * red + 0.587F * green + 0.114F * blue) / 255.0F;
        luminance = Mth.clamp((luminance - 0.5F) * 1.15F + 0.5F, 0.0F, 1.0F);
        float du = u - 0.5F;
        float dv = v - 0.5F;
        float vignette = 1.0F - 0.55F * (du * du + dv * dv) * 2.0F;
        luminance = luminance * vignette + (grain.nextFloat() - 0.5F) * 0.06F;
        luminance = Mth.clamp(luminance, 0.0F, 1.0F);
        float fade = 0.1F;
        float r = Mth.lerp(fade, 255.0F * Math.min(1.0F, luminance * 1.08F + 0.04F), PAPER[0]);
        float g = Mth.lerp(fade, 255.0F * Math.min(1.0F, luminance * 0.94F + 0.02F), PAPER[1]);
        float b = Mth.lerp(fade, 255.0F * Math.max(0.0F, luminance * 0.74F - 0.01F), PAPER[2]);
        return new float[]{r, g, b};
    }

    private static int channel(float value) {
        return Mth.clamp(Math.round(value), 0, 255);
    }

    private static void spread(float[] pixels, int size, int x, int y, float red, float green, float blue, float weight) {
        if (x < 0 || y < 0 || x >= size || y >= size) {
            return;
        }
        int index = (x + y * size) * 3;
        pixels[index] += red * weight;
        pixels[index + 1] += green * weight;
        pixels[index + 2] += blue * weight;
    }

    private static byte nearest(float red, float green, float blue) {
        return PALETTE_IDS.get(nearestIndex(red, green, blue));
    }

    private static int nearestIndex(float red, float green, float blue) {
        int best = 0;
        float bestDistance = Float.MAX_VALUE;
        for (int i = 0; i < PALETTE.size(); i++) {
            float[] color = PALETTE.get(i);
            float dr = red - color[0];
            float dg = green - color[1];
            float db = blue - color[2];
            float distance = dr * dr * 0.3F + dg * dg * 0.59F + db * db * 0.11F;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = i;
            }
        }
        return best;
    }

    private static synchronized void palette() {
        if (!PALETTE.isEmpty()) {
            return;
        }
        for (int id = 1; id < 64; id++) {
            MapColor color = MapColor.byId(id);
            if (color == null || color == MapColor.NONE || color.id != id) {
                continue;
            }
            for (MapColor.Brightness brightness : MapColor.Brightness.values()) {
                int red = (color.col >> 16 & 0xFF) * brightness.modifier / 255;
                int green = (color.col >> 8 & 0xFF) * brightness.modifier / 255;
                int blue = (color.col & 0xFF) * brightness.modifier / 255;
                int spread = Math.max(red, Math.max(green, blue)) - Math.min(red, Math.min(green, blue));
                if (red + 6 >= green && green + 6 >= blue && spread <= 70) {
                    PALETTE.add(new float[]{red, green, blue});
                    PALETTE_IDS.add(color.getPackedId(brightness));
                }
            }
        }
    }

    private static File file(File directory) {
        directory.mkdirs();
        String name = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "_photo";
        File file = new File(directory, name + ".png");
        for (int i = 1; file.exists(); i++) {
            file = new File(directory, name + "_" + i + ".png");
        }
        return file;
    }
}
