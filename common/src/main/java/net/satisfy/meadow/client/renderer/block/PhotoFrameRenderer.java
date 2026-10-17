package net.satisfy.meadow.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.satisfy.meadow.core.block.PhotoFrameBlock;
import net.satisfy.meadow.core.block.entity.PhotoFrameBlockEntity;

public class PhotoFrameRenderer implements BlockEntityRenderer<PhotoFrameBlockEntity> {
    private static final float PICTURE_SIZE = 10.0F / 16.0F;
    private static final float PICTURE_DEPTH = 15.45F / 16.0F;

    public PhotoFrameRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(PhotoFrameBlockEntity frame, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        if (!(frame.getLevel() instanceof ClientLevel level)) {
            return;
        }
        MapId id = frame.getPhoto().get(DataComponents.MAP_ID);
        if (id == null) {
            return;
        }
        MapItemSavedData data = MapItem.getSavedData(id, level);
        if (data == null) {
            byte[] colors = frame.getColors();
            if (colors == null || colors.length != 128 * 128) {
                return;
            }
            data = MapItemSavedData.createForClient((byte) 0, true, level.dimension());
            System.arraycopy(colors, 0, data.colors, 0, colors.length);
            level.overrideMapData(id, data);
        }
        Direction facing = frame.getBlockState().getValue(PhotoFrameBlock.FACING);
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
        poseStack.translate(0.0F, 0.0F, PICTURE_DEPTH - 0.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        float scale = PICTURE_SIZE / 128.0F;
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-64.0F, -64.0F, 0.0F);
        Minecraft.getInstance().gameRenderer.getMapRenderer().render(poseStack, buffers, id, data, true, light);
        poseStack.popPose();
    }
}
