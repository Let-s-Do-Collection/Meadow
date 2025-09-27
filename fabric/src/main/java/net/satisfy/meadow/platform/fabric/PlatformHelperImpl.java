package net.satisfy.meadow.platform.fabric;

import com.google.gson.JsonObject;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.fabric.core.config.MeadowFabricConfig;
import net.satisfy.meadow.platform.PlatformHelper;

import java.util.function.Supplier;

public class PlatformHelperImpl extends PlatformHelper {
    public static <T extends Entity> Supplier<EntityType<T>> registerBoatType(String name, EntityType.EntityFactory<T> factory, MobCategory category, float width, float height, int clientTrackingRange) {
        EntityType<T> registry = Registry.register(BuiltInRegistries.ENTITY_TYPE, Meadow.identifier(name), FabricEntityTypeBuilder.create(category, factory).dimensions(EntityDimensions.scalable(width, height)).trackRangeChunks(clientTrackingRange).build());
        return () -> registry;
    }

    public static boolean shouldGiveEffect() {
        MeadowFabricConfig config = AutoConfig.getConfigHolder(MeadowFabricConfig.class).getConfig();
        return config.items.banner.giveEffect;
    }

    public static boolean shouldShowTooltip() {
        MeadowFabricConfig config = AutoConfig.getConfigHolder(MeadowFabricConfig.class).getConfig();
        return config.items.banner.isShowTooltipEnabled();
    }

    public static <T extends Recipe<?>> T fromJson(ResourceLocation recipeId, JsonObject json, HolderLookup.Provider provider) {
        if (!"conditional".equals(recipeId.getNamespace())) {
            throw new UnsupportedOperationException("All Meadow conditional recipes must use the 'conditional' namespace. Invalid recipe: " + recipeId);
        }
        RecipeHolder<?> holder = RecipeManager.fromJson(recipeId, GsonHelper.getAsJsonObject(json, "recipe"), provider);
        return (T) holder.value();
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
