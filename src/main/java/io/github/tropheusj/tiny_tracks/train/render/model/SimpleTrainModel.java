package io.github.tropheusj.tiny_tracks.train.render.model;

import io.github.tropheusj.tiny_tracks.train.TrainEntity;
import net.minecraft.client.model.geom.ModelPart;

/**
 * A simple TrainModel implementation.
 * Designed for use with JsonEM
 * Has two parts - main, or body, and wheels
 * @see TrainModels
 */
public class SimpleTrainModel extends TrainModel {
	private final ModelPart wheels;

	public SimpleTrainModel(ModelPart root) {
		super(root);
		renderables.add(root.getChild("main"));
		wheels = root.getChild("wheels");
		renderables.add(wheels);
	}

	@Override
	public void setupAnim(TrainEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		wheels.zRot = ageInTicks;
	}
}
