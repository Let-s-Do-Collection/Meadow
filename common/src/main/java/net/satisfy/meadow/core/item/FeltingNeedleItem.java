package net.satisfy.meadow.core.item;

import com.mojang.serialization.DataResult;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.satisfy.meadow.core.registry.RecipeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FeltingNeedleItem extends Item {
    private static final int USE_DURATION = 120;
    private static final String FELTING_KEY = "Felting";

    public FeltingNeedleItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USE_DURATION;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResultHolder.fail(player.getItemInHand(hand));
        ItemStack needle = player.getMainHandItem();
        ItemStack input = player.getOffhandItem();
        if (!canFelt(level, input)) return InteractionResultHolder.fail(needle);
        ItemStack inputCopy = input.copy();
        ItemStack toFelt = inputCopy.split(1);
        CompoundTag root = getOrCreateCustomData(needle);
        DataResult<Tag> enc = ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, toFelt);
        Tag tag = enc.result().orElseGet(CompoundTag::new);
        if (tag instanceof CompoundTag ct) {
            root.put(FELTING_KEY, ct);
            setCustomData(needle, root);
        }
        player.setItemInHand(InteractionHand.OFF_HAND, inputCopy);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(needle);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (!(user instanceof ServerPlayer player)) return stack;
        CompoundTag root = getCustomData(stack);
        if (root != null && root.contains(FELTING_KEY)) {
            CompoundTag stored = root.getCompound(FELTING_KEY);
            Optional<ItemStack> inputOpt = ItemStack.CODEC.parse(NbtOps.INSTANCE, stored).result();
            if (inputOpt.isPresent()) {
                ItemStack input = inputOpt.get();
                Optional<ItemStack> result = getFeltingResult(level, input);
                if (result.isPresent()) {
                    player.getInventory().placeItemBackInInventory(result.get());
                    spawnParticles(user.position(), result.get(), level);
                    stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                } else {
                    player.getInventory().placeItemBackInInventory(input);
                }
            }
            root.remove(FELTING_KEY);
            if (root.isEmpty()) removeCustomData(stack); else setCustomData(stack, root);
        }
        return stack;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;
        CompoundTag root = getCustomData(stack);
        if (root != null && root.contains(FELTING_KEY)) {
            CompoundTag stored = root.getCompound(FELTING_KEY);
            Optional<ItemStack> inputOpt = ItemStack.CODEC.parse(NbtOps.INSTANCE, stored).result();
            inputOpt.ifPresent(s -> player.getInventory().placeItemBackInInventory(s));
            root.remove(FELTING_KEY);
            if (root.isEmpty()) removeCustomData(stack); else setCustomData(stack, root);
        }
    }

    private Optional<ItemStack> getFeltingResult(Level level, ItemStack input) {
        SingleRecipeInput in = new SingleRecipeInput(input);
        return level.getRecipeManager().getRecipeFor(RecipeRegistry.FELTING.get(), in, level)
                .map(h -> h.value().assemble(in, level.registryAccess()));
    }

    private boolean canFelt(Level level, ItemStack input) {
        return getFeltingResult(level, input).isPresent();
    }

    private void spawnParticles(Vec3 pos, ItemStack stack, Level level) {
        for (int i = 0; i < 15; i++) {
            Vec3 m = new Vec3((level.random.nextDouble() - 0.5) * 0.2, (level.random.nextDouble() - 0.5) * 0.2, (level.random.nextDouble() - 0.5) * 0.2);
            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), pos.x, pos.y + 1.0, pos.z, m.x, m.y, m.z);
        }
    }

    private static CompoundTag getCustomData(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? null : data.copyTag();
    }

    private static CompoundTag getOrCreateCustomData(ItemStack stack) {
        CompoundTag tag = getCustomData(stack);
        return tag == null ? new CompoundTag() : tag;
    }

    private static void setCustomData(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static void removeCustomData(ItemStack stack) {
        stack.remove(DataComponents.CUSTOM_DATA);
    }
}
