package com.lodington.aetheraddon;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, AetherAddon.MOD_ID);

    public static final Supplier<EntityType<ThrownSpud>> THROWN_SPUD = ENTITIES.register("thrown_spud",
            () -> EntityType.Builder.<ThrownSpud>of(ThrownSpud::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("thrown_spud"));

    public static final Supplier<EntityType<SpudRatEntity>> SPUD_RAT = ENTITIES.register("spud_rat",
            () -> EntityType.Builder.of(SpudRatEntity::new, MobCategory.CREATURE)
                    .sized(0.4f, 0.3f)
                    .clientTrackingRange(8)
                    .build("spud_rat"));
}
