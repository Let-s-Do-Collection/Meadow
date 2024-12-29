package net.satisfy.meadow.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.satisfy.meadow.Meadow;

public class SoundRegistry {
    public static final Registrar<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Meadow.MOD_ID, Registries.SOUND_EVENT).getRegistrar();
    public static final RegistrySupplier<SoundEvent> WOODCUTTER = create("woodcutter");
    public static final RegistrySupplier<SoundEvent> FUR = create("fur");
    public static final RegistrySupplier<SoundEvent> CLICK_CAMERA = create("click_camera");
    public static final RegistrySupplier<SoundEvent> BUFFALO_AMBIENT = create("buffalo_ambient");
    public static final RegistrySupplier<SoundEvent> BUFFALO_HURT = create("buffalo_hurt");
    public static final RegistrySupplier<SoundEvent> BUFFALO_DEATH = create("buffalo_death");

    private static RegistrySupplier<SoundEvent> create(String name) {
        final ResourceLocation id = Meadow.identifier(name);
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void init() {
        Meadow.LOGGER.debug("Registering Sounds for " + Meadow.MOD_ID);
    }
}
