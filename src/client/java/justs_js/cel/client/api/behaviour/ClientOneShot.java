package justs_js.cel.client.api.behaviour;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import org.jetbrains.annotations.NotNull;

public abstract class ClientOneShot<E extends LivingEntity> implements ClientBehaviorControl<E>, ClientTrigger<E> {
    private Behavior.Status status;

    public ClientOneShot() {
        this.status = Behavior.Status.STOPPED;
    }

    @Override
    public boolean tryStart(ServerLevel serverLevel, E livingEntity, long l) {return false;}

    @Override
    public void tickOrStop(ServerLevel serverLevel, E livingEntity, long l) {}

    @Override
    public void doStop(ServerLevel serverLevel, E livingEntity, long l) {}

    public final Behavior.@NotNull Status getStatus() {
        return this.status;
    }

    public final boolean tryStart(ClientLevel clientLevel, E livingEntity, long l) {
        if (this.trigger(clientLevel, livingEntity, l)) {
            this.status = Behavior.Status.RUNNING;
            return true;
        } else {
            return false;
        }
    }

    public final void tickOrStop(ClientLevel serverLevel, E livingEntity, long l) {
        this.doStop(serverLevel, livingEntity, l);
    }

    public final void doStop(ClientLevel serverLevel, E livingEntity, long l) {
        this.status = Behavior.Status.STOPPED;
    }

    public @NotNull String debugString() {
        return this.getClass().getSimpleName();
    }
}
