package net.satisfy.meadow.core.item;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.satisfy.meadow.core.registry.ObjectRegistry;

import java.util.Map;
import java.util.function.Supplier;

public class CompletionistBannerItem extends StandingAndWallBlockItem {
    private static final Map<Item, Pair<ResourceLocation, Supplier<MobEffectInstance>>> STANDARD_BY_ITEM = Maps.newHashMap();
    public CompletionistBannerItem(Properties properties, ResourceLocation texture, Supplier<MobEffectInstance> effectSupplier) {
        super(ObjectRegistry.MEADOW_BANNER.get(), ObjectRegistry.MEADOW_WALL_BANNER.get(), properties, Direction.DOWN);
        STANDARD_BY_ITEM.put(this, new Pair<>(texture, effectSupplier));
    }

    public static Pair<ResourceLocation, Supplier<MobEffectInstance>> getPair(Item item){
        return STANDARD_BY_ITEM.get(item);
    }

    public static ResourceLocation getLocationOrNull(Item item){
        Pair<ResourceLocation, Supplier<MobEffectInstance>> pair = STANDARD_BY_ITEM.get(item);
        if(pair == null) return null;
        return pair.getFirst();
    }

    public static MobEffectInstance getEffectInstanceOrNull(Item item){
        Pair<ResourceLocation, Supplier<MobEffectInstance>> pair = STANDARD_BY_ITEM.get(item);
        if(pair == null) return null;
        return pair.getSecond().get();
    }

    @Override
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }
}