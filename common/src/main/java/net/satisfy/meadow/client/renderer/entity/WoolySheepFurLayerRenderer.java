package net.satisfy.meadow.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.model.WoolySheepFurModel;
import net.satisfy.meadow.client.model.WoolySheepModel;
import net.satisfy.meadow.core.entity.WoolySheepEntity;

public class WoolySheepFurLayerRenderer extends RenderLayer<WoolySheepEntity, WoolySheepModel<WoolySheepEntity>> {
    private static final ResourceLocation FLECKED_FUR_LOCATION = Meadow.identifier("textures/entity/sheep/flecked_sheep_fur.png");
    private static final ResourceLocation PATCHED_FUR_LOCATION = Meadow.identifier("textures/entity/sheep/patched_sheep_fur.png");
    private static final ResourceLocation ROCKY_FUR_LOCATION = Meadow.identifier("textures/entity/sheep/rocky_sheep_fur.png");
    private static final ResourceLocation INKY_FUR_LOCATION = Meadow.identifier("textures/entity/sheep/inky_sheep_fur.png");
    private static final ResourceLocation FUZZY_FUR_LOCATION = Meadow.identifier("textures/entity/sheep/fuzzy_sheep_fur.png");
    private static final ResourceLocation LONG_NOSED_FUR_LOCATION = Meadow.identifier("textures/entity/sheep/long_nosed_sheep_fur.png");
    private final WoolySheepFurModel<WoolySheepEntity> sheepModel;

    public WoolySheepFurLayerRenderer(RenderLayerParent<WoolySheepEntity, WoolySheepModel<WoolySheepEntity>> parent, EntityRendererProvider.Context context) {
        super(parent);
        this.sheepModel = new WoolySheepFurModel<>(context.bakeLayer(ModelLayers.SHEEP_FUR));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, WoolySheepEntity sheep, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (sheep.isSheared() || sheep.isInvisible()) {
            return;
        }
        ResourceLocation furLocation = switch (sheep.getVariant()) {
            case PATCHED -> PATCHED_FUR_LOCATION;
            case ROCKY -> ROCKY_FUR_LOCATION;
            case INKY -> INKY_FUR_LOCATION;
            case FUZZY -> FUZZY_FUR_LOCATION;
            case LONG_NOSED -> LONG_NOSED_FUR_LOCATION;
            default -> FLECKED_FUR_LOCATION;
        };
        float[] fs = sheep.getVariant().getTextureDiffuseColors();
        coloredCutoutModelCopyLayerRender(this.getParentModel(), this.sheepModel, furLocation, poseStack, bufferSource, packedLight, sheep, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, fs[0], fs[1], fs[2]);
    }
}
