package justs_js.cel.client.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import justs_js.cel.client.api.behaviour.ClientBehaviorControl;
import justs_js.cel.client.api.sensor.ClientSensor;
import justs_js.cel.client.api.sensor.ClientSensorType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public class ClientBrain<E extends ClientEntity> extends Brain<E> {
    public ClientBrain(Collection<? extends MemoryModuleType<?>> collection, Collection<? extends ClientSensorType<? extends ClientSensor<? super E>>> collection2, ImmutableList<Brain.MemoryValue<?>> immutableList, Supplier<Codec<Brain<E>>> supplier) {
        super(collection, collection2, immutableList, supplier);
        Iterator var5 = collection2.iterator();

        while(var5.hasNext()) {
            ClientSensorType<? extends ClientSensor<? super E>> sensorType = (ClientSensorType<? extends ClientSensor<? super E>>)var5.next();
            this.sensors.put(sensorType, sensorType.create());
        }

        var5 = this.sensors.values().iterator();

        while(var5.hasNext()) {
            Sensor<? super E> sensor = (Sensor)var5.next();
            Iterator var7 = sensor.requires().iterator();

            while(var7.hasNext()) {
                MemoryModuleType<?> memoryModuleType2 = (MemoryModuleType)var7.next();
                this.getMemories().put(memoryModuleType2, Optional.empty());
            }
        }

        UnmodifiableIterator var9 = immutableList.iterator();

        while(var9.hasNext()) {
            MemoryValue<?> memoryValue = (MemoryValue)var9.next();
            memoryValue.setMemoryInternal(this);
        }
    }

    public void stopAll(ClientLevel clientLevel, E livingEntity) {
        long l = clientLevel.getGameTime();
        Iterator var5 = this.getRunningBehaviors().iterator();

        while(var5.hasNext()) {
            ClientBehaviorControl<? super E> behaviorControl = (ClientBehaviorControl<? super E>) var5.next();
            behaviorControl.doStop(clientLevel, livingEntity, l);
        }
    }

    public static <E extends ClientEntity> ClientBrain.Provider<E> clientProvider(Collection<? extends MemoryModuleType<?>> collection, Collection<? extends ClientSensorType<? extends ClientSensor<? super E>>> collection2) {
        return new ClientBrain.Provider<>(collection, collection2);
    }

    private final Map<ClientSensorType<? extends ClientSensor<? super E>>, ClientSensor<? super E>> sensors = Maps.newLinkedHashMap();

    public void tick(ClientLevel clientLevel, E livingEntity) {
        this.forgetOutdatedMemories();
        this.tickSensors(clientLevel, livingEntity);
        this.startEachNonRunningBehavior(clientLevel, livingEntity);
        this.tickEachRunningBehavior(clientLevel, livingEntity);
    }

    private void tickEachRunningBehavior(ClientLevel clientLevel, E livingEntity) {
        long l = clientLevel.getGameTime();
        Iterator var5 = this.getRunningBehaviors().iterator();

        while(var5.hasNext()) {
            BehaviorControl<? super E> behaviorControl = (BehaviorControl)var5.next();
            ((ClientBehaviorControl<? super E>)behaviorControl).tickOrStop(clientLevel, livingEntity, l);
        }
    }

    private void tickSensors(ClientLevel clientLevel, E livingEntity) {
        for (ClientSensor<? super E> sensor : this.sensors.values()) {
            sensor.tick(clientLevel, livingEntity);
        }
    }

    private void startEachNonRunningBehavior(ClientLevel clientLevel, E livingEntity) {
        long l = clientLevel.getGameTime();
        Iterator var5 = this.availableBehaviorsByPriority.values().iterator();

        label34:
        while(var5.hasNext()) {
            Map<Activity, Set<BehaviorControl<? super E>>> map = (Map)var5.next();
            Iterator var7 = map.entrySet().iterator();

            while(true) {
                Map.Entry entry;
                Activity activity;
                do {
                    if (!var7.hasNext()) {
                        continue label34;
                    }

                    entry = (Map.Entry)var7.next();
                    activity = (Activity)entry.getKey();
                } while(!this.getActiveActivities().contains(activity));

                Set<BehaviorControl<? super E>> set = (Set)entry.getValue();
                Iterator var11 = set.iterator();

                while(var11.hasNext()) {
                    BehaviorControl<? super E> behaviorControl = (BehaviorControl)var11.next();
                    if (behaviorControl.getStatus() == Behavior.Status.STOPPED) {
                        ((ClientBehaviorControl<? super E>)behaviorControl).tryStart(clientLevel, livingEntity, l);
                    }
                }
            }
        }

    }

    @Override
    public @NotNull ClientBrain<E> copyWithoutBehaviors() {
        ClientBrain<E> brain = new ClientBrain(this.getMemories().keySet(), this.sensors.keySet(), ImmutableList.of(), codec);
        Iterator var2 = this.getMemories().entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> entry = (Map.Entry)var2.next();
            MemoryModuleType<?> memoryModuleType = (MemoryModuleType)entry.getKey();
            if (((Optional)entry.getValue()).isPresent()) {
                brain.getMemories().put(memoryModuleType, (Optional)entry.getValue());
            }
        }

        return brain;
    }

    public static final class Provider<E extends ClientEntity> {
        private final Collection<? extends MemoryModuleType<?>> memoryTypes;
        private final Collection<? extends ClientSensorType<? extends ClientSensor<? super E>>> sensorTypes;
        private final Codec<Brain<E>> codec;

        Provider(Collection<? extends MemoryModuleType<?>> collection, Collection<? extends ClientSensorType<? extends ClientSensor<? super E>>> collection2) {
            this.memoryTypes = collection;
            this.sensorTypes = collection2;
            this.codec = Brain.codec(collection, collection2);
        }

        public ClientBrain<E> makeBrain(Dynamic<?> dynamic) {
            return new ClientBrain<>(this.memoryTypes, this.sensorTypes, ImmutableList.of(), () -> this.codec);
        }
    }
}
