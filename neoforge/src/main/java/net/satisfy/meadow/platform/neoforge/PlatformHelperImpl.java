package net.satisfy.meadow.platform.neoforge;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.neoforge.core.config.MeadowNeoForgeConfig;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class PlatformHelperImpl {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Meadow.MOD_ID);

    public static <T extends Entity> Supplier<EntityType<T>> registerBoatType(String name, EntityType.EntityFactory<T> factory, MobCategory category, float width, float height, int clientTrackingRange) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(factory, category).sized(width, height).build(name));
    }

    public static boolean shouldGiveEffect() {
        return MeadowNeoForgeConfig.bannerGiveEffect;
    }

    public static int getBannerEffectRadius() {
        return MeadowNeoForgeConfig.bannerEffectRadius;
    }

    public static int getBannerEffectAmplifier() {
        return MeadowNeoForgeConfig.bannerEffectAmplifier;
    }

    public static boolean showFonduePotInfo() {
        return MeadowNeoForgeConfig.showFonduePotInfo;
    }

    public static boolean showCheeseRackInfo() {
        return MeadowNeoForgeConfig.showCheeseRackInfo;
    }

    public static boolean showCookingPotInfo() {
        return MeadowNeoForgeConfig.showCookingPotInfo;
    }

    public static boolean showMilkCanInfo() {
        return MeadowNeoForgeConfig.showMilkCanInfo;
    }

    public static boolean showCheesePressInfo() {
        return MeadowNeoForgeConfig.showCheesePressInfo;
    }

    public static boolean showCameraInfo() {
        return MeadowNeoForgeConfig.showCameraInfo;
    }

    public static boolean enableCameraFlash() {
        return MeadowNeoForgeConfig.enableCameraFlash;
    }

    public static int getCameraFlashRange() {
        return MeadowNeoForgeConfig.cameraFlashRange;
    }

    public static int getCameraFlashDuration() {
        return MeadowNeoForgeConfig.cameraFlashDuration;
    }

    public static int getCameraTimerDuration() {
        return MeadowNeoForgeConfig.cameraTimerDuration;
    }

    public static int getCheesePressDuration() {
        return MeadowNeoForgeConfig.cheesePressDuration;
    }

    public static int getFondueWheelMeltDuration() {
        return MeadowNeoForgeConfig.fondueWheelMeltDuration;
    }

    public static int getFonduePieceMeltDuration() {
        return MeadowNeoForgeConfig.fonduePieceMeltDuration;
    }

    public static float getCookingPotSkimBonus() {
        return MeadowNeoForgeConfig.cookingPotSkimBonus / 100.0F;
    }

    public static float getMilkCanFillChance() {
        return MeadowNeoForgeConfig.milkCanFillChance / 100.0F;
    }

    public static int getMilkCanCowRange() {
        return MeadowNeoForgeConfig.milkCanCowRange;
    }

    public static int getWoolyCowMaxWool() {
        return MeadowNeoForgeConfig.woolyCowMaxWool;
    }

    public static boolean isWarpedCowPortalSpawnEnabled() {
        return MeadowNeoForgeConfig.enableWarpedCowPortalSpawn;
    }

    public static int getWarpedCowPortalSpawnRarity() {
        return MeadowNeoForgeConfig.warpedCowPortalSpawnRarity;
    }

    public static int getWoolyCowSpawnWeight() {
        return MeadowNeoForgeConfig.woolyCowSpawnWeight;
    }

    public static int getWoolyCowMinGroupSize() {
        return MeadowNeoForgeConfig.woolyCowMinGroupSize;
    }

    public static int getWoolyCowMaxGroupSize() {
        return MeadowNeoForgeConfig.woolyCowMaxGroupSize;
    }

    public static int getWarpedCowSpawnWeight() {
        return MeadowNeoForgeConfig.warpedCowSpawnWeight;
    }

    public static int getWarpedCowMinGroupSize() {
        return MeadowNeoForgeConfig.warpedCowMinGroupSize;
    }

    public static int getWarpedCowMaxGroupSize() {
        return MeadowNeoForgeConfig.warpedCowMaxGroupSize;
    }

    public static int getWaterBuffaloSpawnWeight() {
        return MeadowNeoForgeConfig.waterBuffaloSpawnWeight;
    }

    public static int getWaterBuffaloMinGroupSize() {
        return MeadowNeoForgeConfig.waterBuffaloMinGroupSize;
    }

    public static int getWaterBuffaloMaxGroupSize() {
        return MeadowNeoForgeConfig.waterBuffaloMaxGroupSize;
    }

    public static float getWoodenCauldronRainFillChance() {
        return MeadowNeoForgeConfig.woodenCauldronRainFillChance / 100.0F;
    }

    public static float getWoodenCauldronSnowFillChance() {
        return MeadowNeoForgeConfig.woodenCauldronSnowFillChance / 100.0F;
    }

    public static int getCameraMaxPaper() {
        return MeadowNeoForgeConfig.cameraMaxPaper;
    }

    public static int getCameraCooldown() {
        return MeadowNeoForgeConfig.cameraCooldown;
    }

    public static int getFeltingNeedleUseDuration() {
        return MeadowNeoForgeConfig.feltingNeedleUseDuration;
    }

    public static int getClimbingRopeMaxLength() {
        return MeadowNeoForgeConfig.climbingRopeMaxLength;
    }

    public static boolean enableLimestoneCliffs() {
        return MeadowNeoForgeConfig.enableLimestoneCliffs;
    }

    public static boolean isFurSetBonusEnabled() {
        return MeadowNeoForgeConfig.enableFurSetBonus;
    }

    public static double getFurSetKnockbackResistance() {
        return MeadowNeoForgeConfig.furSetKnockbackResistance / 100.0;
    }

    public static boolean animationsEnabled() {
        return MeadowNeoForgeConfig.animations;
    }

    public static int getEffectDuration(String itemName) {
        try {
            return switch (itemName) {
                case "warped_cheese_slice" -> MeadowNeoForgeConfig.WARPED_CHEESE_SLICE_EFFECT_DURATION.get();
                case "roasted_buffalo_ham" -> MeadowNeoForgeConfig.ROASTED_BUFFALO_HAM_EFFECT_DURATION.get();
                default -> -1;
            };
        } catch (IllegalStateException notLoadedYet) {
            return switch (itemName) {
                case "warped_cheese_slice" -> MeadowNeoForgeConfig.warpedCheeseSliceEffectDuration;
                case "roasted_buffalo_ham" -> MeadowNeoForgeConfig.roastedBuffaloHamEffectDuration;
                default -> -1;
            };
        }
    }

    public static int getNutrition(String itemName) {
        try {
            return switch (itemName) {
                case "cheesecake_slice" -> MeadowNeoForgeConfig.CHEESECAKE_SLICE_NUTRITION.get();
                case "cheese_tart_slice" -> MeadowNeoForgeConfig.CHEESE_TART_SLICE_NUTRITION.get();
                case "cheese_slice" -> MeadowNeoForgeConfig.CHEESE_SLICE_NUTRITION.get();
                case "sheep_cheese_slice" -> MeadowNeoForgeConfig.SHEEP_CHEESE_SLICE_NUTRITION.get();
                case "grain_cheese_slice" -> MeadowNeoForgeConfig.GRAIN_CHEESE_SLICE_NUTRITION.get();
                case "amethyst_cheese_slice" -> MeadowNeoForgeConfig.AMETHYST_CHEESE_SLICE_NUTRITION.get();
                case "buffalo_cheese_slice" -> MeadowNeoForgeConfig.BUFFALO_CHEESE_SLICE_NUTRITION.get();
                case "goat_cheese_slice" -> MeadowNeoForgeConfig.GOAT_CHEESE_SLICE_NUTRITION.get();
                case "warped_cheese_slice" -> MeadowNeoForgeConfig.WARPED_CHEESE_SLICE_NUTRITION.get();
                case "cheese_sandwich" -> MeadowNeoForgeConfig.CHEESE_SANDWICH_NUTRITION.get();
                case "cheese_roll" -> MeadowNeoForgeConfig.CHEESE_ROLL_NUTRITION.get();
                case "cheese_stick" -> MeadowNeoForgeConfig.CHEESE_STICK_NUTRITION.get();
                case "raw_buffalo_meat" -> MeadowNeoForgeConfig.RAW_BUFFALO_MEAT_NUTRITION.get();
                case "cooked_buffalo_meat" -> MeadowNeoForgeConfig.COOKED_BUFFALO_MEAT_NUTRITION.get();
                case "roasted_buffalo_ham" -> MeadowNeoForgeConfig.ROASTED_BUFFALO_HAM_NUTRITION.get();
                case "sausage_with_cheese" -> MeadowNeoForgeConfig.SAUSAGE_WITH_CHEESE_NUTRITION.get();
                default -> 0;
            };
        } catch (IllegalStateException notLoadedYet) {
            return switch (itemName) {
                case "cheesecake_slice" -> MeadowNeoForgeConfig.cheesecakeSliceNutrition;
                case "cheese_tart_slice" -> MeadowNeoForgeConfig.cheeseTartSliceNutrition;
                case "cheese_slice" -> MeadowNeoForgeConfig.cheeseSliceNutrition;
                case "sheep_cheese_slice" -> MeadowNeoForgeConfig.sheepCheeseSliceNutrition;
                case "grain_cheese_slice" -> MeadowNeoForgeConfig.grainCheeseSliceNutrition;
                case "amethyst_cheese_slice" -> MeadowNeoForgeConfig.amethystCheeseSliceNutrition;
                case "buffalo_cheese_slice" -> MeadowNeoForgeConfig.buffaloCheeseSliceNutrition;
                case "goat_cheese_slice" -> MeadowNeoForgeConfig.goatCheeseSliceNutrition;
                case "warped_cheese_slice" -> MeadowNeoForgeConfig.warpedCheeseSliceNutrition;
                case "cheese_sandwich" -> MeadowNeoForgeConfig.cheeseSandwichNutrition;
                case "cheese_roll" -> MeadowNeoForgeConfig.cheeseRollNutrition;
                case "cheese_stick" -> MeadowNeoForgeConfig.cheeseStickNutrition;
                case "raw_buffalo_meat" -> MeadowNeoForgeConfig.rawBuffaloMeatNutrition;
                case "cooked_buffalo_meat" -> MeadowNeoForgeConfig.cookedBuffaloMeatNutrition;
                case "roasted_buffalo_ham" -> MeadowNeoForgeConfig.roastedBuffaloHamNutrition;
                case "sausage_with_cheese" -> MeadowNeoForgeConfig.sausageWithCheeseNutrition;
                default -> 0;
            };
        }
    }

    public static float getSaturation(String itemName) {
        try {
            return switch (itemName) {
                case "cheesecake_slice" -> MeadowNeoForgeConfig.CHEESECAKE_SLICE_SATURATION.get().floatValue();
                case "cheese_tart_slice" -> MeadowNeoForgeConfig.CHEESE_TART_SLICE_SATURATION.get().floatValue();
                case "cheese_slice" -> MeadowNeoForgeConfig.CHEESE_SLICE_SATURATION.get().floatValue();
                case "sheep_cheese_slice" -> MeadowNeoForgeConfig.SHEEP_CHEESE_SLICE_SATURATION.get().floatValue();
                case "grain_cheese_slice" -> MeadowNeoForgeConfig.GRAIN_CHEESE_SLICE_SATURATION.get().floatValue();
                case "amethyst_cheese_slice" -> MeadowNeoForgeConfig.AMETHYST_CHEESE_SLICE_SATURATION.get().floatValue();
                case "buffalo_cheese_slice" -> MeadowNeoForgeConfig.BUFFALO_CHEESE_SLICE_SATURATION.get().floatValue();
                case "goat_cheese_slice" -> MeadowNeoForgeConfig.GOAT_CHEESE_SLICE_SATURATION.get().floatValue();
                case "warped_cheese_slice" -> MeadowNeoForgeConfig.WARPED_CHEESE_SLICE_SATURATION.get().floatValue();
                case "cheese_sandwich" -> MeadowNeoForgeConfig.CHEESE_SANDWICH_SATURATION.get().floatValue();
                case "cheese_roll" -> MeadowNeoForgeConfig.CHEESE_ROLL_SATURATION.get().floatValue();
                case "cheese_stick" -> MeadowNeoForgeConfig.CHEESE_STICK_SATURATION.get().floatValue();
                case "raw_buffalo_meat" -> MeadowNeoForgeConfig.RAW_BUFFALO_MEAT_SATURATION.get().floatValue();
                case "cooked_buffalo_meat" -> MeadowNeoForgeConfig.COOKED_BUFFALO_MEAT_SATURATION.get().floatValue();
                case "roasted_buffalo_ham" -> MeadowNeoForgeConfig.ROASTED_BUFFALO_HAM_SATURATION.get().floatValue();
                case "sausage_with_cheese" -> MeadowNeoForgeConfig.SAUSAGE_WITH_CHEESE_SATURATION.get().floatValue();
                default -> 0.0f;
            };
        } catch (IllegalStateException notLoadedYet) {
            return switch (itemName) {
                case "cheesecake_slice" -> (float) MeadowNeoForgeConfig.cheesecakeSliceSaturation;
                case "cheese_tart_slice" -> (float) MeadowNeoForgeConfig.cheeseTartSliceSaturation;
                case "cheese_slice" -> (float) MeadowNeoForgeConfig.cheeseSliceSaturation;
                case "sheep_cheese_slice" -> (float) MeadowNeoForgeConfig.sheepCheeseSliceSaturation;
                case "grain_cheese_slice" -> (float) MeadowNeoForgeConfig.grainCheeseSliceSaturation;
                case "amethyst_cheese_slice" -> (float) MeadowNeoForgeConfig.amethystCheeseSliceSaturation;
                case "buffalo_cheese_slice" -> (float) MeadowNeoForgeConfig.buffaloCheeseSliceSaturation;
                case "goat_cheese_slice" -> (float) MeadowNeoForgeConfig.goatCheeseSliceSaturation;
                case "warped_cheese_slice" -> (float) MeadowNeoForgeConfig.warpedCheeseSliceSaturation;
                case "cheese_sandwich" -> (float) MeadowNeoForgeConfig.cheeseSandwichSaturation;
                case "cheese_roll" -> (float) MeadowNeoForgeConfig.cheeseRollSaturation;
                case "cheese_stick" -> (float) MeadowNeoForgeConfig.cheeseStickSaturation;
                case "raw_buffalo_meat" -> (float) MeadowNeoForgeConfig.rawBuffaloMeatSaturation;
                case "cooked_buffalo_meat" -> (float) MeadowNeoForgeConfig.cookedBuffaloMeatSaturation;
                case "roasted_buffalo_ham" -> (float) MeadowNeoForgeConfig.roastedBuffaloHamSaturation;
                case "sausage_with_cheese" -> (float) MeadowNeoForgeConfig.sausageWithCheeseSaturation;
                default -> 0.0f;
            };
        }
    }

    public static boolean isModLoaded(String modid) {
        ModList list = ModList.get();
        if (list != null) {
            return list.isLoaded(modid);
        }
        return isModPreLoaded(modid);
    }

    public static boolean isModPreLoaded(String modid) {
        return getPreLoadedModInfo(modid) != null;
    }

    public static @Nullable ModInfo getPreLoadedModInfo(String modId) {
        for (ModInfo info : LoadingModList.get().getMods()) {
            if (info.getModId().equals(modId)) {
                return info;
            }
        }
        return null;
    }
}
