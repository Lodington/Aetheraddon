package com.lodington.aetheraddon;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;

import javax.annotation.Nullable;

public class SpudRatEntity extends TamableAnimal {

    private int oreScanCooldown = 0;

    public SpudRatEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                player -> !this.isTame()));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    public void tick() {
        super.tick();

        // Ore sniffing: when riding a player, scan for nearby Chudington Ore
        if (!this.level().isClientSide() && this.isPassenger() && this.getVehicle() instanceof Player player) {
            oreScanCooldown--;
            if (oreScanCooldown <= 0) {
                oreScanCooldown = 40; // Scan every 2 seconds
                scanForOre(player);
            }
        }
    }

    private void scanForOre(Player player) {
        BlockPos center = player.blockPosition();
        int radius = 8;
        BlockPos nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    if (this.level().getBlockState(pos).is(ModBlocks.CHUDINGTON_ORE.get())) {
                        double dist = center.distSqr(pos);
                        if (dist < nearestDist) {
                            nearestDist = dist;
                            nearest = pos;
                        }
                    }
                }
            }
        }

        if (nearest != null && this.level() instanceof ServerLevel serverLevel) {
            // Spawn particles at the ore location to guide the player
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    nearest.getX() + 0.5, nearest.getY() + 1.0, nearest.getZ() + 0.5,
                    5, 0.3, 0.3, 0.3, 0.0);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame() && this.isOwnedBy(player)) {
            // Heal with Spud if damaged
            if (stack.is(ModItems.SPUD.get()) && this.getHealth() < this.getMaxHealth()) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.heal(4.0f);
                this.level().broadcastEntityEvent(this, (byte) 7);
                return InteractionResult.SUCCESS;
            }

            // Shift+right-click: ride on player's head
            if (player.isShiftKeyDown()) {
                if (this.isPassenger()) {
                    // Dismount
                    this.stopRiding();
                } else {
                    // Mount on player
                    this.startRiding(player, true);
                    // Grant "Ratatouille" advancement
                    if (player instanceof ServerPlayer serverPlayer) {
                        AdvancementHolder advancement = serverPlayer.server.getAdvancements()
                                .get(ResourceLocation.fromNamespaceAndPath(AetherAddon.MOD_ID, "rat_on_head"));
                        if (advancement != null) {
                            AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
                            if (!progress.isDone()) {
                                for (String criterion : progress.getRemainingCriteria()) {
                                    serverPlayer.getAdvancements().award(advancement, criterion);
                                }
                            }
                        }
                    }
                }
                return InteractionResult.SUCCESS;
            }

            // Normal right-click: toggle sit/follow
            this.setOrderedToSit(!this.isOrderedToSit());
            return InteractionResult.SUCCESS;
        } else if (!this.isTame()) {
            // Tame with Spud
            if (stack.is(ModItems.SPUD.get())) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                if (!this.level().isClientSide()) {
                    if (this.random.nextInt(3) == 0) {
                        this.tame(player);
                        this.setOrderedToSit(true);
                        this.level().broadcastEntityEvent(this, (byte) 7);

                        if (player instanceof ServerPlayer serverPlayer) {
                            AdvancementHolder advancement = serverPlayer.server.getAdvancements()
                                    .get(ResourceLocation.fromNamespaceAndPath(AetherAddon.MOD_ID, "best_friends_forever"));
                            if (advancement != null) {
                                AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
                                if (!progress.isDone()) {
                                    for (String criterion : progress.getRemainingCriteria()) {
                                        serverPlayer.getAdvancements().award(advancement, criterion);
                                    }
                                }
                            }
                        }
                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 6);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean canRiderInteract() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Invulnerable while riding on player's head
        if (this.isPassenger() && this.getVehicle() instanceof Player) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModItems.SPUD.get());
    }

    @Override
    protected void dropAllDeathLoot(ServerLevel serverLevel, DamageSource source) {
        super.dropAllDeathLoot(serverLevel, source);
        if (!this.isTame()) {
            int count = 1 + this.random.nextInt(2);
            this.spawnAtLocation(new ItemStack(ModItems.TOE_JAM.get(), count));
        }
    }
}
