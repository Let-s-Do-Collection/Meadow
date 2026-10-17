package net.satisfy.meadow.core.cooking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;
import java.util.Optional;

public record CookingLiquid(int color, boolean biomeTint, ResourceLocation texture, List<Fill> fill) {
    public static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.withDefaultNamespace("block/water_still");

    public static final Codec<Integer> COLOR_CODEC = Codec.STRING.xmap(
            text -> 0xFF000000 | Integer.parseInt(text.startsWith("#") ? text.substring(1) : text, 16),
            color -> String.format("#%06X", color & 0xFFFFFF));

    public static final Codec<CookingLiquid> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            COLOR_CODEC.optionalFieldOf("color", 0xFF3F76E4).forGetter(CookingLiquid::color),
            Codec.BOOL.optionalFieldOf("biome_tint", false).forGetter(CookingLiquid::biomeTint),
            ResourceLocation.CODEC.optionalFieldOf("texture", DEFAULT_TEXTURE).forGetter(CookingLiquid::texture),
            Fill.CODEC.listOf().fieldOf("fill").forGetter(CookingLiquid::fill)
    ).apply(instance, CookingLiquid::new));

    public Optional<Fill> fillFor(ItemStack stack) {
        return fill.stream().filter(entry -> entry.matches(stack)).findFirst();
    }

    public record Fill(Optional<Holder<Item>> item, Optional<Holder<Potion>> potion, int amount, Optional<Holder<Item>> remainder) {
        public static final Codec<Fill> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ITEM.holderByNameCodec().optionalFieldOf("item").forGetter(Fill::item),
                BuiltInRegistries.POTION.holderByNameCodec().optionalFieldOf("potion").forGetter(Fill::potion),
                Codec.intRange(1, CookingLiquids.CAPACITY).fieldOf("amount").forGetter(Fill::amount),
                BuiltInRegistries.ITEM.holderByNameCodec().optionalFieldOf("remainder").forGetter(Fill::remainder)
        ).apply(instance, Fill::new));

        public boolean matches(ItemStack stack) {
            if (potion.isPresent()) {
                PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
                return contents != null && contents.is(potion.get()) && item.map(stack::is).orElse(true);
            }
            return item.isPresent() && stack.is(item.get());
        }

        public ItemStack remainderStack() {
            return remainder.map(holder -> new ItemStack(holder.value())).orElse(ItemStack.EMPTY);
        }
    }
}
