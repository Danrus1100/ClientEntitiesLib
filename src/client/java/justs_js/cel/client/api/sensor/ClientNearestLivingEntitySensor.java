package justs_js.cel.client.api.sensor;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import justs_js.cel.client.mixin.NearestVisibleLivingEntitiesAccessor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ClientNearestLivingEntitySensor<T extends LivingEntity> extends ClientSensor<T> {

    public void doTick(ClientLevel clientLevel, T livingEntity) {
        double d = livingEntity.getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB aABB = livingEntity.getBoundingBox().inflate(d, d, d);
        List<LivingEntity> list = clientLevel.getEntitiesOfClass(
                LivingEntity.class,
                aABB,
                (livingEntity2) -> livingEntity2 != livingEntity && livingEntity2.isAlive()
        );
        Objects.requireNonNull(livingEntity);
        list.sort(Comparator.comparingDouble(livingEntity::distanceToSqr));
        Brain<?> brain = livingEntity.getBrain();
        brain.setMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES, list);
        NearestVisibleLivingEntities nearestVisibleLivingEntities = new NearestVisibleLivingEntities(null, livingEntity, list);
        Object2BooleanOpenHashMap<LivingEntity> object2BooleanOpenHashMap = new Object2BooleanOpenHashMap<>(list.size());
        ((NearestVisibleLivingEntitiesAccessor)nearestVisibleLivingEntities).cel$setLineOfSightTest(
                (livingEntityx) -> object2BooleanOpenHashMap.computeIfAbsent(
                        livingEntityx,
                        (livingEntity2) -> isEntityTargetable(clientLevel, livingEntity, (LivingEntity) livingEntity2)
                )
        );
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, nearestVisibleLivingEntities);
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }
}
