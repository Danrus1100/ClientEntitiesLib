package justs_js.cel.client.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Predicate;

@Mixin(NearestVisibleLivingEntities.class)
public interface NearestVisibleLivingEntitiesAccessor {
    @Mutable
    @Accessor("lineOfSightTest")
    void cel$setLineOfSightTest(Predicate<LivingEntity> newTest);
}
