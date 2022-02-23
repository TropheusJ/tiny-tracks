package io.github.tropheusj.tiny_tracks.train.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.github.tropheusj.tiny_tracks.train.TrainEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

import java.util.ArrayList;
import java.util.List;

public abstract class TrainModel extends EntityModel<TrainEntity> {
	protected final ModelPart root;
	protected final List<ModelPart> renderables = new ArrayList<>();

	public TrainModel(ModelPart root) {
		this.root = root;
	}

	public void render(TrainEntity entity, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		renderables.forEach(r -> r.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha));
	}

	@Override
	public void setupAnim(TrainEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
}
