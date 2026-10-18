package net.satisfy.meadow.client;

import net.satisfy.foundation.client.armor.ArmorModels;
import net.satisfy.foundation.client.creative.CreativeSideTabs;
import net.minecraft.network.chat.Component;
import net.satisfy.meadow.core.registry.TabRegistry;
import net.satisfy.foundation.overlay.BlockInfoOverlay;
import net.satisfy.meadow.client.gui.overlay.CameraInfoProvider;
import net.satisfy.meadow.client.gui.overlay.CheesePressInfoProvider;
import net.satisfy.meadow.client.photo.CameraFlash;
import dev.architectury.event.events.client.ClientGuiEvent;
import net.satisfy.meadow.client.gui.overlay.CheeseRackInfoProvider;
import net.satisfy.meadow.client.gui.overlay.CookingPotInfoProvider;
import net.satisfy.meadow.client.gui.overlay.MilkCanInfoProvider;
import net.satisfy.meadow.client.gui.overlay.FonduePotInfoProvider;
import net.satisfy.meadow.client.renderer.block.TextureColors;
import net.satisfy.meadow.client.renderer.block.CheesePressRenderer;
import net.satisfy.meadow.client.renderer.block.PhotoFrameRenderer;
import net.satisfy.meadow.client.renderer.block.CookingPotRenderer;
import net.satisfy.meadow.client.renderer.block.FonduePotRenderer;
import net.satisfy.meadow.client.renderer.block.WoodcutterRenderer;
import net.satisfy.meadow.core.block.CookingPotEffects;
import net.satisfy.meadow.core.block.entity.FonduePotBlockEntity;
import net.satisfy.foundation.client.render.WardrobeRenderer;
import net.satisfy.foundation.client.wood.WoodBoatRenderer;
import net.satisfy.foundation.client.wood.WoodClient;
import net.satisfy.foundation.banner.CompletionistBannerRenderer;
import net.satisfy.foundation.storage.StorageBlockEntityRenderer;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.gui.DoormatEditScreen;
import net.satisfy.meadow.client.renderer.block.DoormatRenderer;
import net.satisfy.meadow.core.block.entity.DoormatBlockEntity;
import net.minecraft.client.Minecraft;
import net.satisfy.meadow.client.gui.WoodcutterGui;
import net.satisfy.meadow.client.model.FurBootsModel;
import net.satisfy.meadow.client.model.FurChestplateModel;
import net.satisfy.meadow.client.model.FurHelmetModel;
import net.satisfy.meadow.client.model.FurLeggingsModel;
import net.satisfy.meadow.client.model.WaterBuffaloModel;
import net.satisfy.meadow.client.model.WoolyCowModel;
import net.satisfy.meadow.client.renderer.block.storage.CheeseRackRenderer;
import net.satisfy.meadow.client.renderer.block.storage.FlowerBoxRenderer;
import net.satisfy.meadow.client.renderer.block.storage.FlowerPotBigRenderer;
import net.satisfy.meadow.client.renderer.block.storage.FlowerPotSmallRenderer;
import net.satisfy.meadow.client.renderer.block.storage.WheelBarrowRenderer;
import net.satisfy.meadow.client.renderer.entity.WaterBuffaloRenderer;
import net.satisfy.meadow.client.renderer.entity.WoolyCowRenderer;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.ScreenHandlerRegistry;
import net.satisfy.meadow.core.registry.StorageTypeRegistry;
import net.satisfy.meadow.core.util.MeadowWoodType;

import static net.satisfy.meadow.core.registry.ObjectRegistry.*;

public class MeadowClient {
    public static final ModelLayerLocation SHEARABLE_MEADOW_COW_MODEL_LAYER = new ModelLayerLocation(Meadow.identifier("shearable_meadow_cow"), "head");
    public static final ModelLayerLocation WATER_BUFFALO_MODEL_LAYER = new ModelLayerLocation(Meadow.identifier("water_buffalo"), "head");

    public static void initClient() {
        RenderTypeRegistry.register(RenderType.cutout(), CHEESE_FORM.get(), PHOTO_FRAME.get(), RUSTIC_CARPET.get(), LINEN_CARPET.get(), JACQUARD_CARPET.get(), PLAID_CARPET.get(), CHAMBRAY_CARPET.get(), TWEED_CARPET.get(), WARPED_CARPET.get(), DOORMAT.get(), OIL_LANTERN.get(), PINE_DOOR.get(), PINE_TRAPDOOR.get(), DELPHINIUM.get(), ALPINE_POPPY.get(), SAXIFRAGE.get(), ENZIAN.get(), COOKING_CAULDRON.get(), COOKING_FRAME.get(), PINE_TABLE.get(), FIREWOOD.get(), ERIOPHORUM.get(), TALL_ERIOPHORUM.get(), SMALL_FIR.get(), PINE_CHAIR.get(), PINE_SOFA_RUSTIC.get(), PINE_SOFA_LINEN.get(), PINE_SOFA_JACQUARD.get(), PINE_SOFA_PLAID.get(), PINE_SOFA_CHAMBRAY.get(), PINE_SOFA_TWEED.get(), PINE_SOFA_WARPED.get(), PINE_DRESSER.get(), POTTED_DELPHINIUM.get(), POTTED_ALPINE_POPPY.get(), POTTED_SAXIFRAGE.get(), POTTED_ENZIAN.get(), POTTED_ERIOPHORUM.get(), PINE_SAPLING.get(), POTTED_PINE_SAPLING.get(), FIRE_LILY.get(), POTTED_FIRE_LILY.get(), WOODEN_FLOWER_POT_SMALL.get(), WHEELBARROW.get(), WOODEN_FLOWER_POT_BIG.get(), WOODCUTTER.get(), ALPINE_BIRCH_SAPLING.get(), YELLOW_PINE_SAPLING.get(), POTTED_ALPINE_BIRCH_SAPLING.get(), POTTED_YELLOW_PINE_SAPLING.get(), PINE_WARDROBE.get(), MILK_CAN.get());        RenderTypeRegistry.register(RenderType.translucent(), ARTISAN_GLASS_WINDOW_PANE.get(), ORNATE_GLASS_WINDOW_PANE.get(), PINE_WINDOW_PANE.get(), ARTISAN_GLASS_WINDOW.get(), ORNATE_GLASS_WINDOW.get(), PINE_WINDOW.get());


        registerStorageTypeRenderers();
        registerClientScreens();

        ColorHandlerRegistry.registerBlockColors((state, world, pos, tintIndex) -> {
                    if (world == null || pos == null) {
                        return -1;
                    }
                    return BiomeColors.getAverageWaterColor(world, pos);
                }, WOODEN_WATER_CAULDRON, WATERING_CAN
        );

        ItemPropertiesRegistry.register(WATERING_CAN_ITEM.get(), Meadow.identifier("empty"), (stack, level, entity, seed) -> stack.getDamageValue() >= stack.getMaxDamage() ? 1.0F : 0.0F);

        registerBlockEntityRenderer();
        registerArmorModels();
        MeadowTooltips.init();
        BlockInfoOverlay.registerProvider(new FonduePotInfoProvider());
        BlockInfoOverlay.registerProvider(new CheeseRackInfoProvider());
        BlockInfoOverlay.registerProvider(new CookingPotInfoProvider());
        BlockInfoOverlay.registerProvider(new MilkCanInfoProvider());
        BlockInfoOverlay.registerProvider(new CheesePressInfoProvider());
        BlockInfoOverlay.registerProvider(new CameraInfoProvider());
        ClientGuiEvent.RENDER_HUD.register(CameraFlash::render);
        CreativeSideTabs.register(TabRegistry.MEADOW_TAB.getKey(),
                CreativeSideTabs.SideTab.of(Component.translatable("creativetab.meadow.side.cheese"), CHEESE_WHEEL.get(), TabRegistry::acceptCheese),
                CreativeSideTabs.SideTab.of(Component.translatable("creativetab.meadow.side.pine"), PINE_LOG.get(), TabRegistry::acceptPine),
                CreativeSideTabs.SideTab.of(Component.translatable("creativetab.meadow.side.nature"), ENZIAN.get(), TabRegistry::acceptNature));
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

    public static void openDoormatScreen(DoormatBlockEntity doormat) {
        Minecraft.getInstance().setScreen(new DoormatEditScreen(doormat));
    }

    private static void registerClientScreens() {
        MenuRegistry.registerScreenFactory(ScreenHandlerRegistry.WOODCUTTER_SCREEN_HANDLER.get(), WoodcutterGui::new);
    }

    public static void registerBlockEntityRenderer() {
        BlockEntityRendererRegistry.register(EntityTypeRegistry.MEADOW_BANNER.get(), CompletionistBannerRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.STORAGE_ENTITY.get(), context -> new StorageBlockEntityRenderer());
        BlockEntityRendererRegistry.register(EntityTypeRegistry.WARDROBE_BLOCK_ENTITY.get(), WardrobeRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.FONDUE_POT_BLOCK_ENTITY.get(), FonduePotRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.WOODCUTTER_BLOCK_ENTITY.get(), WoodcutterRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.COOKING_CAULDRON.get(), CookingPotRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.DOORMAT_BLOCK_ENTITY.get(), DoormatRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.CHEESE_FORM_BLOCK_ENTITY.get(), CheesePressRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.PHOTO_FRAME_BLOCK_ENTITY.get(), PhotoFrameRenderer::new);
        CookingPotEffects.colorProvider = CookingPotRenderer::color;
        FonduePotBlockEntity.colorProvider = pot -> TextureColors.blend(pot.getPortions(), pot.getMelting(), pot.meltProgress(0) * pot.meltingPortions());
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