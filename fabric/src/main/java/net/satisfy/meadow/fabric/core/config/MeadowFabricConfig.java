package net.satisfy.meadow.fabric.core.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "meadow")
@Config.Gui.Background("meadow:textures/block/pine_planks.png")
public class MeadowFabricConfig implements ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    public CheeseSettings cheese = new CheeseSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public AnimalsSettings animals = new AnimalsSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public WeatherSettings weather = new WeatherSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public CameraSettings camera = new CameraSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public ToolsSettings tools = new ToolsSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public ArmorSettings armor = new ArmorSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public InfoTooltipSettings infoTooltips = new InfoTooltipSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public EffectsSettings effects = new EffectsSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public MiscSettings misc = new MiscSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public FoodSettings food = new FoodSettings();

    public static class CheeseSettings {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 200, max = 24000)
        public int cheesePressDuration = 2400;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 10, max = 1200)
        public int fondueWheelMeltDuration = 100;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 10, max = 600)
        public int fonduePieceMeltDuration = 40;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int cookingPotSkimBonus = 15;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int milkCanFillChance = 2;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
        public int milkCanCowRange = 3;
    }

    public static class AnimalsSettings {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
        public int woolyCowMaxWool = 3;

        @ConfigEntry.Gui.Tooltip
        public boolean enableWarpedCowPortalSpawn = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 10000)
        public int warpedCowPortalSpawnRarity = 200;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int woolyCowSpawnWeight = 14;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
        public int woolyCowMinGroupSize = 3;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
        public int woolyCowMaxGroupSize = 6;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int warpedCowSpawnWeight = 60;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
        public int warpedCowMinGroupSize = 2;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
        public int warpedCowMaxGroupSize = 4;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int waterBuffaloSpawnWeight = 8;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
        public int waterBuffaloMinGroupSize = 2;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
        public int waterBuffaloMaxGroupSize = 3;
    }

    public static class WeatherSettings {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int woodenCauldronRainFillChance = 5;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int woodenCauldronSnowFillChance = 10;
    }

    public static class CameraSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enableCameraFlash = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 32)
        public int cameraFlashRange = 8;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 20, max = 600)
        public int cameraFlashDuration = 100;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 20, max = 200)
        public int cameraTimerDuration = 60;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 64)
        public int cameraMaxPaper = 16;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 200)
        public int cameraCooldown = 20;
    }

    public static class ToolsSettings {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 20, max = 600)
        public int feltingNeedleUseDuration = 120;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 64)
        public int climbingRopeMaxLength = 12;
    }

    public static class ArmorSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enableFurSetBonus = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int furSetKnockbackResistance = 20;
    }

    public static class InfoTooltipSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean showFonduePotInfo = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showCheeseRackInfo = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showCookingPotInfo = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showMilkCanInfo = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showCheesePressInfo = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showCameraInfo = true;
    }

    public static class EffectsSettings {
        @ConfigEntry.Gui.CollapsibleObject
        public CompletionistBannerEffectSettings completionistBannerEffect = new CompletionistBannerEffectSettings();

        public static class CompletionistBannerEffectSettings {
            @ConfigEntry.Gui.Tooltip
            public boolean bannerGiveEffect = true;

            @ConfigEntry.Gui.Tooltip
            @ConfigEntry.BoundedDiscrete(min = 1, max = 32)
            public int bannerEffectRadius = 8;

            @ConfigEntry.Gui.Tooltip
            @ConfigEntry.BoundedDiscrete(min = 0, max = 4)
            public int bannerEffectAmplifier = 1;
        }
    }

    public static class MiscSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean animations = true;
    }

    public static class FoodSettings {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int cheesecakeSliceNutrition = 7;

        @ConfigEntry.Gui.Tooltip
        public float cheesecakeSliceSaturation = 0.8f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int cheeseTartSliceNutrition = 7;

        @ConfigEntry.Gui.Tooltip
        public float cheeseTartSliceSaturation = 0.8f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int cheeseSliceNutrition = 6;

        @ConfigEntry.Gui.Tooltip
        public float cheeseSliceSaturation = 0.8f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int sheepCheeseSliceNutrition = 6;

        @ConfigEntry.Gui.Tooltip
        public float sheepCheeseSliceSaturation = 0.8f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int grainCheeseSliceNutrition = 6;

        @ConfigEntry.Gui.Tooltip
        public float grainCheeseSliceSaturation = 0.8f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int amethystCheeseSliceNutrition = 6;

        @ConfigEntry.Gui.Tooltip
        public float amethystCheeseSliceSaturation = 0.8f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int buffaloCheeseSliceNutrition = 6;

        @ConfigEntry.Gui.Tooltip
        public float buffaloCheeseSliceSaturation = 0.8f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int goatCheeseSliceNutrition = 6;

        @ConfigEntry.Gui.Tooltip
        public float goatCheeseSliceSaturation = 0.8f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int warpedCheeseSliceNutrition = 6;

        @ConfigEntry.Gui.Tooltip
        public float warpedCheeseSliceSaturation = 0.8f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 72000)
        public int warpedCheeseSliceEffectDuration = 300;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int cheeseSandwichNutrition = 7;

        @ConfigEntry.Gui.Tooltip
        public float cheeseSandwichSaturation = 0.7f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int cheeseRollNutrition = 7;

        @ConfigEntry.Gui.Tooltip
        public float cheeseRollSaturation = 0.8f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int cheeseStickNutrition = 6;

        @ConfigEntry.Gui.Tooltip
        public float cheeseStickSaturation = 0.6f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int rawBuffaloMeatNutrition = 2;

        @ConfigEntry.Gui.Tooltip
        public float rawBuffaloMeatSaturation = 0.3f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int cookedBuffaloMeatNutrition = 8;

        @ConfigEntry.Gui.Tooltip
        public float cookedBuffaloMeatSaturation = 0.8f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int roastedBuffaloHamNutrition = 8;

        @ConfigEntry.Gui.Tooltip
        public float roastedBuffaloHamSaturation = 0.9f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 72000)
        public int roastedBuffaloHamEffectDuration = 600;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int sausageWithCheeseNutrition = 8;

        @ConfigEntry.Gui.Tooltip
        public float sausageWithCheeseSaturation = 0.9f;
    }

    @Override
    public void validatePostLoad() {
        food.cheesecakeSliceSaturation = Math.max(0.0f, Math.min(2.0f, food.cheesecakeSliceSaturation));
        food.cheeseTartSliceSaturation = Math.max(0.0f, Math.min(2.0f, food.cheeseTartSliceSaturation));
        food.cheeseSliceSaturation = Math.max(0.0f, Math.min(2.0f, food.cheeseSliceSaturation));
        food.sheepCheeseSliceSaturation = Math.max(0.0f, Math.min(2.0f, food.sheepCheeseSliceSaturation));
        food.grainCheeseSliceSaturation = Math.max(0.0f, Math.min(2.0f, food.grainCheeseSliceSaturation));
        food.amethystCheeseSliceSaturation = Math.max(0.0f, Math.min(2.0f, food.amethystCheeseSliceSaturation));
        food.buffaloCheeseSliceSaturation = Math.max(0.0f, Math.min(2.0f, food.buffaloCheeseSliceSaturation));
        food.goatCheeseSliceSaturation = Math.max(0.0f, Math.min(2.0f, food.goatCheeseSliceSaturation));
        food.warpedCheeseSliceSaturation = Math.max(0.0f, Math.min(2.0f, food.warpedCheeseSliceSaturation));
        food.cheeseSandwichSaturation = Math.max(0.0f, Math.min(2.0f, food.cheeseSandwichSaturation));
        food.cheeseRollSaturation = Math.max(0.0f, Math.min(2.0f, food.cheeseRollSaturation));
        food.cheeseStickSaturation = Math.max(0.0f, Math.min(2.0f, food.cheeseStickSaturation));
        food.rawBuffaloMeatSaturation = Math.max(0.0f, Math.min(2.0f, food.rawBuffaloMeatSaturation));
        food.cookedBuffaloMeatSaturation = Math.max(0.0f, Math.min(2.0f, food.cookedBuffaloMeatSaturation));
        food.roastedBuffaloHamSaturation = Math.max(0.0f, Math.min(2.0f, food.roastedBuffaloHamSaturation));
        food.sausageWithCheeseSaturation = Math.max(0.0f, Math.min(2.0f, food.sausageWithCheeseSaturation));
    }
}
