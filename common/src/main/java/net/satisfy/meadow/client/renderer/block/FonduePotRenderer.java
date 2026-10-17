package net.satisfy.meadow.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.block.FonduePotBlock;
import net.satisfy.meadow.core.block.entity.FonduePotBlockEntity;

import java.util.Map;
import java.util.WeakHashMap;
import net.satisfy.meadow.platform.PlatformHelper;

public class FonduePotRenderer implements BlockEntityRenderer<FonduePotBlockEntity> {
    private static final float INNER_MIN = 4.0F / 16.0F;
    private static final float INNER_MAX = 12.0F / 16.0F;
    private static final float STIR_SPEED = 0.35F;
    private static final float BUMP_TICKS = 10.0F;
    private static final float BUMP_LIFT = 0.015F;
    private static final float STIR_SWAY = 2.0F;
    private static final Map<FonduePotBlockEntity, Animation> ANIMATIONS = new WeakHashMap<>();

    public FonduePotRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FonduePotBlockEntity pot, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        Level level = pot.getLevel();
        if (level == null) {
            return;
        }
        Animation animation = ANIMATIONS.computeIfAbsent(pot, key -> new Animation());
        float time = level.getGameTime() + partialTick;
        float delta = animation.lastTime < 0 ? 0 : Mth.clamp(time - animation.lastTime, 0, 5);
        animation.lastTime = time;
        boolean animated = PlatformHelper.animationsEnabled();

        float progress = pot.meltProgress(partialTick);
        float targetFill = pot.getPortions().size() + progress * pot.meltingPortions();
        animation.fill = animation.fill < 0 || !animated ? targetFill : Mth.lerp(Math.min(1.0F, delta * 0.25F), animation.fill, targetFill);
        boolean stirring = pot.isStirring();
        animation.stirBlend = !animated ? 0.0F : Mth.clamp(animation.stirBlend + (pot.isBeingStirred() ? 0.15F : -0.1F) * delta, 0, 1);
        animation.stirAngle += delta * STIR_SPEED * animation.stirBlend;

        poseStack.pushPose();
        move(poseStack, animated ? time - pot.getLastShakeTime() : -1.0F, animation);
        renderPot(poseStack, buffers, light, overlay, pot);

        if (animation.fill > 0.01F) {
            int color = TextureColors.blend(pot.getPortions(), pot.getMelting(), progress * pot.meltingPortions());
            float wobble = animated && stirring ? Mth.sin(time * 0.6F) * 0.15F : 0.0F;
            renderSurface(poseStack, buffers, light, (FonduePotBlock.surfaceHeight(animation.fill) + wobble) / 16.0F, color);
        }

        ItemStack melting = pot.getMelting();
        if (!melting.isEmpty()) {
            renderMelting(poseStack, buffers, light, level, pot, melting, progress, animation, time);
        }

        renderStick(poseStack, buffers, light, animation);
        poseStack.popPose();
    }

    private static void move(PoseStack poseStack, float sinceBump, Animation animation) {
        poseStack.translate(0.5F, 0.0F, 0.5F);
        if (sinceBump >= 0.0F && sinceBump < BUMP_TICKS) {
            poseStack.translate(0.0F, Mth.sin(Mth.PI * sinceBump / BUMP_TICKS) * BUMP_LIFT, 0.0F);
        }
        float sway = animation.stirBlend * STIR_SWAY;
        if (sway > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.cos(animation.stirAngle) * sway));
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(animation.stirAngle) * sway));
        }
        poseStack.translate(-0.5F, 0.0F, -0.5F);
    }

    private static void renderPot(PoseStack poseStack, MultiBufferSource buffers, int light, int overlay, FonduePotBlockEntity pot) {
        BlockState state = pot.getBlockState();
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        dispatcher.getModelRenderer().renderModel(poseStack.last(), buffers.getBuffer(Sheets.cutoutBlockSheet()), state, dispatcher.getBlockModel(state), 1.0F, 1.0F, 1.0F, light, overlay);
    }

    private static void renderSurface(PoseStack poseStack, MultiBufferSource buffers, int light, float height, int color) {
        TextureAtlasSprite sprite = sprite("block/fondue_pot_cheese");
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
        PoseStack.Pose pose = poseStack.last();
        float u0 = sprite.getU(INNER_MIN);
        float u1 = sprite.getU(INNER_MAX);
        float v0 = sprite.getV(INNER_MIN);
        float v1 = sprite.getV(INNER_MAX);
        vertex(consumer, pose, INNER_MIN, height, INNER_MIN, u0, v0, color, light, 0, 1, 0);
        vertex(consumer, pose, INNER_MIN, height, INNER_MAX, u0, v1, color, light, 0, 1, 0);
        vertex(consumer, pose, INNER_MAX, height, INNER_MAX, u1, v1, color, light, 0, 1, 0);
        vertex(consumer, pose, INNER_MAX, height, INNER_MIN, u1, v0, color, light, 0, 1, 0);
    }

    private static void renderMelting(PoseStack poseStack, MultiBufferSource buffers, int light, Level level, FonduePotBlockEntity pot, ItemStack melting, float progress, Animation animation, float time) {
        float surface = FonduePotBlock.surfaceHeight(animation.fill) / 16.0F;
        float bob = PlatformHelper.animationsEnabled() ? Mth.sin(time * 0.2F) * 0.008F : 0.0F;
        poseStack.pushPose();
        float size = (FonduePotBlockEntity.isWheel(melting) ? 0.5F : 0.35F) * (1.0F - 0.85F * progress);
        poseStack.translate(0.5F, surface + 0.01F + bob, 0.5F);
        poseStack.mulPose(Axis.YP.rotation(animation.stirAngle * 0.5F));
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.scale(size, size, size);
        Minecraft.getInstance().getItemRenderer().renderStatic(melting, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, buffers, level, (int) pot.getBlockPos().asLong());
        poseStack.popPose();
    }

    private static void renderStick(PoseStack poseStack, MultiBufferSource buffers, int light, Animation animation) {
        TextureAtlasSprite sprite = sprite("block/cheese_rack_side");
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotation(animation.stirAngle));
        poseStack.translate(-0.5F, 0.0F, -0.5F);
        poseStack.translate(10.0F / 16.0F, 2.0F / 16.0F, 8.0F / 16.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(45 - 12 * animation.stirBlend));
        box(consumer, poseStack.last(), sprite, light, 0, 0, 0, 2.0F / 16.0F, 12.0F / 16.0F, 2.0F / 16.0F);
        poseStack.popPose();
    }

    private static void box(VertexConsumer consumer, PoseStack.Pose pose, TextureAtlasSprite sprite, int light, float x0, float y0, float z0, float x1, float y1, float z1) {
        float su0 = sprite.getU(2.0F / 16.0F);
        float su1 = sprite.getU(4.0F / 16.0F);
        float sv0 = sprite.getV(0.0F);
        float sv1 = sprite.getV(12.0F / 16.0F);
        float eu0 = sprite.getU(8.0F / 16.0F);
        float eu1 = sprite.getU(10.0F / 16.0F);
        float ev0 = sprite.getV(4.0F / 16.0F);
        float ev1 = sprite.getV(6.0F / 16.0F);
        int white = 0xFFFFFFFF;
        vertex(consumer, pose, x0, y1, z0, su0, sv0, white, light, 0, 0, -1);
        vertex(consumer, pose, x1, y1, z0, su1, sv0, white, light, 0, 0, -1);
        vertex(consumer, pose, x1, y0, z0, su1, sv1, white, light, 0, 0, -1);
        vertex(consumer, pose, x0, y0, z0, su0, sv1, white, light, 0, 0, -1);
        vertex(consumer, pose, x1, y1, z1, su0, sv0, white, light, 0, 0, 1);
        vertex(consumer, pose, x0, y1, z1, su1, sv0, white, light, 0, 0, 1);
        vertex(consumer, pose, x0, y0, z1, su1, sv1, white, light, 0, 0, 1);
        vertex(consumer, pose, x1, y0, z1, su0, sv1, white, light, 0, 0, 1);
        vertex(consumer, pose, x0, y1, z1, su0, sv0, white, light, -1, 0, 0);
        vertex(consumer, pose, x0, y1, z0, su1, sv0, white, light, -1, 0, 0);
        vertex(consumer, pose, x0, y0, z0, su1, sv1, white, light, -1, 0, 0);
        vertex(consumer, pose, x0, y0, z1, su0, sv1, white, light, -1, 0, 0);
        vertex(consumer, pose, x1, y1, z0, su0, sv0, white, light, 1, 0, 0);
        vertex(consumer, pose, x1, y1, z1, su1, sv0, white, light, 1, 0, 0);
        vertex(consumer, pose, x1, y0, z1, su1, sv1, white, light, 1, 0, 0);
        vertex(consumer, pose, x1, y0, z0, su0, sv1, white, light, 1, 0, 0);
        vertex(consumer, pose, x0, y1, z1, eu0, ev1, white, light, 0, 1, 0);
        vertex(consumer, pose, x1, y1, z1, eu1, ev1, white, light, 0, 1, 0);
        vertex(consumer, pose, x1, y1, z0, eu1, ev0, white, light, 0, 1, 0);
        vertex(consumer, pose, x0, y1, z0, eu0, ev0, white, light, 0, 1, 0);
        vertex(consumer, pose, x0, y0, z0, eu0, ev0, white, light, 0, -1, 0);
        vertex(consumer, pose, x1, y0, z0, eu1, ev0, white, light, 0, -1, 0);
        vertex(consumer, pose, x1, y0, z1, eu1, ev1, white, light, 0, -1, 0);
        vertex(consumer, pose, x0, y0, z1, eu0, ev1, white, light, 0, -1, 0);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int color, int light, float nx, float ny, float nz) {
        consumer.addVertex(pose, x, y, z).setColor(color).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, nx, ny, nz);
    }

    private static TextureAtlasSprite sprite(String path) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(Meadow.identifier(path));
    }

    private static final class Animation {
        private float lastTime = -1;
        private float fill = -1;
        private float stirAngle;
        private float stirBlend;
    }
}
