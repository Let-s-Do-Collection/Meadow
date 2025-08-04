package net.satisfy.meadow.core.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.TagRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.satisfy.meadow.core.registry.ObjectRegistry.*;

public class WoolySheepEntity extends Animal implements Shearable {
    private static final EntityDataAccessor<Byte> DATA_WOOL_TYPE = SynchedEntityData.defineId(WoolySheepEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(WoolySheepEntity.class, EntityDataSerializers.INT);
    private EatBlockGoal eatBlockGoal;
    private int eatAnimationTick;

    public WoolySheepEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.setWoolType((byte) 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0).add(Attributes.MOVEMENT_SPEED, 0.23000000417232513);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_WOOL_TYPE, (byte) 0);
        this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag c) {
        super.addAdditionalSaveData(c);
        c.putBoolean("Sheared", this.isSheared());
        c.putByte("WoolType", this.getWoolType());
        c.putInt("Variant", getTypeVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag c) {
        super.readAdditionalSaveData(c);
        this.setSheared(c.getBoolean("Sheared"));
        this.setWoolType(c.getByte("WoolType"));
        setTypeVariant(c.getInt("Variant"));
    }

    @Override
    protected void registerGoals() {
        this.eatBlockGoal = new EatBlockGoal(this);
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, Ingredient.of(Items.WHEAT), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, this.eatBlockGoal);
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public void aiStep() {
        if (this.level().isClientSide) {
            this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
        }
        super.aiStep();
    }

    @Override
    public void customServerAiStep() {
        this.eatAnimationTick = this.eatBlockGoal.getEatAnimationTick();
        super.customServerAiStep();
    }

    @Override
    public void ate() {
        super.ate();
        this.setSheared(false);
    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b == 10) {
            this.eatAnimationTick = 40;
        } else {
            super.handleEntityEvent(b);
        }
    }

    public float getHeadEatPositionScale(float f) {
        if (this.eatAnimationTick <= 0) {
            return 0.0F;
        } else if (this.eatAnimationTick >= 4 && this.eatAnimationTick <= 36) {
            return 1.0F;
        } else {
            return this.eatAnimationTick < 4 ? ((float) this.eatAnimationTick - f) / 4.0F : -((float) (this.eatAnimationTick - 40) - f) / 4.0F;
        }
    }

    public float getHeadEatAngleScale(float f) {
        if (this.eatAnimationTick > 4 && this.eatAnimationTick <= 36) {
            float g = ((float) (this.eatAnimationTick - 4) - f) / 32.0F;
            return 0.62831855F + 0.21991149F * Mth.sin(g * 28.7F);
        } else {
            return this.eatAnimationTick > 0 ? 0.62831855F : this.getXRot() * 0.017453292F;
        }
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(TagRegistry.SHEARS)) {
            if (!this.level().isClientSide && this.readyForShearing()) {
                this.shear(SoundSource.PLAYERS);
                this.gameEvent(GameEvent.SHEAR, player);
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void shear(SoundSource source) {
        if (!this.level().isClientSide && this.readyForShearing()) {
            this.level().playSound(null, this, SoundEvents.SHEEP_SHEAR, source, 1.0F, 1.0F);
            this.setSheared(true);
            WoolySheepVar variant = this.getVariant();
            int count = 1 + this.random.nextInt(3);
            for (int i = 0; i < count; i++) {
                Item woolItem = getWoolItemByVariant(variant);
                ItemStack woolStack = new ItemStack(woolItem);
                ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY() + 1.0D, this.getZ(), woolStack);
                itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add(
                        (this.random.nextFloat() - this.random.nextFloat()) * 0.1F,
                        this.random.nextFloat() * 0.05F,
                        (this.random.nextFloat() - this.random.nextFloat()) * 0.1F));
                this.level().addFreshEntity(itemEntity);
            }
        }
    }

    public static Item getWoolItemByVariant(WoolySheepVar variant) {
        return switch (variant) {
            case FLECKED -> FLECKED_WOOL.get().asItem();
            case PATCHED -> PATCHED_WOOL.get().asItem();
            case ROCKY -> ROCKY_WOOL.get().asItem();
            case INKY -> INKY_WOOL.get().asItem();
            case FUZZY -> Items.WHITE_WOOL;
            case LONG_NOSED -> HIGHLAND_WOOL.get().asItem();
        };
    }

    @Nullable
    @Override
    public WoolySheepEntity getBreedOffspring(ServerLevel level, AgeableMob mob) {
        WoolySheepEntity sheep = EntityTypeRegistry.WOOLY_SHEEP.get().create(level);
        if (sheep != null) {
            RandomSource random = level.getRandom();
            WoolySheepVar var = this.getVariant();
            if (random.nextBoolean() && mob instanceof WoolySheepEntity varSheep) {
                var = varSheep.getVariant();
            }
            sheep.setVariant(var);
        }
        return sheep;
    }

    @Override
    public @NotNull SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        WoolySheepVar variant;
        if (spawnGroupData instanceof ShearableVarSheepGroupData data) {
            variant = data.variant;
        } else {
            variant = WoolySheepVar.getRandomVariant(levelAccessor, this.blockPosition(), mobSpawnType.equals(MobSpawnType.SPAWN_EGG));
            spawnGroupData = new ShearableVarSheepGroupData(variant);
        }
        setVariant(variant);
        return Objects.requireNonNull(super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag));
    }

    public void setVariant(WoolySheepVar variant) {
        setTypeVariant(variant.getId() & 255 | this.getTypeVariant() & -256);
    }

    public @NotNull WoolySheepVar getVariant() {
        return WoolySheepVar.byId(getTypeVariant() & 255);
    }

    private void setTypeVariant(int i) {
        entityData.set(DATA_ID_TYPE_VARIANT, i);
    }

    private int getTypeVariant() {
        return entityData.get(DATA_ID_TYPE_VARIANT);
    }

    public static class ShearableVarSheepGroupData extends AgeableMob.AgeableMobGroupData {
        public final WoolySheepVar variant;

        public ShearableVarSheepGroupData(WoolySheepVar variant) {
            super(true);
            this.variant = variant;
        }
    }

    public byte getWoolType() {
        return (byte) (this.entityData.get(DATA_WOOL_TYPE) & 0x0F);
    }

    public void setWoolType(byte woolType) {
        this.entityData.set(DATA_WOOL_TYPE, woolType);
    }

    public boolean isSheared() {
        return (this.entityData.get(DATA_WOOL_TYPE) & 16) != 0;
    }

    public void setSheared(boolean bl) {
        byte b = this.entityData.get(DATA_WOOL_TYPE);
        if (bl) {
            this.entityData.set(DATA_WOOL_TYPE, (byte) (b | 16));
        } else {
            this.entityData.set(DATA_WOOL_TYPE, (byte) (b & -17));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SHEEP_AMBIENT;
    }

    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.SHEEP_STEP, 0.15F, 1.0F);
    }

    @Override
    protected boolean shouldDropLoot() {
        return true;
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull DamageSource source, int lootingMultiplier, boolean recentlyHit) {
        this.dropFromLootTable(source, recentlyHit);
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.SHEEP_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SHEEP_DEATH;
    }


    public boolean readyForShearing() {
        return this.isAlive() && !this.isSheared() && !this.isBaby();
    }
}
