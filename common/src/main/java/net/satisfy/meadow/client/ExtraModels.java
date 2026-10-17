package net.satisfy.meadow.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.meadow.Meadow;

import java.util.List;

public final class ExtraModels {
    public static final ResourceLocation COOKING_FRAME_POT = Meadow.identifier("block/cooking_frame_pot");
    public static final ResourceLocation CHEESE_PRESS_SCREW = Meadow.identifier("block/cheese_press_screw");
    public static final ResourceLocation CHEESE_PRESS_PLATE = Meadow.identifier("block/cheese_press_plate");

    private ExtraModels() {
    }

    public static List<ResourceLocation> all() {
        return List.of(COOKING_FRAME_POT, CHEESE_PRESS_SCREW, CHEESE_PRESS_PLATE);
    }

    @ExpectPlatform
    public static BakedModel get(ResourceLocation id) {
        throw new AssertionError();
    }
}
