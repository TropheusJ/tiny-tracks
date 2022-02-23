package io.github.tropheusj.tiny_tracks.track.network;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.tropheusj.tiny_tracks.TinyTracks;
import io.github.tropheusj.tiny_tracks.track.connection.WorldlyTrackSegment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer.SimpleDebugRenderer;

@Environment(EnvType.CLIENT)
public class TrackNetworkDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, double camX, double camY, double camZ) {
		ClientLevel level = Minecraft.getInstance().level;
		TrackNetworkManager manager = TrackNetworkManager.ALL.get(level);
		if (manager != null) {
			for (TrackNetwork network : manager.networks) {
//				for (WorldlyTrackSegment segment : network.tracks) {
//					setupRS();
//					DebugRenderer.renderFilledBox(segment.pos(), -0.4f, 1, 1, 1, 0.5f);
//					finishRS();
//				}
			}
		} else {
			TinyTracks.LOGGER.warn("client has no track network manager!");
		}
	}

	private void setupRS() {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(0.0F, 1.0F, 0.0F, 0.75F);
		RenderSystem.disableTexture();
		RenderSystem.lineWidth(6.0F);
	}

	private void finishRS() {
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	@Override
	public void clear() {
		SimpleDebugRenderer.super.clear();
	}
}
