package com.exemplo.transformacao.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

/** Desenha a armadura (armadura.obj) por cima dos jogadores que estão transformados. */
public class TransformationArmorLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public TransformationArmorLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (player.isInvisible() || !ClientState.isTransformed(player.getUUID())) return;

        ArmorObjModel model = ArmorObjModel.get();
        if (model == null) return;

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(ArmorObjModel.TEXTURE));
        int overlay = LivingEntityRenderer.getOverlayCoords(player, 0.0F);

        // A armadura "se monta" no jogador logo após a transformação:
        // começa menor e sobe suavemente até o tamanho normal.
        float age = player.tickCount + partialTick;
        float progress = ClientState.transformProgress(player, age);
        float eased = progress * progress * (3F - 2F * progress);
        float scale = Mth.lerp(eased, 0.72F, 1.0F);

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.5F + (1.0F - eased) * 0.08F, 0.0F);
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0.0F, -0.5F, 0.0F);
        model.render(poseStack, vc, packedLight, overlay, getParentModel());
        poseStack.popPose();
    }
}
