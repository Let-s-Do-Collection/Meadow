package net.satisfy.meadow.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.model.WoolySheepModel;
import net.satisfy.meadow.core.entity.WoolySheepEntity;
import org.jetbrains.annotations.NotNull;

public class WoolySheepRenderer extends MobRenderer<WoolySheepEntity, WoolySheepModel<WoolySheepEntity>> {
    private static final ResourceLocation FLECKED_WOOL_TEXTURE = Meadow.identifier("textures/entity/sheep/flecked_sheep.png");
    private static final ResourceLocation PATCHED_WOOL_TEXTURE = Meadow.identifier("textures/entity/sheep/patched_sheep.png");
    private static final ResourceLocation ROCKY_WOOL_TEXTURE = Meadow.identifier("textures/entity/sheep/rocky_sheep.png");
    private static final ResourceLocation INKY_WOOL_TEXTURE = Meadow.identifier("textures/entity/sheep/inky_sheep.png");
    private static final ResourceLocation FUZZY_WOOL_TEXTURE = Meadow.identifier("textures/entity/sheep/fuzzy_sheep.png");
    private static final ResourceLocation LONG_NOSED_WOOL_TEXTURE = Meadow.identifier("textures/entity/sheep/long_nosed_sheep.png");
    private static final ResourceLocation FLECKED_WOOL_TEXTURE_SHORN = Meadow.identifier("textures/entity/sheep/flecked_sheep.png");
    private static final ResourceLocation PATCHED_WOOL_TEXTURE_SHORN = Meadow.identifier("textures/entity/sheep/patched_sheep.png");
    private static final ResourceLocation ROCKY_WOOL_TEXTURE_SHORN = Meadow.identifier("textures/entity/sheep/rocky_sheep.png");
    private static final ResourceLocation INKY_WOOL_TEXTURE_SHORN = Meadow.identifier("textures/entity/sheep/inky_sheep.png");
    private static final ResourceLocation FUZZY_WOOL_TEXTURE_SHORN = Meadow.identifier("textures/entity/sheep/fuzzy_sheep.png");
    private static final ResourceLocation LONG_NOSED_WOOL_TEXTURE_SHORN = Meadow.identifier("textures/entity/sheep/long_nosed_sheep.png");

    public WoolySheepRenderer(EntityRendererProvider.Context context) {
        super(context, new WoolySheepModel<>(context.bakeLayer(ModelLayers.SHEEP)), 0.7F);
        this.addLayer(new WoolySheepFurLayerRenderer(this, context));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(WoolySheepEntity entity) {
        if (entity.isSheared()) {
            return switch (entity.getVariant()) {
                case PATCHED -> PATCHED_WOOL_TEXTURE_SHORN;
                case ROCKY -> ROCKY_WOOL_TEXTURE_SHORN;
                case INKY -> INKY_WOOL_TEXTURE_SHORN;
                case FUZZY -> FUZZY_WOOL_TEXTURE_SHORN;
                case LONG_NOSED -> LONG_NOSED_WOOL_TEXTURE_SHORN;
                default -> FLECKED_WOOL_TEXTURE_SHORN;
            };
        }
        return switch (entity.getVariant()) {
            case PATCHED -> PATCHED_WOOL_TEXTURE;
            case ROCKY -> ROCKY_WOOL_TEXTURE;
            case INKY -> INKY_WOOL_TEXTURE;
            case FUZZY -> FUZZY_WOOL_TEXTURE;
            case LONG_NOSED -> LONG_NOSED_WOOL_TEXTURE;
            default -> FLECKED_WOOL_TEXTURE;
        };
    }
}
