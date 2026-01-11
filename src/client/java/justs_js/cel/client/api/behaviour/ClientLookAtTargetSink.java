package justs_js.cel.client.api.behaviour;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class ClientLookAtTargetSink extends ClientBehavior<Mob> {
    public ClientLookAtTargetSink(int i, int j) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_PRESENT), i, j);
    }

    @Override
    protected boolean canStillUse(ClientLevel clientLevel, Mob livingEntity, long l) {
        return livingEntity.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).filter((positionTracker) -> positionTracker.isVisibleBy(livingEntity)).isPresent();
    }

    @Override
    protected void stop(ClientLevel clientLevel, Mob livingEntity, long l) {
        livingEntity.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
    }

    @Override
    protected void tick(ClientLevel clientLevel, Mob livingEntity, long l) {
        livingEntity.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).ifPresent((positionTracker) -> {
            livingEntity.getLookControl().setLookAt(positionTracker.currentPosition());
        });
    }
}
