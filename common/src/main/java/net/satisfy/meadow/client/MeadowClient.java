package net.satisfy.meadow.client;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.gui.CheeseFormGui;
import net.satisfy.meadow.client.gui.CookingCauldronGui;
import net.satisfy.meadow.client.gui.WoodcutterGui;
import net.satisfy.meadow.client.model.*;
import net.satisfy.meadow.client.renderer.block.CompletionistBannerRenderer;
import net.satisfy.meadow.client.renderer.block.PineHangingSignRenderer;
import net.satisfy.meadow.client.renderer.block.PineSignRenderer;
import net.satisfy.meadow.client.renderer.block.storage.*;
import net.satisfy.meadow.client.renderer.entity.ChairRenderer;
import net.satisfy.meadow.client.renderer.entity.PineBoatRenderer;
import net.satisfy.meadow.client.renderer.entity.WaterBuffaloRenderer;
import net.satisfy.meadow.client.renderer.entity.WoolyCowRenderer;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.ScreenHandlerRegistry;
import net.satisfy.meadow.core.registry.StorageTypeRegistry;

import static net.satisfy.meadow.core.registry.ObjectRegistry.*;

public class MeadowClient {
    public static final ModelLayerLocation SHEARABLE_MEADOW_COW_MODEL_LAYER = new ModelLayerLocation(Meadow.identifier("shearable_meadow_cow"), "head");
    public static final ModelLayerLocation WATER_BUFFALO_MODEL_LAYER = new ModelLayerLocation(Meadow.identifier("water_buffalo"), "head");

    public static void initClient() {
        RenderTypeRegistry.register(RenderType.cutout(), PINE_DOOR.get(), PINE_TRAPDOOR.get(), DELPHINIUM.get(), ALPINE_POPPY.get(), SAXIFRAGE.get(), ENZIAN.get(), COOKING_CAULDRON.get(), FRAME.get(), PINE_TABLE.get(), FIRE_LOG.get(), ERIOPHORUM.get(), TALL_ERIOPHORUM.get(), SMALL_FIR.get(), PINE_SAPLING.get(), PINE_CHAIR.get(), POTTED_DELPHINIUM.get(), POTTED_ALPINE_POPPY.get(), POTTED_SAXIFRAGE.get(), POTTED_ENZIAN.get(), POTTED_ERIOPHORUM.get(), PINE_SAPLING.get(), POTTED_PINE_SAPLING.get(), FIRE_LILY.get(), POTTED_FIRE_LILY.get(), WOODEN_FLOWER_POT_SMALL.get(), FONDUE.get(), OIL_LANTERN.get(), WHEELBARROW.get(), YELLOW_PINE_LEAVES.get(), WOODEN_FLOWER_POT_BIG.get(), WOODCUTTER.get());
        RenderTypeRegistry.register(RenderType.translucent(), ARTISAN_GLASS_WINDOW_PANE.get(), ORNATE_GLASS_WINDOW_PANE.get(), PINE_WINDOW_PANE.get());

        registerStorageTypeRenderers();
        registerClientScreens();

        ColorHandlerRegistry.registerBlockColors((state, world, pos, tintIndex) -> {
                    if (world == null || pos == null) {
                        return -1;
                    }
                    return BiomeColors.getAverageWaterColor(world, pos);
                }, WOODEN_WATER_CAULDRON, WATERING_CAN
        );

        registerBlockEntityRenderer();
    }

    public static void preInitClient() {
        registerEntityRenderers();
        registerEntityModelLayers();
    }

    public static void registerStorageTypeRenderers() {
        StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.WHEEL_BARROW, new WheelBarrowRenderer());
        StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.FLOWER_BOX, new FlowerBoxRenderer());
        StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.FLOWER_POT_SMALL, new FlowerPotSmallRenderer());
        StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.FLOWER_POT_BIG, new FlowerPotBigRenderer());
        StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.CHEESE_RACK, new CheeseRackRenderer());
    }

    private static void registerClientScreens() {
        MenuRegistry.registerScreenFactory(ScreenHandlerRegistry.CHEESE_FORM_SCREEN_HANDLER.get(), CheeseFormGui::new);
        MenuRegistry.registerScreenFactory(ScreenHandlerRegistry.COOKING_CAULDRON_SCREEN_HANDLER.get(), CookingCauldronGui::new);
        MenuRegistry.registerScreenFactory(ScreenHandlerRegistry.WOODCUTTER_SCREEN_HANDLER.get(), WoodcutterGui::new);
    }

    public static void registerBlockEntityRenderer() {
        BlockEntityRendererRegistry.register(EntityTypeRegistry.MEADOW_BANNER.get(), CompletionistBannerRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.STORAGE_ENTITY.get(), context -> new StorageBlockEntityRenderer());
        BlockEntityRendererRegistry.register(EntityTypeRegistry.MOD_SIGN.get(), PineSignRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.MOD_HANGING_SIGN.get(), PineHangingSignRenderer::new);
    }

    private static void registerEntityRenderers() {
        EntityRendererRegistry.register(EntityTypeRegistry.CHAIR, ChairRenderer::new);
        EntityRendererRegistry.register(EntityTypeRegistry.WOOLY_COW, WoolyCowRenderer::new);
        EntityRendererRegistry.register(EntityTypeRegistry.WATER_BUFFALO, WaterBuffaloRenderer::new);
        EntityRendererRegistry.register(EntityTypeRegistry.PINE_BOAT, context -> new PineBoatRenderer<>(context, false));
        EntityRendererRegistry.register(EntityTypeRegistry.PINE_CHEST_BOAT, context -> new PineBoatRenderer<>(context, true));
    }

    public static void registerEntityModelLayers() {
        EntityModelLayerRegistry.register(FurHelmetModel.LAYER_LOCATION, FurHelmetModel::createBodyLayer);
        EntityModelLayerRegistry.register(FurChestplateModel.LAYER_LOCATION, FurChestplateModel::createBodyLayer);
        EntityModelLayerRegistry.register(FurLeggingsModel.LAYER_LOCATION, FurLeggingsModel::createBodyLayer);
        EntityModelLayerRegistry.register(FurBootsModel.LAYER_LOCATION, FurBootsModel::createBodyLayer);
        EntityModelLayerRegistry.register(CompletionistBannerRenderer.LAYER_LOCATION, CompletionistBannerRenderer::createBodyLayer);
        EntityModelLayerRegistry.register(SHEARABLE_MEADOW_COW_MODEL_LAYER, WoolyCowModel::createBodyLayer);
        EntityModelLayerRegistry.register(WATER_BUFFALO_MODEL_LAYER, WaterBuffaloModel::getTexturedModelData);
     }
}