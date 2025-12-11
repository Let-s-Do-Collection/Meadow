package net.satisfy.meadow.core.item;

import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;
import net.satisfy.meadow.core.entity.WoolyCowEntity;
import net.satisfy.meadow.core.entity.WoolyCowVariant;

public class CattleSpawnEggItem extends ArchitecturySpawnEggItem {
    private final int variantId;

    public CattleSpawnEggItem(RegistrySupplier<? extends EntityType<? extends Animal>> type, int primary, int secondary, Item.Properties properties, int variantId) {
        super(type, primary, secondary, properties);
        this.variantId = variantId;
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        EntityType<?> entityType = getType(stack);

        CompoundTag entityTag = new CompoundTag();
        if (entityType != null) {
            entityTag.putString("id", EntityType.getKey(entityType).toString());
        }
        entityTag.putInt("Variant", variantId);

        CompoundTag root = new CompoundTag();
        root.put("EntityTag", entityTag);
        stack.set(DataComponents.ENTITY_DATA, CustomData.of(root));
        return stack;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        if (!(ctx.getLevel() instanceof ServerLevel server)) return super.useOn(ctx);

        Direction face = ctx.getClickedFace();
        BlockPos spawnPos = ctx.getClickedPos().relative(face);
        EntityType<?> type = getType(ctx.getItemInHand());
        Entity e = type.create(server);
        if (e == null) return InteractionResult.PASS;

        e.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, server.random.nextFloat() * 360f, 0f);

        if (e instanceof WoolyCowEntity cow) {
            cow.setVariant(WoolyCowVariant.byId(variantId));
        }

        if (e instanceof Mob mob) {
            DifficultyInstance diff = server.getCurrentDifficultyAt(spawnPos);
            mob.finalizeSpawn(server, diff, MobSpawnType.SPAWN_EGG, null);
        }

        server.addFreshEntity(e);

        Player p = ctx.getPlayer();
        if (p == null || !p.getAbilities().instabuild) {
            ItemStack inHand = ctx.getItemInHand();
            inHand.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}
