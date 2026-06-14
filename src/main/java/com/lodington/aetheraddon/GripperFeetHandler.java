package com.lodington.aetheraddon;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = AetherAddon.MOD_ID)
public class GripperFeetHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (!player.hasEffect(ModEffects.GRIPPER_FEET)) return;

        int amplifier = player.getEffect(ModEffects.GRIPPER_FEET).getAmplifier();

        // Wall climbing: works on both client and server
        if (player.horizontalCollision && !player.onGround()) {
            Vec3 motion = player.getDeltaMovement();
            // If player is pressing into a wall, let them climb
            double climbSpeed = 0.2;
            player.setDeltaMovement(motion.x, climbSpeed, motion.z);
            player.fallDistance = 0;
            // Mark as on climbable so the game doesn't fight us
            player.resetFallDistance();
        }

        // Apply speed and jump boost on server side only
        if (!player.level().isClientSide()) {
            if (!player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 5, amplifier, true, false));
            }
            if (!player.hasEffect(MobEffects.JUMP)) {
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 5, amplifier, true, false));
            }
        }
    }
}
