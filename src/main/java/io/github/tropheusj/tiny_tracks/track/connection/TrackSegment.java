package io.github.tropheusj.tiny_tracks.track.connection;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import io.github.tropheusj.tiny_tracks.track.TrackBlock;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Represents a segment of a track running from one connection to another.
 * This is also effectively an enum.
 */
public class TrackSegment {
	private static final Table<TrackConnection, TrackConnection, TrackSegment> TABLE = HashBasedTable.create(24, 24);
	public static final TrackSegment[] VALUES = new TrackSegment[24 * 24];

	public final TrackConnection connectionOne;
	public final TrackConnection connectionTwo;
	public final Direction[] directions;
	public final TrackConnection[] connections;
	public final int ordinal;

	private TrackSegment(TrackConnection connectionOne, TrackConnection connectionTwo, int ordinal) {
		this.connectionOne = connectionOne;
		this.connectionTwo = connectionTwo;
		connections = new TrackConnection[] { connectionOne, connectionTwo };
		directions = new Direction[] { connectionOne.face, connectionTwo.face };
		this.ordinal = ordinal;
	}

	static {
		int ordinal = 0;
		for (TrackConnection one : TrackConnection.VALUES) {
			for (TrackConnection two : TrackConnection.VALUES) {
				TrackSegment s = new TrackSegment(one, two, ordinal);
				TABLE.put(one, two, s);
				VALUES[ordinal] = s;
				ordinal++;
			}
		}
	}

	public static TrackSegment get(TrackConnection one, TrackConnection two) {
		return TABLE.get(one, two);
	}

	public static TrackSegment get(int ordinal) {
		return VALUES[ordinal];
	}

	public static TrackSegment get(FriendlyByteBuf buf) {
		int ordinal = buf.readVarInt();
		return get(ordinal);
	}

	/**
	 * @return a Multimap mapping each adjacent BlockPos to a List of TrackSegments
	 */
	public Multimap<BlockPos, TrackSegment> getConnections(BlockPos pos, Level level) {
		Multimap<BlockPos, TrackSegment> segments = HashMultimap.create();
		for (Direction d : this.directions) {
			BlockPos connectedPos = pos.relative(d);
			BlockState connectedState = level.getBlockState(connectedPos);
			if (connectedState.getBlock() instanceof TrackBlock adjacentTrackBlock) {
				List<TrackSegment> adjacentSegments = adjacentTrackBlock.getTrackSegments(connectedPos, connectedState, level);
				for (TrackSegment segment : adjacentSegments) {
					if (this.canConnect(segment)) {
						segments.put(connectedPos, segment);
					}
				}
			}
		}
		return segments;
	}

	/**
	 * @return The TrackConnection of this segment that connects to the other segment. Should be symmetrical.
	 */
	@Nullable
	public TrackConnection getConnection(TrackSegment other) {
		for (TrackConnection connection : connections) {
			for (TrackConnection otherConnection : other.connections) {
				if (connection.canConnect(otherConnection)) {
					return connection;
				}
			}
		}
		return null;
	}

	public boolean hasConnection(TrackConnection connection) {
		return connectionOne.canConnect(connection) || connectionTwo.canConnect(connection);
	}

	public boolean canConnect(TrackSegment segment) {
		return getConnection(segment) != null;
	}

	public FriendlyByteBuf toBuf() {
		return PacketByteBufs.create().writeVarInt(ordinal);
	}

	public static TrackSegment fromBuf(FriendlyByteBuf buf) {
		int index = buf.readVarInt();
		return get(index);
	}

	@Override
	public boolean equals(Object o) {
		return this == o;
	}

	@Override
	public int hashCode() {
		return Objects.hash(connectionOne, connectionTwo, ordinal);
	}
}
