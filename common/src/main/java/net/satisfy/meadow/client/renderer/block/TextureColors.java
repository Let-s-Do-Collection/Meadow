package net.satisfy.meadow.client.renderer.block;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class TextureColors {
    public static final int DEFAULT_COLOR = 0xFFF2C14E;
    private static final Map<Item, Integer> COLORS = new ConcurrentHashMap<>();

    private TextureColors() {
    }

    public static int of(ItemStack stack) {
        return stack.isEmpty() ? DEFAULT_COLOR : COLORS.computeIfAbsent(stack.getItem(), item -> sample(new ItemStack(item)));
    }

    public static int blend(List<ItemStack> portions, ItemStack melting, float meltWeight) {
        float red = 0;
        float green = 0;
        float blue = 0;
        float total = 0;
        for (ItemStack portion : portions) {
            int color = of(portion);
            red += (color >> 16 & 0xFF);
            green += (color >> 8 & 0xFF);
            blue += (color & 0xFF);
            total++;
        }
        if (!melting.isEmpty() && meltWeight > 0) {
            int color = of(melting);
            red += (color >> 16 & 0xFF) * meltWeight;
            green += (color >> 8 & 0xFF) * meltWeight;
            blue += (color & 0xFF) * meltWeight;
            total += meltWeight;
        }
        if (total <= 0) {
            return of(melting);
        }
        return 0xFF000000 | Math.round(red / total) << 16 | Math.round(green / total) << 8 | Math.round(blue / total);
    }

    public static int mix(int base, List<ItemStack> stacks, float weight) {
        if (stacks.isEmpty() || weight <= 0) {
            return base;
        }
        float red = 0;
        float green = 0;
        float blue = 0;
        for (ItemStack stack : stacks) {
            int color = of(stack);
            red += (color >> 16 & 0xFF);
            green += (color >> 8 & 0xFF);
            blue += (color & 0xFF);
        }
        int count = stacks.size();
        float keep = 1.0F - weight;
        int r = Math.round((base >> 16 & 0xFF) * keep + red / count * weight);
        int g = Math.round((base >> 8 & 0xFF) * keep + green / count * weight);
        int b = Math.round((base & 0xFF) * keep + blue / count * weight);
        return 0xFF000000 | r << 16 | g << 8 | b;
    }

    private static int sample(ItemStack stack) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 0).getParticleIcon();
        ResourceLocation name = sprite.contents().name();
        ResourceLocation file = ResourceLocation.fromNamespaceAndPath(name.getNamespace(), "textures/" + name.getPath() + ".png");
        Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(file);
        if (resource.isEmpty()) {
            return DEFAULT_COLOR;
        }
        try (InputStream stream = resource.get().open(); NativeImage image = NativeImage.read(stream)) {
            return average(image);
        } catch (IOException exception) {
            return DEFAULT_COLOR;
        }
    }

    private static int average(NativeImage image) {
        long red = 0;
        long green = 0;
        long blue = 0;
        int count = 0;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int abgr = image.getPixelRGBA(x, y);
                if ((abgr >>> 24) < 128) {
                    continue;
                }
                red += abgr & 0xFF;
                green += (abgr >> 8) & 0xFF;
                blue += (abgr >> 16) & 0xFF;
                count++;
            }
        }
        if (count == 0) {
            return DEFAULT_COLOR;
        }
        return 0xFF000000 | (int) (red / count) << 16 | (int) (green / count) << 8 | (int) (blue / count);
    }
}
