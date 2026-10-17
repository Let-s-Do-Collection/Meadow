package net.satisfy.meadow.platform.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.fabric.core.config.MeadowFabricConfig;
import net.satisfy.meadow.platform.PlatformHelper;

import java.util.function.Supplier;

public class PlatformHelperImpl extends PlatformHelper {
    private static MeadowFabricConfig config() {
        return AutoConfig.getConfigHolder(MeadowFabricConfig.class).getConfig();
    }

    public static <T extends Entity> Supplier<EntityType<T>> registerBoatType(String name, EntityType.EntityFactory<T> factory, MobCategory category, float width, float height, int clientTrackingRange) {
        EntityType<T> registry = Registry.register(BuiltInRegistries.ENTITY_TYPE, Meadow.identifier(name), FabricEntityTypeBuilder.create(category, factory).dimensions(EntityDimensions.scalable(width, height)).trackRangeChunks(clientTrackingRange).build());
        return () -> registry;
    }

    public static boolean shouldGiveEffect() {
        return config().effects.completionistBannerEffect.bannerGiveEffect;
    }

    public static int getBannerEffectRadius() {
        return config().effects.completionistBannerEffect.bannerEffectRadius;
    }

    public static int getBannerEffectAmplifier() {
        return config().effects.completionistBannerEffect.bannerEffectAmplifier;
    }

    public static boolean showFonduePotInfo() {
        return config().infoTooltips.showFonduePotInfo;
    }

    public static boolean showCheeseRackInfo() {
        return config().infoTooltips.showCheeseRackInfo;
    }

    public static boolean showCookingPotInfo() {
        return config().infoTooltips.showCookingPotInfo;
    }

    public static boolean showMilkCanInfo() {
        return config().infoTooltips.showMilkCanInfo;
    }

    public static boolean showCheesePressInfo() {
        return config().infoTooltips.showCheesePressInfo;
    }

    public static boolean showCameraInfo() {
        return config().infoTooltips.showCameraInfo;
    }

    public static boolean enableCameraFlash() {
        return config().camera.enableCameraFlash;
    }

    public static int getCameraFlashRange() {
        return config().camera.cameraFlashRange;
    }

    public static int getCameraFlashDuration() {
        return config().camera.cameraFlashDuration;
    }

    public static int getCameraTimerDuration() {
        return config().camera.cameraTimerDuration;
    }

    public static int getCheesePressDuration() {
        return config().cheese.cheesePressDuration;
    }

    public static int getFondueWheelMeltDuration() {
        return config().cheese.fondueWheelMeltDuration;
    }

    public static int getFonduePieceMeltDuration() {
        return config().cheese.fonduePieceMeltDuration;
    }

    public static float getCookingPotSkimBonus() {
        return config().cheese.cookingPotSkimBonus / 100.0F;
    }

    public static float getMilkCanFillChance() {
        return config().cheese.milkCanFillChance / 100.0F;
    }

    public static int getMilkCanCowRange() {
        return config().cheese.milkCanCowRange;
    }

    public static int getWoolyCowMaxWool() {
        return config().animals.woolyCowMaxWool;
    }

    public static boolean isWarpedCowPortalSpawnEnabled() {
        return config().animals.enableWarpedCowPortalSpawn;
    }

    public static int getWarpedCowPortalSpawnRarity() {
        return config().animals.warpedCowPortalSpawnRarity;
    }

    public static int getWoolyCowSpawnWeight() {
        return config().animals.woolyCowSpawnWeight;
    }

    public static int getWoolyCowMinGroupSize() {
        return config().animals.woolyCowMinGroupSize;
    }

    public static int getWoolyCowMaxGroupSize() {
        return config().animals.woolyCowMaxGroupSize;
    }

    public static int getWarpedCowSpawnWeight() {
        return config().animals.warpedCowSpawnWeight;
    }

    public static int getWarpedCowMinGroupSize() {
        return config().animals.warpedCowMinGroupSize;
    }

    public static int getWarpedCowMaxGroupSize() {
        return config().animals.warpedCowMaxGroupSize;
    }

    public static int getWaterBuffaloSpawnWeight() {
        return config().animals.waterBuffaloSpawnWeight;
    }

    public static int getWaterBuffaloMinGroupSize() {
        return config().animals.waterBuffaloMinGroupSize;
    }

    public static int getWaterBuffaloMaxGroupSize() {
        return config().animals.waterBuffaloMaxGroupSize;
    }

    public static float getWoodenCauldronRainFillChance() {
        return config().weather.woodenCauldronRainFillChance / 100.0F;
    }

    public static float getWoodenCauldronSnowFillChance() {
        return config().weather.woodenCauldronSnowFillChance / 100.0F;
    }

    public static int getCameraMaxPaper() {
        return config().camera.cameraMaxPaper;
    }

    public static int getCameraCooldown() {
        return config().camera.cameraCooldown;
    }

    public static int getFeltingNeedleUseDuration() {
        return config().tools.feltingNeedleUseDuration;
    }

    public static int getClimbingRopeMaxLength() {
        return config().tools.climbingRopeMaxLength;
    }

    public static boolean isFurSetBonusEnabled() {
        return config().armor.enableFurSetBonus;
    }

    public static double getFurSetKnockbackResistance() {
        return config().armor.furSetKnockbackResistance / 100.0;
    }

    public static boolean animationsEnabled() {
        return config().misc.animations;
    }

    public static int getEffectDuration(String itemName) {
        MeadowFabricConfig.FoodSettings food = config().food;
        return switch (itemName) {
            case "warped_cheese_slice" -> food.warpedCheeseSliceEffectDuration;
            case "roasted_buffalo_ham" -> food.roastedBuffaloHamEffectDuration;
            default -> -1;
        };
    }

    public static int getNutrition(String itemName) {
        MeadowFabricConfig.FoodSettings food = config().food;
        return switch (itemName) {
            case "cheesecake_slice" -> food.cheesecakeSliceNutrition;
            case "cheese_tart_slice" -> food.cheeseTartSliceNutrition;
            case "cheese_slice" -> food.cheeseSliceNutrition;
            case "sheep_cheese_slice" -> food.sheepCheeseSliceNutrition;
            case "grain_cheese_slice" -> food.grainCheeseSliceNutrition;
            case "amethyst_cheese_slice" -> food.amethystCheeseSliceNutrition;
            case "buffalo_cheese_slice" -> food.buffaloCheeseSliceNutrition;
            case "goat_cheese_slice" -> food.goatCheeseSliceNutrition;
            case "warped_cheese_slice" -> food.warpedCheeseSliceNutrition;
            case "cheese_sandwich" -> food.cheeseSandwichNutrition;
            case "cheese_roll" -> food.cheeseRollNutrition;
            case "cheese_stick" -> food.cheeseStickNutrition;
            case "raw_buffalo_meat" -> food.rawBuffaloMeatNutrition;
            case "cooked_buffalo_meat" -> food.cookedBuffaloMeatNutrition;
            case "roasted_buffalo_ham" -> food.roastedBuffaloHamNutrition;
            case "sausage_with_cheese" -> food.sausageWithCheeseNutrition;
            default -> 0;
        };
    }

    public static float getSaturation(String itemName) {
        MeadowFabricConfig.FoodSettings food = config().food;
        return switch (itemName) {
            case "cheesecake_slice" -> food.cheesecakeSliceSaturation;
            case "cheese_tart_slice" -> food.cheeseTartSliceSaturation;
            case "cheese_slice" -> food.cheeseSliceSaturation;
            case "sheep_cheese_slice" -> food.sheepCheeseSliceSaturation;
            case "grain_cheese_slice" -> food.grainCheeseSliceSaturation;
            case "amethyst_cheese_slice" -> food.amethystCheeseSliceSaturation;
            case "buffalo_cheese_slice" -> food.buffaloCheeseSliceSaturation;
            case "goat_cheese_slice" -> food.goatCheeseSliceSaturation;
            case "warped_cheese_slice" -> food.warpedCheeseSliceSaturation;
            case "cheese_sandwich" -> food.cheeseSandwichSaturation;
            case "cheese_roll" -> food.cheeseRollSaturation;
            case "cheese_stick" -> food.cheeseStickSaturation;
            case "raw_buffalo_meat" -> food.rawBuffaloMeatSaturation;
            case "cooked_buffalo_meat" -> food.cookedBuffaloMeatSaturation;
            case "roasted_buffalo_ham" -> food.roastedBuffaloHamSaturation;
            case "sausage_with_cheese" -> food.sausageWithCheeseSaturation;
            default -> 0.0f;
        };
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
