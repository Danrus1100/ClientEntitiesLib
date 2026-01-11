package justs_js.cel.client.api.behaviour;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.List;
import java.util.Map;

public class ClientRunOne<E extends LivingEntity> extends ClientGateBehavior<E> {
    public ClientRunOne(List<Pair<? extends ClientBehaviorControl<? super E>, Integer>> list) {
        this(ImmutableMap.of(), list);
    }
    public ClientRunOne(Map<MemoryModuleType<?>, MemoryStatus> map, List<Pair<? extends ClientBehaviorControl<? super E>, Integer>> list) {
        super(map, ImmutableSet.of(), OrderPolicy.SHUFFLED, RunningPolicy.RUN_ONE, list);
    }
}
