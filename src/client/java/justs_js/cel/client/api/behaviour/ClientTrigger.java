package justs_js.cel.client.api.behaviour;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;

public interface ClientTrigger<E extends LivingEntity> {
    boolean trigger(ClientLevel serverLevel, E livingEntity, long l);
}
