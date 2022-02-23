package io.github.tropheusj.tiny_tracks.train.render;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.math.Vector3f;

import io.github.tropheusj.tiny_tracks.train.TrainEntity;
import io.github.tropheusj.tiny_tracks.train.render.model.TrainModel;
import io.github.tropheusj.tiny_tracks.train.render.model.TrainModels;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class TrainEntityRenderer extends EntityRenderer<TrainEntity> {
	private TrainModel model = null;
	private ResourceLocation texture = null;

	public TrainEntityRenderer(Context context) {
		super(context);
	}

	@Override
	public void render(TrainEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
		if (model == null || texture == null)
			initAssets(entity);

		matrixStack.pushPose();
		matrixStack.translate(0, 0.4, 0);
		matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
		model.render(
				entity,
				matrixStack,
				buffer.getBuffer(
						RenderType.entityCutoutNoCull(texture)
				),
				packedLight,
				OverlayTexture.NO_OVERLAY,
				1,
				1,
				1,
				1
		);
		matrixStack.popPose();
		if (entity.carriage != null) {
			entity.carriage.render(entity, model, entityYaw, partialTicks, matrixStack, buffer, packedLight);
		}

		super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
	}

	private void initAssets(TrainEntity entity) {
		texture = entity.getTexture();
		model = TrainModels.get(entity.getModel()).getModel();
	}

	@Override
	public ResourceLocation getTextureLocation(TrainEntity entity) {
		if (texture == null)
			initAssets(entity);
		return texture;
	}
}
