package net.satisfy.meadow.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.satisfy.foundation.render.DisplayItemRenderer;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.block.WoodcutterBlock;
import net.satisfy.meadow.core.block.entity.WoodcutterBlockEntity;
import net.satisfy.meadow.platform.PlatformHelper;

public class WoodcutterRenderer implements BlockEntityRenderer<WoodcutterBlockEntity> {
    private static final float AXE_BOTTOM = 14.0F / 16.0F;
    private static final float AXE_TOP = 30.0F / 16.0F;
    private static final float PIVOT_Y = 15.0F / 16.0F;
    private static final float PIVOT_Z = 14.0F / 16.0F;
    private static final float SWING_ANGLE = 55.0F;
    private static final float SWING_LIFT = 0.25F;
    private static final double ITEM_Y = 1.0;
    private static final float ITEM_SCALE = 0.5F;
    private static final float HOP_TICKS = 6.0F;
    private static final double HOP_HEIGHT = 0.08;

    public WoodcutterRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(WoodcutterBlockEntity cutter, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        Direction facing = cutter.getBlockState().getValue(WoodcutterBlock.FACING);
        float modelYaw = (facing.toYRot() + 180.0F) % 360.0F;
        float since = cutter.getLevel() == null || !PlatformHelper.animationsEnabled() ? Float.MAX_VALUE : cutter.getLevel().getGameTime() - cutter.getCutStart() + partialTick;

        if (!cutter.getInput().isEmpty()) {
            double hop = 0.0;
            float sinceImpact = since - WoodcutterBlockEntity.CHOP_TICKS / 2.0F;
            if (sinceImpact >= 0.0F && sinceImpact < HOP_TICKS) {
                float progress = sinceImpact / HOP_TICKS;
                hop = Math.abs(Mth.sin(progress * Mth.TWO_PI)) * (1.0F - progress) * HOP_HEIGHT;
            }
            DisplayItemRenderer.renderFlat(cutter.getInput(), poseStack, buffers, light, overlay, cutter.getLevel(),
                    0.5, ITEM_Y + hop, 0.5, -modelYaw + 90.0F, ITEM_SCALE, (int) cutter.getBlockPos().asLong());
        }

        float swing = 0.0F;
        if (since >= 0.0F && since < WoodcutterBlockEntity.CHOP_TICKS) {
            float progress = since / WoodcutterBlockEntity.CHOP_TICKS;
            swing = progress < 0.5F ? Mth.sin(progress * Mth.PI) : 1.0F - (progress - 0.5F) * 2.0F;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-modelYaw));
        poseStack.translate(-0.5, 0.0, -0.5);
        poseStack.translate(0.0, swing * SWING_LIFT, 0.0);
        poseStack.translate(0.5, PIVOT_Y, PIVOT_Z);
        poseStack.mulPose(Axis.XP.rotationDegrees(swing * SWING_ANGLE));
        poseStack.translate(-0.5, -PIVOT_Y, -PIVOT_Z);
        this.renderAxe(poseStack, buffers, light, overlay);
        poseStack.popPose();
    }

    private void renderAxe(PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(Meadow.identifier("item/woodcutter_axe"));
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS));
        PoseStack.Pose pose = poseStack.last();
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        this.vertex(consumer, pose, 0.0F, AXE_BOTTOM, u0, v1, light, overlay);
        this.vertex(consumer, pose, 1.0F, AXE_BOTTOM, u1, v1, light, overlay);
        this.vertex(consumer, pose, 1.0F, AXE_TOP, u1, v0, light, overlay);
        this.vertex(consumer, pose, 0.0F, AXE_TOP, u0, v0, light, overlay);
    }

    private void vertex(VertexConsumer consumer, PoseStack.Pose pose, float z, float y, float u, float v, int light, int overlay) {
        consumer.addVertex(pose, 0.5F, y, z).setColor(-1).setUv(u, v).setOverlay(overlay).setLight(light).setNormal(pose, -1.0F, 0.0F, 0.0F);
    }

    @Override
    public boolean shouldRenderOffScreen(WoodcutterBlockEntity cutter) {
        return true;
    }
}
