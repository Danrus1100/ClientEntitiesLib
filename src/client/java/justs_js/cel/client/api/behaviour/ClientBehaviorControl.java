package justs_js.cel.client.api.behaviour;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;

public interface ClientBehaviorControl<E extends LivingEntity> extends BehaviorControl<E> {

    boolean tryStart(ClientLevel clientLevel, E livingEntity, long l);

    void tickOrStop(ClientLevel clientLevel, E livingEntity, long l);

    void doStop(ClientLevel clientLevel, E livingEntity, long l);
}
