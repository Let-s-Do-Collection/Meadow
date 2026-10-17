package net.satisfy.meadow.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.satisfy.meadow.core.block.DoormatBlock;
import net.satisfy.meadow.core.block.entity.DoormatBlockEntity;

public class DoormatRenderer implements BlockEntityRenderer<DoormatBlockEntity> {
    private static final float MAX_WIDTH = 13.0F / 16.0F;
    private static final float BASE_SCALE = 1.0F / 80.0F;

    private final Font font;

    public DoormatRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(DoormatBlockEntity doormat, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        if (doormat.isEmpty()) {
            return;
        }
        Direction facing = doormat.getBlockState().getValue(DoormatBlock.FACING);
        int width = 0;
        for (int i = 0; i < DoormatBlockEntity.LINES; i++) {
            width = Math.max(width, font.width(doormat.getLine(i)));
        }
        float scale = Math.min(BASE_SCALE, MAX_WIDTH / width);
        int color = doormat.getColor().getTextColor();
        int outline = doormat.getColor() == DyeColor.BLACK ? 0xFFF0EBCC : darken(color);
        float lineSpacing = font.lineHeight + 1;
        float top = -lineSpacing * DoormatBlockEntity.LINES / 2.0F + 1;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.0 / 16.0 + 0.002, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.scale(scale, -scale, scale);
        for (int i = 0; i < DoormatBlockEntity.LINES; i++) {
            String text = doormat.getLine(i);
            if (text.isEmpty()) {
                continue;
            }
            float x = -font.width(text) / 2.0F;
            float y = top + i * lineSpacing;
            if (doormat.isGlowing()) {
                font.drawInBatch8xOutline(Component.literal(text).getVisualOrderText(), x, y, color, outline, poseStack.last().pose(), buffer, LightTexture.FULL_BRIGHT);
            } else {
                font.drawInBatch(text, x, y, color, false, poseStack.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, light);
            }
        }
        poseStack.popPose();
    }

    private static int darken(int color) {
        int r = (int) (((color >> 16) & 0xFF) * 0.4);
        int g = (int) (((color >> 8) & 0xFF) * 0.4);
        int b = (int) ((color & 0xFF) * 0.4);
        return 0xFF000000 | r << 16 | g << 8 | b;
    }
}
