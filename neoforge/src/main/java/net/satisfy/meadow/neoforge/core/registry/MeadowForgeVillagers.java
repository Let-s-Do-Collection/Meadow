package net.satisfy.meadow.neoforge.core.registry;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.registry.ObjectRegistry;

public class MeadowForgeVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, Meadow.MOD_ID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(Registries.VILLAGER_PROFESSION, Meadow.MOD_ID);

    public static final DeferredHolder<PoiType, PoiType> HERMIT_POI = POI_TYPES.register("hermit_poi", () ->
            new PoiType(ImmutableSet.copyOf(ObjectRegistry.WOODCUTTER.get().getStateDefinition().getPossibleStates()), 1, 1));

    public static final DeferredHolder<VillagerProfession, VillagerProfession> HERMIT = VILLAGER_PROFESSIONS.register("hermit", () ->
            new VillagerProfession("hermit", x -> x.value() == HERMIT_POI.get(), x -> x.value() == HERMIT_POI.get(), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_SHEPHERD));

    public static final DeferredHolder<PoiType, PoiType> CHEESEMAKER_POI = POI_TYPES.register("cheesemaker_poi", () ->
            new PoiType(ImmutableSet.copyOf(ObjectRegistry.PINE_CHEESE_RACK.get().getStateDefinition().getPossibleStates()), 1, 1));

    public static final DeferredHolder<VillagerProfession, VillagerProfession> CHEESEMAKER = VILLAGER_PROFESSIONS.register("cheesemaker", () ->
            new VillagerProfession("cheesemaker", x -> x.value() == CHEESEMAKER_POI.get(), x -> x.value() == CHEESEMAKER_POI.get(), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_BUTCHER));

    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }
}
