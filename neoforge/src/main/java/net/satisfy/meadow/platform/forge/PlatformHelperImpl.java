package net.satisfy.meadow.platform.forge;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.forge.core.config.MeadowForgeConfig;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class PlatformHelperImpl {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Meadow.MOD_ID);

    public static <T extends Entity> Supplier<EntityType<T>> registerBoatType(String name, EntityType.EntityFactory<T> factory, MobCategory category, float width, float height, int clientTrackingRange) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(factory, category).sized(width, height).build(name));
    }

    public static boolean shouldGiveEffect() {
        return MeadowForgeConfig.GIVE_EFFECT.get();
    }

    public static boolean shouldShowTooltip() {
        return MeadowForgeConfig.GIVE_EFFECT.get() && MeadowForgeConfig.SHOW_TOOLTIP.get();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> T fromJson(ResourceLocation recipeId, JsonObject json) {
        JsonObject recipe = GsonHelper.getAsJsonObject(json, "recipe");
        JsonArray conditions = GsonHelper.getAsJsonArray(json, "conditions");
        JsonObject forgeRecipe = new JsonObject();
        forgeRecipe.addProperty("type", "forge:conditional");
        JsonArray recipes = new JsonArray();
        JsonObject newRecipe = new JsonObject();
        newRecipe.add("conditions", conditions);
        newRecipe.add("recipe", recipe);
        recipes.add(newRecipe);
        forgeRecipe.add("recipes", recipes);
        return (T) ConditionalRecipe.SERIALZIER.fromJson(recipeId, forgeRecipe);
    }

    public static boolean isModLoaded(String modid) {
        ModList modList = ModList.get();
        if(modList != null){
            return modList.isLoaded(modid);
        }
        return isModPreLoaded(modid);
    }

    public static boolean isModPreLoaded(String modid) {
        return getPreLoadedModInfo(modid) != null;
    }

    public static @Nullable ModInfo getPreLoadedModInfo(String modId){
        for(ModInfo info : LoadingModList.get().getMods()){
            if(info.getModId().equals(modId)) {
                return info;
            }
        }
        return null;
    }
}
