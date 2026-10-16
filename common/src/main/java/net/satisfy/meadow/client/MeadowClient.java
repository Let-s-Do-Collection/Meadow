package net.satisfy.meadow.client;

import net.satisfy.foundation.client.armor.ArmorModels;
import net.satisfy.foundation.client.render.WardrobeRenderer;
import net.satisfy.foundation.client.wood.WoodBoatRenderer;
import net.satisfy.foundation.client.wood.WoodClient;
import net.satisfy.foundation.banner.CompletionistBannerRenderer;
import net.satisfy.foundation.storage.StorageBlockEntityRenderer;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
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
import net.satisfy.meadow.client.model.FurBootsModel;
import net.satisfy.meadow.client.model.FurChestplateModel;
import net.satisfy.meadow.client.model.FurHelmetModel;
import net.satisfy.meadow.client.model.FurLeggingsModel;
import net.satisfy.meadow.client.model.WaterBuffaloModel;
import net.satisfy.meadow.client.model.WoolyCowModel;
import net.satisfy.meadow.client.particle.FondueBubbleParticle;
import net.satisfy.meadow.client.renderer.block.storage.CheeseRackRenderer;
import net.satisfy.meadow.client.renderer.block.storage.FlowerBoxRenderer;
import net.satisfy.meadow.client.renderer.block.storage.FlowerPotBigRenderer;
import net.satisfy.meadow.client.renderer.block.storage.FlowerPotSmallRenderer;
import net.satisfy.meadow.client.renderer.block.storage.WheelBarrowRenderer;
import net.satisfy.meadow.client.renderer.entity.WaterBuffaloRenderer;
import net.satisfy.meadow.client.renderer.entity.WoolyCowRenderer;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.ParticleTypeRegistry;
import net.satisfy.meadow.core.registry.ScreenHandlerRegistry;
import net.satisfy.meadow.core.registry.StorageTypeRegistry;
import net.satisfy.meadow.core.util.MeadowWoodType;

import static net.satisfy.meadow.core.registry.ObjectRegistry.*;

public class MeadowClient {
    public static final ModelLayerLocation SHEARABLE_MEADOW_COW_MODEL_LAYER = new ModelLayerLocation(Meadow.identifier("shearable_meadow_cow"), "head");
    public static final ModelLayerLocation WATER_BUFFALO_MODEL_LAYER = new ModelLayerLocation(Meadow.identifier("water_buffalo"), "head");

    public static void initClient() {
        RenderTypeRegistry.register(RenderType.cutout(), CHEESE_FORM.get(), RUSTIC_CARPET.get(), LINEN_CARPET.get(), JACQUARD_CARPET.get(), PLAID_CARPET.get(), CHAMBRAY_CARPET.get(), TWEED_CARPET.get(), WARPED_CARPET.get(), DOORMAT.get(), OIL_LANTERN.get(), PINE_DOOR.get(), PINE_TRAPDOOR.get(), DELPHINIUM.get(), ALPINE_POPPY.get(), SAXIFRAGE.get(), ENZIAN.get(), COOKING_CAULDRON.get(), COOKING_FRAME.get(), PINE_TABLE.get(), FIREWOOD.get(), ERIOPHORUM.get(), TALL_ERIOPHORUM.get(), SMALL_FIR.get(), PINE_CHAIR.get(), PINE_SOFA_RUSTIC.get(), PINE_SOFA_LINEN.get(), PINE_SOFA_JACQUARD.get(), PINE_SOFA_PLAID.get(), PINE_SOFA_CHAMBRAY.get(), PINE_SOFA_TWEED.get(), PINE_SOFA_WARPED.get(), PINE_DRESSER.get(), POTTED_DELPHINIUM.get(), POTTED_ALPINE_POPPY.get(), POTTED_SAXIFRAGE.get(), POTTED_ENZIAN.get(), POTTED_ERIOPHORUM.get(), PINE_SAPLING.get(), POTTED_PINE_SAPLING.get(), FIRE_LILY.get(), POTTED_FIRE_LILY.get(), WOODEN_FLOWER_POT_SMALL.get(), WHEELBARROW.get(), WOODEN_FLOWER_POT_BIG.get(), WOODCUTTER.get(), ALPINE_BIRCH_SAPLING.get(), YELLOW_PINE_SAPLING.get(), POTTED_ALPINE_BIRCH_SAPLING.get(), POTTED_YELLOW_PINE_SAPLING.get(), PINE_WARDROBE.get(), MILK_CAN.get());        RenderTypeRegistry.register(RenderType.translucent(), ARTISAN_GLASS_WINDOW_PANE.get(), ORNATE_GLASS_WINDOW_PANE.get(), PINE_WINDOW_PANE.get(), ARTISAN_GLASS_WINDOW.get(), ORNATE_GLASS_WINDOW.get(), PINE_WINDOW.get());

        ParticleProviderRegistry.register(ParticleTypeRegistry.FONDUE_BUBBLE.get(), FondueBubbleParticle.Provider::new);

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
        registerArmorModels();
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
        BlockEntityRendererRegistry.register(EntityTypeRegistry.WARDROBE_BLOCK_ENTITY.get(), WardrobeRenderer::new);
        WoodClient.registerSignMaterials(MeadowWoodType.PINE);
        WoodClient.registerSignRenderers(EntityTypeRegistry.MOD_SIGN.get(), EntityTypeRegistry.MOD_HANGING_SIGN.get());
    }

    private static void registerEntityRenderers() {
        EntityRendererRegistry.register(EntityTypeRegistry.WOOLY_COW, WoolyCowRenderer::new);
        EntityRendererRegistry.register(EntityTypeRegistry.WATER_BUFFALO, WaterBuffaloRenderer::new);
        EntityRendererRegistry.register(EntityTypeRegistry.PINE_BOAT, context -> new WoodBoatRenderer(context, false));
        EntityRendererRegistry.register(EntityTypeRegistry.PINE_CHEST_BOAT, context -> new WoodBoatRenderer(context, true));
    }

    public static void registerEntityModelLayers() {
        EntityModelLayerRegistry.register(FurHelmetModel.LAYER_LOCATION, FurHelmetModel::createBodyLayer);
        EntityModelLayerRegistry.register(FurChestplateModel.LAYER_LOCATION, FurChestplateModel::createBodyLayer);
        EntityModelLayerRegistry.register(FurLeggingsModel.LAYER_LOCATION, FurLeggingsModel::createBodyLayer);
        EntityModelLayerRegistry.register(FurBootsModel.LAYER_LOCATION, FurBootsModel::createBodyLayer);
        EntityModelLayerRegistry.register(SHEARABLE_MEADOW_COW_MODEL_LAYER, WoolyCowModel::createBodyLayer);
        EntityModelLayerRegistry.register(WATER_BUFFALO_MODEL_LAYER, WaterBuffaloModel::getTexturedModelData);
        WoodClient.registerBoatLayers(MeadowWoodType.PINE_BOAT);
    }

    public static void registerArmorModels() {
        ArmorModels.register(FurHelmetModel.LAYER_LOCATION, FurHelmetModel::new, FUR_HELMET.get());
        ArmorModels.register(FurChestplateModel.LAYER_LOCATION, FurChestplateModel::new, FUR_CHESTPLATE.get());
        ArmorModels.register(FurLeggingsModel.LAYER_LOCATION, FurLeggingsModel::new, FUR_LEGGINGS.get());
        ArmorModels.register(FurBootsModel.LAYER_LOCATION, FurBootsModel::new, FUR_BOOTS.get());
    }
}