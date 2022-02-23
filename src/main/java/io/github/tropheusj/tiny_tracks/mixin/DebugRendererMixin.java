package io.github.tropheusj.tiny_tracks.mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.tropheusj.tiny_tracks.track.network.TrackNetworkDebugRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
	@Unique
	private final TrackNetworkDebugRenderer tracks$networkRenderer = new TrackNetworkDebugRenderer();

	@Inject(method = "render", at = @At("HEAD"))
	private void tracks$render(PoseStack poseStack, BufferSource bufferSource, double camX, double camY, double camZ, CallbackInfo ci) {
		tracks$networkRenderer.render(poseStack, bufferSource, camX, camY, camZ);
	}

	@Inject(method = "clear", at = @At("HEAD"))
	private void tracks$clear(CallbackInfo ci) {
		tracks$networkRenderer.clear();
	}
}
