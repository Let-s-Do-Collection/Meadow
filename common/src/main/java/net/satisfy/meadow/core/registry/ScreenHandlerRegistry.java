package net.satisfy.meadow.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.gui.handler.WoodcutterGuiHandler;

import java.util.function.Supplier;

public class ScreenHandlerRegistry {
    public static final DeferredRegister<MenuType<?>> SCREEN_TYPES = DeferredRegister.create(Meadow.MOD_ID, Registries.MENU);

    public static final RegistrySupplier<MenuType<WoodcutterGuiHandler>> WOODCUTTER_SCREEN_HANDLER = create("woodcutter", () -> new MenuType<>(WoodcutterGuiHandler::new, FeatureFlags.VANILLA_SET));

    public static void init() {
        SCREEN_TYPES.register();
    }

    private static <T extends MenuType<?>> RegistrySupplier<T> create(String name, Supplier<T> type) {
        return SCREEN_TYPES.register(name, type);
    }
}
