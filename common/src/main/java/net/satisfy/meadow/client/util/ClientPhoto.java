package net.satisfy.meadow.client.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public final class ClientPhoto {

    public static void capture(BlockPos pos, Direction facing) {
        Minecraft mc = Minecraft.getInstance();
        RenderTarget target = mc.getMainRenderTarget();
        File dir = new File(mc.gameDirectory, "screenshots");
        Screenshot.grab(dir, target, component -> {
            try {
                File src = new File(component.getString());
                if (!src.exists()) return;
                BufferedImage img = ImageIO.read(src);
                if (img == null) return;
                int w = img.getWidth();
                int h = img.getHeight();
                BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                double cx = w * 0.5;
                double cy = h * 0.5;
                double maxr = Math.hypot(cx, cy);
                Random rnd = new Random();
                for (int y = 0; y < h; y++) {
                    boolean scan = (y & 1) == 0;
                    for (int x = 0; x < w; x++) {
                        int argb = img.getRGB(x, y);
                        int a = (argb >> 24) & 255;
                        int r = (argb >> 16) & 255;
                        int g = (argb >> 8) & 255;
                        int b = argb & 255;
                        int lum = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                        int dr = (int)(lum * 0.6 + r * 0.4);
                        int dg = (int)(lum * 0.7 + g * 0.3);
                        int db = (int)(lum * 0.8 + b * 0.2);
                        int sr = (int)Math.min(255, 0.472 * dr + 0.842 * dg + 0.189 * db + 18);
                        int sg = (int)Math.min(255, 0.349 * dr + 0.686 * dg + 0.168 * db + 6);
                        int sb = (int)Math.min(255, 0.272 * dr + 0.534 * dg + 0.131 * db - 8);
                        sr = (int)(sr * 0.92 + 12);
                        sg = (int)(sg * 0.90 + 10);
                        sb = (int)(sb * 0.88 + 8);
                        double dx = x - cx;
                        double dy = y - cy;
                        double dist = Math.hypot(dx, dy) / maxr;
                        double vig = 1.0 - Math.pow(dist, 1.6);
                        vig = Math.max(0.08, vig * 0.9);
                        int nr = clamp((int)(sr * vig));
                        int ng = clamp((int)(sg * (vig * 0.98)));
                        int nb = clamp((int)(sb * (vig * 0.94)));
                        int grain = rnd.nextInt(25) - 12;
                        nr = clamp(nr + grain);
                        ng = clamp(ng + grain);
                        nb = clamp(nb + grain);
                        if (scan) {
                            nr = clamp(nr - 8);
                            ng = clamp(ng - 8);
                            nb = clamp(nb - 8);
                        }
                        int outArgb = (a << 24) | (nr << 16) | (ng << 8) | nb;
                        out.setRGB(x, y, outArgb);
                    }
                }
                ImageIO.write(out, "PNG", src);
            } catch (Exception ignored) {}
        });
    }

    private static int clamp(int v) {
        if (v < 0) return 0;
        if (v > 255) return 255;
        return v;
    }

    private ClientPhoto() {}
}
