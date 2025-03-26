package net.satisfy.meadow.core.block;

import com.mojang.serialization.MapCodec;
import de.cristelknight.doapi.common.block.CabinetBlock;
import de.cristelknight.doapi.common.registry.DoApiSoundEventRegistry;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

public class ShelfBlock extends CabinetBlock {
    public static final MapCodec<ShelfBlock> CODEC = simpleCodec(ShelfBlock::new);

    public ShelfBlock() {
        super(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), DoApiSoundEventRegistry.CABINET_OPEN, DoApiSoundEventRegistry.CABINET_CLOSE);
    }

    public ShelfBlock(Properties settings) {
        super(settings, DoApiSoundEventRegistry.CABINET_OPEN, DoApiSoundEventRegistry.CABINET_CLOSE);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}