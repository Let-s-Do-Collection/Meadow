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
        return MeadowNeoForgeConfig.GIVE_EFFECT.get();
    }

    public static boolean shouldShowTooltip() {
        return MeadowNeoForgeConfig.GIVE_EFFECT.get() && MeadowNeoForgeConfig.SHOW_TOOLTIP.get();
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
