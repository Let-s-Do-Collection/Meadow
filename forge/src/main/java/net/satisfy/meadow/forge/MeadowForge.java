package net.satisfy.meadow.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.registry.CompostableRegistry;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.forge.core.config.MeadowForgeConfig;
import net.satisfy.meadow.forge.core.registry.MeadowForgeVillagers;
import net.satisfy.meadow.platform.forge.PlatformHelperImpl;

@Mod(Meadow.MOD_ID)
public class MeadowForge {
    public MeadowForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Meadow.MOD_ID, modEventBus);
        Meadow.init();
        PlatformHelperImpl.ENTITY_TYPES.register(modEventBus);
        MeadowForgeVillagers.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        MeadowForgeConfig.loadConfig(MeadowForgeConfig.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("meadow.toml").toString());

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CompostableRegistry::registerCompostable);
        Meadow.commonSetup();
    }

    @SubscribeEvent
    public static void spawnEvent(SpawnPlacementRegisterEvent event) {
        event.register(EntityTypeRegistry.WOOLY_COW.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(EntityTypeRegistry.WATER_BUFFALO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(EntityTypeRegistry.WOOLY_SHEEP.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
    }
}
