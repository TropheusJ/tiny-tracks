package io.github.tropheusj.tiny_tracks.track.network;

import com.google.common.collect.Multimap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.Tesselator;

import com.mojang.blaze3d.vertex.VertexFormat;

import io.github.tropheusj.tiny_tracks.TinyTracks;
import io.github.tropheusj.tiny_tracks.track.connection.TrackSegment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Map.Entry;

@Environment(EnvType.CLIENT)
public class TrackNetworkDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
	private final Minecraft mc = Minecraft.getInstance();

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, double camX, double camY, double camZ) {
		if (!mc.options.renderDebug) {
			return;
		}
		ClientLevel level = mc.level;
		TrackNetworkManager manager = TrackNetworkManager.ALL.get(level);
		if (manager != null) {
			for (TrackNetwork network : manager.networks) {
				Multimap<BlockPos, TrackSegment> segments = network.segments;
				setupRS(network.r, network.g, network.b);
				for (Entry<BlockPos, TrackSegment> entry : segments.entries()) {
					TrackSegment segment = entry.getValue();
					BlockPos pos = entry.getKey();
					renderSegment(segment, pos, network.r, network.g, network.b);
				}
				finishRS();
			}
		} else {
			TinyTracks.LOGGER.warn("client has no track network manager!");
		}
	}

	public static void renderSegment(TrackSegment segment, BlockPos pos, float r, float g, float b) {
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		if (!camera.isInitialized()) return;
		Vec3 offset = camera.getPosition().reverse();

		Vec3 pos1 = segment.connectionOne.pos.add(0, 0.3, 0);
		AABB connection1Box = AABB.ofSize(pos1, 0.1, 0.1, 0.1).move(offset).move(pos);
		DebugRenderer.renderFilledBox(connection1Box, 1, 1, 1, 1);

		Vec3 pos2 = segment.connectionTwo.pos.add(0, 0.3, 0);
		AABB connection2Box = AABB.ofSize(pos2, 0.1, 0.1, 0.1).move(offset).move(pos);
		DebugRenderer.renderFilledBox(connection2Box, 1, 1, 1, 1);

		drawLine(connection1Box.getCenter(), connection2Box.getCenter(), r, g, b);
	}

	public static void drawLine(Vec3 start, Vec3 end, float r, float g, float b) {
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tesselator.getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferBuilder.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

		bufferBuilder.vertex(start.x, start.y, start.z).color(r, g, b, 1).endVertex();
		bufferBuilder.vertex(end.x, end.y, end.z).color(r, g, b, 1).endVertex();
		tesselator.end();
	}

	private static void setupRS(float r, float g, float b) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(r, g, b, 0.4f);
		RenderSystem.disableTexture();
		RenderSystem.lineWidth(6.0f);
	}

	private static void finishRS() {
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
}
