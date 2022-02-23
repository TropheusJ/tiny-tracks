package io.github.tropheusj.tiny_tracks.track.connection;

import io.github.tropheusj.tiny_tracks.track.TrackBlock;
import io.github.tropheusj.tiny_tracks.track.network.TrackNetwork;
import io.github.tropheusj.tiny_tracks.track.network.TrackNetworkManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.core.Direction.Plane.HORIZONTAL;

/**
 * Represents a track segment in-world.
 */
public record WorldlyTrackSegment(TrackSegment segment, Level level, BlockPos pos) {
	public WorldlyTrackSegment(TrackSegment segment, Level level, BlockPos pos) {
		this.segment = segment;
		this.level = level;
		this.pos = pos.immutable();
	}

	public BlockState getState() {
		return level.getBlockState(pos);
	}

	public boolean valid() {
		return getState() instanceof TrackBlock;
	}

	public boolean canConnect(WorldlyTrackSegment other) {
		if (pos.distManhattan(other.pos) == 1) { // if two segments are within adjacent blocks
			return segment.canConnect(other.segment);
		}
		return false;
	}

	public boolean hasConnection(TrackConnection connection) {
		TrackConnection one = segment.connectionOne();
		TrackConnection two = segment.connectionTwo();
		return one.equals(connection) || two.equals(connection);
	}

	public List<WorldlyTrackSegment> findConnections() {
		List<WorldlyTrackSegment> connections = new ArrayList<>();
		for (Direction d : HORIZONTAL) {
			BlockPos offset = pos.relative(d);
			BlockState state = level.getBlockState(offset);
			if (state.getBlock() instanceof TrackBlock track) {
				List<WorldlyTrackSegment> segments = track.getTrackSegments(level, offset, state);
				for (WorldlyTrackSegment segment : segments) {
					if (segment.canConnect(this)) {
						connections.add(segment);
					}
				}
			}
		}
		return connections;
	}

	@Nullable
	public TrackNetwork getNetwork() {
		TrackNetworkManager manager = TrackNetworkManager.ALL.get(level);
		if (manager != null) {
			return manager.get(this);
		}
		TrackNetworkManager.warnMissing(level);
		return null;
	}

	public FriendlyByteBuf toBuf() {
		FriendlyByteBuf buf = PacketByteBufs.create();
		segment.writeToBuf(buf);
		buf.writeBlockPos(pos);
		return buf;
	}

	public static WorldlyTrackSegment readFromBuf(FriendlyByteBuf buf, Level level) {
		TrackSegment segment = TrackSegment.readFromBuf(buf);
		BlockPos pos = buf.readBlockPos();
		return new WorldlyTrackSegment(segment, level, pos);
	}
}
