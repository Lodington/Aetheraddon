package com.lodington.aetheraddon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SpudRatRenderer extends EntityRenderer<SpudRatEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AetherAddon.MOD_ID, "textures/entity/spud_rat.png");

    public SpudRatRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(SpudRatEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0, 0.15, 0.0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        vertex(consumer, matrix, normal, packedLight, -0.5f, -0.5f, 0, 1);
        vertex(consumer, matrix, normal, packedLight, 0.5f, -0.5f, 1, 1);
        vertex(consumer, matrix, normal, packedLight, 0.5f, 0.5f, 1, 0);
        vertex(consumer, matrix, normal, packedLight, -0.5f, 0.5f, 0, 0);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal,
                               int light, float x, float y, float u, float v) {
        consumer.addVertex(matrix, x, y, 0.0f)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0.0f, 1.0f, 0.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(SpudRatEntity entity) {
        return TEXTURE;
    }
}
