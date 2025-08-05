package net.satisfy.meadow.core.item;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.satisfy.meadow.core.registry.RecipeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FeltingNeedleItem extends Item {
    private static final int USE_DURATION = 120;
    public FeltingNeedleItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND)
            return InteractionResultHolder.fail(player.getItemInHand(hand));

        ItemStack needle = player.getMainHandItem();
        ItemStack input = player.getOffhandItem();

        if (!canFelt(level, input))
            return InteractionResultHolder.fail(needle);

        ItemStack inputCopy = input.copy();
        ItemStack toFelt = inputCopy.split(1);

        needle.getOrCreateTag().put("Felting", toFelt.save(new CompoundTag()));
        player.setItemInHand(InteractionHand.OFF_HAND, inputCopy);
        player.startUsingItem(hand);

        return InteractionResultHolder.consume(needle);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (!(user instanceof ServerPlayer player))
            return stack;

        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("Felting")) {
            ItemStack input = ItemStack.of(tag.getCompound("Felting"));
            Optional<ItemStack> result = getFeltingResult(level, input);

            if (result.isPresent()) {
                player.getInventory().placeItemBackInInventory(result.get());
                spawnParticles(user.position(), result.get(), level);
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
            } else {
                player.getInventory().placeItemBackInInventory(input);
            }

            tag.remove("Felting");
        }

        return stack;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;

        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("Felting")) {
            ItemStack input = ItemStack.of(tag.getCompound("Felting"));
            player.getInventory().placeItemBackInInventory(input);
            tag.remove("Felting");
        }
    }

    private Optional<ItemStack> getFeltingResult(Level level, ItemStack input) {
        SimpleContainer container = new SimpleContainer(input);
        return level.getRecipeManager()
                .getRecipeFor(RecipeRegistry.FELTING.get(), container, level)
                .map(recipe -> recipe.assemble(container, level.registryAccess()));
    }

    private boolean canFelt(Level level, ItemStack input) {
        return getFeltingResult(level, input).isPresent();
    }

    private void spawnParticles(Vec3 pos, ItemStack stack, Level level) {
        for (int i = 0; i < 15; i++) {
            Vec3 motion = new Vec3(
                    (level.random.nextDouble() - 0.5) * 0.2,
                    (level.random.nextDouble() - 0.5) * 0.2,
                    (level.random.nextDouble() - 0.5) * 0.2
            );
            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack),
                    pos.x, pos.y + 1.0, pos.z, motion.x, motion.y, motion.z);
        }
    }
}
