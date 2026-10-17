package net.satisfy.meadow.core.cooking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.satisfy.meadow.Meadow;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class CookingLiquids extends SimpleJsonResourceReloadListener {
    public static final int CAPACITY = 4;
    public static final ResourceLocation WATER = Meadow.identifier("water");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    public static final CookingLiquids INSTANCE = new CookingLiquids();

    private Map<ResourceLocation, CookingLiquid> liquids = Map.of();

    private CookingLiquids() {
        super(GSON, "meadow/cooking_liquid");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, CookingLiquid> loaded = new LinkedHashMap<>();
        entries.forEach((id, json) -> CookingLiquid.CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> LOGGER.warn("[{}] Skipping cooking liquid '{}': {}", Meadow.MOD_ID, id, error))
                .ifPresent(liquid -> loaded.put(id, liquid)));
        liquids = Map.copyOf(loaded);
    }

    public static Component name(ResourceLocation id) {
        return Component.translatable("liquid." + id.getNamespace() + "." + id.getPath());
    }

    public static Component describe(ResourceLocation id, int amount) {
        return Component.translatable("hud.meadow.cooking_pot.liquid_amount", name(id), amount);
    }

    public static @Nullable CookingLiquid get(ResourceLocation id) {
        return INSTANCE.liquids.get(id);
    }

    public static Optional<Map.Entry<ResourceLocation, CookingLiquid.Fill>> findFill(ItemStack stack) {
        for (Map.Entry<ResourceLocation, CookingLiquid> entry : INSTANCE.liquids.entrySet()) {
            Optional<CookingLiquid.Fill> fill = entry.getValue().fillFor(stack);
            if (fill.isPresent()) {
                return Optional.of(Map.entry(entry.getKey(), fill.get()));
            }
        }
        return Optional.empty();
    }
}
