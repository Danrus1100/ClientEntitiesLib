package justs_js.cel.client.api.sensor;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientPlayerSensor extends ClientSensor<LivingEntity> {

    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYERS);
    }

    public void doTick(ClientLevel clientLevel, LivingEntity livingEntity) {
        Stream<AbstractClientPlayer> var10000 = clientLevel.players().stream()
                .filter(EntitySelector.NO_SPECTATORS)
                .filter(
                        (serverPlayer) -> livingEntity.closerThan(
                                serverPlayer,
                                this.getFollowDistance(livingEntity)
                        )
                );
        Objects.requireNonNull(livingEntity);
        List<Player> list = var10000.sorted(Comparator.comparingDouble(livingEntity::distanceToSqr)).collect(Collectors.toList());
        Brain<?> brain = livingEntity.getBrain();
        brain.setMemory(MemoryModuleType.NEAREST_PLAYERS, list);
        List<Player> list2 = list.stream()
                .filter(
                        (player) -> isEntityTargetable(
                                clientLevel,
                                livingEntity,
                                player
                        )
                ).toList();
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, list2.isEmpty() ? null : list2.getFirst());
        List<Player> list3 = list2.stream().filter((player) -> isEntityAttackable(clientLevel, livingEntity, player)).toList();
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYERS, list3);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, list3.isEmpty() ? null : list3.getFirst());
    }

    protected double getFollowDistance(LivingEntity livingEntity) {
        return livingEntity.getAttributeValue(Attributes.FOLLOW_RANGE);
    }
}
