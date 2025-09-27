package net.satisfy.meadow.forge.core.registry;

import com.google.common.collect.ImmutableSet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.registry.ObjectRegistry;

public class MeadowForgeVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, Meadow.MOD_ID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, Meadow.MOD_ID);

    public static final RegistryObject<PoiType> HERMIT_POI = POI_TYPES.register("hermit_poi", () ->
            new PoiType(ImmutableSet.copyOf(ObjectRegistry.WOODCUTTER.get().getStateDefinition().getPossibleStates()), 1, 1));

    public static final RegistryObject<VillagerProfession> HERMIT = VILLAGER_PROFESSIONS.register("hermit", () ->
            new VillagerProfession("hermit", x -> x.get() == HERMIT_POI.get(), x -> x.get() == HERMIT_POI.get(), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_SHEPHERD));

    public static final RegistryObject<PoiType> CHEESEMAKER_POI = POI_TYPES.register("cheesemaker_poi", () ->
            new PoiType(ImmutableSet.copyOf(ObjectRegistry.CHEESE_RACK.get().getStateDefinition().getPossibleStates()), 1, 1));

    public static final RegistryObject<VillagerProfession> CHEESEMAKER = VILLAGER_PROFESSIONS.register("cheesemaker", () ->
            new VillagerProfession("cheesemaker", x -> x.get() == CHEESEMAKER_POI.get(), x -> x.get() == CHEESEMAKER_POI.get(), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_BUTCHER));

    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }
}
