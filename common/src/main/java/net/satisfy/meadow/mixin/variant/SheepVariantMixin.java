package net.satisfy.meadow.mixin.variant;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.ServerLevelAccessor;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.entity.var.SheepVar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheep.class)
public abstract class SheepVariantMixin extends MobVariantMixin {

    @Override
    protected void onFinalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, SpawnGroupData spawnGroupData, CompoundTag compoundTag, CallbackInfoReturnable<SpawnGroupData> cir) {
        SheepVar.setVariant(getSheep(), SheepVar.getRandomVariant(serverLevelAccessor, getSheep().blockPosition(), mobSpawnType.equals(MobSpawnType.SPAWN_EGG)));
    }

    @Inject(
            method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/Sheep;",
            at = @At("HEAD"),
            cancellable = true)
    public void onGetBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob, CallbackInfoReturnable<Sheep> cir) {
        Sheep sheep = EntityType.SHEEP.create(serverLevel);
        if(sheep == null) return;

        RandomSource random = serverLevel.getRandom();
        SheepVar var = SheepVar.getVariant(getSheep());
        if(random.nextBoolean() && ageableMob instanceof Sheep varSheep){
            var = SheepVar.getVariant(varSheep);
        }
        SheepVar.setVariant(sheep, var);
        cir.setReturnValue(sheep);
    }

    @ModifyArg(
            method = "shear",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Sheep;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;I)Lnet/minecraft/world/entity/item/ItemEntity;"))
    public ItemLike getDrop(ItemLike itemLike) {
        if(itemLike.asItem().equals(Items.WHITE_WOOL)) return SheepVar.getVariant(getSheep()).getWool();
        return itemLike;
    }

    @Inject(
            method = "getDefaultLootTable",
            at = @At(value = "HEAD"), cancellable = true)
    public void getDefaultLootTable(CallbackInfoReturnable<ResourceLocation> cir) {
        Sheep sheep = getSheep();
        SheepVar var = SheepVar.getVariant(sheep);
        Item wool = var.getWool();
        if(sheep.isSheared() || wool.equals(Items.WHITE_WOOL) || !sheep.getColor().equals(DyeColor.WHITE)) return;

        ResourceLocation location = BuiltInRegistries.ITEM.getKey(wool);
        String s = location.getPath().replace("_wool", "");
        cir.setReturnValue(Meadow.identifier("entities/sheep/" + s));
    }

    @Unique
    private Sheep getSheep() {
        return (Sheep) (Object)this;
    }
}