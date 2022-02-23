package io.github.tropheusj.tiny_tracks.train;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.tropheusj.tiny_tracks.train.render.model.TrainModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * A Carriage Type is... a type of carriage. A CarriageType defines the
 * behavior of a carriage.
 */
public class CarriageType {
	public void tick(TrainEntity train) {
	}

	public void readData(TrainEntity train, CompoundTag data) {
	}

	public void readData(TrainEntity train, FriendlyByteBuf data) {
	}

	public void writeData(TrainEntity train, CompoundTag data) {
	}

	public void writeData(TrainEntity train, FriendlyByteBuf data) {
	}

	@Environment(EnvType.CLIENT)
	public void render(TrainEntity entity, TrainModel model, float entityYaw, float partialTicks,
					   PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
	}
}
