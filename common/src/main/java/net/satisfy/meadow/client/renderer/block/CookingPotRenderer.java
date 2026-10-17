package net.satisfy.meadow.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.satisfy.meadow.client.ExtraModels;
import net.satisfy.meadow.core.block.CookingFrameBlock;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.satisfy.meadow.core.block.entity.CookingCauldronBlockEntity;
import net.satisfy.meadow.core.registry.ObjectRegistry;

import java.util.Map;
import java.util.WeakHashMap;
import net.satisfy.meadow.platform.PlatformHelper;

public class CookingPotRenderer implements BlockEntityRenderer<CookingCauldronBlockEntity> {
    private static final int FOAM_COLOR = 0xE6F4F1E8;
    private static final float PIVOT_Y = 30.0F / 16.0F;
    private static final Map<CookingCauldronBlockEntity, Animation> ANIMATIONS = new WeakHashMap<>();

    public CookingPotRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static int color(CookingCauldronBlockEntity pot) {
        Level level = pot.getLevel();
        int base = pot.isLiquidBiomeTint() && level != null ? 0xFF000000 | BiomeColors.getAverageWaterColor(level, pot.getBlockPos()) : pot.getLiquidColor();
        return TextureColors.mix(base, pot.getIngredients(), pot.isCooking() ? 0.15F + 0.45F * pot.cookProgress() : 0.0F);
    }

    @Override
    public void render(CookingCauldronBlockEntity pot, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        Level level = pot.getLevel();
        if (level == null) {
            return;
        }
        boolean frame = pot.getBlockState().is(ObjectRegistry.COOKING_FRAME.get());
        float inner = frame ? 5.0F / 16.0F : 4.0F / 16.0F;
        float outer = 1.0F - inner;
        Animation animation = ANIMATIONS.computeIfAbsent(pot, key -> new Animation());
        float time = level.getGameTime() + partialTick;
        float delta = animation.lastTime < 0 ? 0 : Mth.clamp(time - animation.lastTime, 0, 5);
        animation.lastTime = time;
        boolean animated = PlatformHelper.animationsEnabled();

        float targetSurface = (float) pot.surfaceY();
        animation.surface = animation.surface < 0 || !animated ? targetSurface : Mth.lerp(Math.min(1.0F, delta * 0.2F), animation.surface, targetSurface);
        boolean hasLiquid = pot.getLiquid() != null || animation.surface > targetSurface + 0.002F;
        animation.foam = !animated ? (pot.hasFoam() ? 1.0F : 0.0F) : Mth.clamp(animation.foam + (pot.hasFoam() ? 0.08F : -0.2F) * delta, 0, 1);
        if (animated && pot.isCooking()) {
            animation.swirl += delta * 0.03F;
        }

        float sinceSkim = time - pot.getLastSkim();
        float ripple = animated && sinceSkim >= 0 && sinceSkim < 12 ? Mth.sin(sinceSkim * 1.2F) * (1 - sinceSkim / 12) * 0.01F : 0.0F;
        float boil = animated && pot.isCooking() ? Mth.sin(time * 0.5F) * 0.004F : 0.0F;
        float surface = animation.surface + ripple + boil;

        poseStack.pushPose();
        if (frame) {
            float since = time - pot.getLastSkim();
            float amplitude = pot.isHeated() ? (pot.isCooking() ? 2.2F : 1.2F) : 0.6F;
            float swing = animated ? Mth.sin(time * 0.09F) * amplitude : 0.0F;
            if (animated && since >= 0 && since < 40) {
                swing += Mth.sin(since * 0.35F) * 6.0F * (1 - since / 40);
            }
            Direction facing = pot.getBlockState().getValue(CookingFrameBlock.FACING);
            poseStack.translate(0.5F, PIVOT_Y, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-facingRotation(facing)));
            poseStack.mulPose(Axis.XP.rotationDegrees(swing));
            poseStack.mulPose(Axis.YP.rotationDegrees(facingRotation(facing)));
            poseStack.translate(-0.5F, -PIVOT_Y, -0.5F);
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.0F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-facingRotation(facing)));
            poseStack.translate(-0.5F, 0.0F, -0.5F);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), buffers.getBuffer(RenderType.cutout()), pot.getBlockState(), ExtraModels.get(ExtraModels.COOKING_FRAME_POT), 1.0F, 1.0F, 1.0F, light, overlay);
            poseStack.popPose();
        }

        if (hasLiquid) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(pot.getLiquidTexture());
            quad(buffers.getBuffer(RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS)), poseStack.last(), sprite, inner, outer, surface, color(pot) & 0x00FFFFFF | 0xE0000000, light);
            if (animation.foam > 0.01F) {
                float inset = inner + (1 - animation.foam) * 0.12F;
                int alpha = (int) (animation.foam * ((FOAM_COLOR >>> 24) & 0xFF)) << 24;
                quad(buffers.getBuffer(RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS)), poseStack.last(), sprite, inset, 1.0F - inset, surface + 0.004F, FOAM_COLOR & 0x00FFFFFF | alpha, light);
            }
        }

        NonNullList<ItemStack> ingredients = pot.getIngredients();
        float itemY = hasLiquid ? surface : (float) pot.surfaceY() + 0.01F;
        float radius = (outer - inner) * 0.25F;
        for (int i = 0; i < ingredients.size(); i++) {
            float angle = animation.swirl + i * Mth.TWO_PI / Math.max(1, ingredients.size());
            float bob = animated && hasLiquid ? Mth.sin(time * 0.15F + i * 1.7F) * 0.006F : 0.0F;
            float x = ingredients.size() == 1 ? 0.5F : 0.5F + Mth.cos(angle) * radius;
            float z = ingredients.size() == 1 ? 0.5F : 0.5F + Mth.sin(angle) * radius;
            renderItem(poseStack, buffers, light, level, pot, ingredients.get(i), x, itemY + bob, z, angle + i, frame ? 0.16F : 0.24F, i);
        }

        ItemStack result = pot.getResult();
        if (!result.isEmpty()) {
            float bob = animated && hasLiquid ? Mth.sin(time * 0.1F) * 0.008F : 0.0F;
            renderItem(poseStack, buffers, light, level, pot, result, 0.5F, itemY + 0.02F + bob, 0.5F, animated ? time * 0.01F : 0.0F, frame ? 0.2F : 0.3F, 7);
        }
        poseStack.popPose();
    }

    private static float facingRotation(Direction facing) {
        return switch (facing) {
            case EAST -> 90.0F;
            case SOUTH -> 180.0F;
            case WEST -> 270.0F;
            default -> 0.0F;
        };
    }

    private static void renderItem(PoseStack poseStack, MultiBufferSource buffers, int light, Level level, CookingCauldronBlockEntity pot, ItemStack stack, float x, float y, float z, float yaw, float scale, int seed) {
        poseStack.pushPose();
        poseStack.translate(x, y + 0.01F, z);
        poseStack.mulPose(Axis.YP.rotation(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.scale(scale, scale, scale);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, buffers, level, (int) pot.getBlockPos().asLong() + seed);
        poseStack.popPose();
    }

    private static void quad(VertexConsumer consumer, PoseStack.Pose pose, TextureAtlasSprite sprite, float min, float max, float y, int color, int light) {
        float u0 = sprite.getU(min);
        float u1 = sprite.getU(max);
        float v0 = sprite.getV(min);
        float v1 = sprite.getV(max);
        vertex(consumer, pose, min, y, min, u0, v0, color, light);
        vertex(consumer, pose, min, y, max, u0, v1, color, light);
        vertex(consumer, pose, max, y, max, u1, v1, color, light);
        vertex(consumer, pose, max, y, min, u1, v0, color, light);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int color, int light) {
        consumer.addVertex(pose, x, y, z).setColor(color).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 1, 0);
    }

    private static final class Animation {
        private float lastTime = -1;
        private float surface = -1;
        private float foam;
        private float swirl;
    }
}
