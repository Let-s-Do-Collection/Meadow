package net.satisfy.meadow.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class PlatformHelper {
    @ExpectPlatform
    public static <T extends Entity> Supplier<EntityType<T>> registerBoatType(String name, EntityType.EntityFactory<T> factory, MobCategory category, float width, float height, int clientTrackingRange) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isModLoaded(String modid){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean shouldGiveEffect() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getBannerEffectRadius() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getBannerEffectAmplifier() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showFonduePotInfo() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showCheeseRackInfo() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showCookingPotInfo() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showMilkCanInfo() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showCheesePressInfo() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showCameraInfo() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean enableCameraFlash() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getCameraFlashRange() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getCameraFlashDuration() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getCameraTimerDuration() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getCheesePressDuration() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getFondueWheelMeltDuration() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getFonduePieceMeltDuration() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static float getCookingPotSkimBonus() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static float getMilkCanFillChance() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getMilkCanCowRange() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWoolyCowMaxWool() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isWarpedCowPortalSpawnEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWarpedCowPortalSpawnRarity() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWoolyCowSpawnWeight() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWoolyCowMinGroupSize() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWoolyCowMaxGroupSize() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWarpedCowSpawnWeight() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWarpedCowMinGroupSize() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWarpedCowMaxGroupSize() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWaterBuffaloSpawnWeight() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWaterBuffaloMinGroupSize() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWaterBuffaloMaxGroupSize() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static float getWoodenCauldronRainFillChance() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static float getWoodenCauldronSnowFillChance() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getCameraMaxPaper() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getCameraCooldown() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getFeltingNeedleUseDuration() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getClimbingRopeMaxLength() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean enableLimestoneCliffs() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isFurSetBonusEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static double getFurSetKnockbackResistance() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean animationsEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getEffectDuration(String itemName) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getNutrition(String itemName) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static float getSaturation(String itemName) {
        throw new AssertionError();
    }
}
