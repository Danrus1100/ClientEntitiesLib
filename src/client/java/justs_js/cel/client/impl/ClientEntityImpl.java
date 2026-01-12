package justs_js.cel.client.impl;

import justs_js.cel.client.api.ClientEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ClientEntityImpl extends ClientEntity {

    public ClientEntityImpl(EntityType<? extends ClientEntity> entityEntityType, Level level) {
        super(entityEntityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        this.level().addParticle(
                ParticleTypes.FIREWORK,
                this.getX(),
                this.getEyeY(),
                this.getZ(),
                0d, 0d, 0d
        );
    }
}
