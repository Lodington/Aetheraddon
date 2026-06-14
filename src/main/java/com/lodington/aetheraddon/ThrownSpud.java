package com.lodington.aetheraddon;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownSpud extends ThrowableItemProjectile {

    public static final ResourceKey<DamageType> THROWN_SPUD_DAMAGE = ResourceKey.create(
            Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(AetherAddon.MOD_ID, "thrown_spud"));

    public ThrownSpud(EntityType<? extends ThrownSpud> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownSpud(Level level, LivingEntity shooter) {
        super(ModEntities.THROWN_SPUD.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.RAW_SPUD.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        DamageSource source = new DamageSource(
                this.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(THROWN_SPUD_DAMAGE),
                this, this.getOwner());
        result.getEntity().hurt(source, 2.0f);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide()) {
            this.discard();
        }
    }
}
