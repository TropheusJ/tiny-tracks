package io.github.tropheusj.tiny_tracks.track.connection;

import net.minecraft.network.FriendlyByteBuf;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a segment of a track running from one connection to another.
 */
public record TrackSegment(TrackConnection connectionOne, TrackConnection connectionTwo) {
	/**
	 * @return The TrackConnection of this segment that connects to the other segment.
	 */
	@Nullable
	public TrackConnection getConnection(TrackSegment other) {
		if (connectionOne.connectingPoint(other.connectionOne) != null) {
			return connectionOne;
		} else if (connectionTwo.connectingPoint(other.connectionTwo) != null) {
			return connectionTwo;
		}
		return null;
	}

	public boolean hasConnection(TrackConnection connection) {
		return connectionOne.canConnect(connection) || connectionTwo.canConnect(connection);
	}

	public boolean canConnect(TrackSegment segment) {
		return getConnection(segment) != null;
	}

	public void writeToBuf(FriendlyByteBuf buf) {
		connectionOne.writeToBuf(buf);
		connectionTwo.writeToBuf(buf);
	}

	public static TrackSegment readFromBuf(FriendlyByteBuf buf) {
		TrackConnection one = TrackConnection.get(buf);
		TrackConnection two = TrackConnection.get(buf);
		return new TrackSegment(one, two);
	}
}
